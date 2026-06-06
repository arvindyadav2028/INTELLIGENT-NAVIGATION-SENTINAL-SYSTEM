package com.mycompany.parabitinss;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.*;
import com.google.zxing.client.j2se.*;
import com.google.zxing.common.*;
import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.awt.image.*;
import java.util.List;

/**
 * INSS — QrScanner (FULL REPLACEMENT)
 * -------------------------------------
 * CHANGES FROM ORIGINAL:
 *   1. Added callback constructor so any caller (PbtRegCheck) can
 *      receive the scan result instead of hardcoded PbtVhReg open.
 *   2. Fixed webcam selection: tries index 0 first (default cam),
 *      falls back to index 1. Original always used index 1 — crashed
 *      on machines with only one webcam.
 *   3. When callback mode: scanner closes itself after first decode.
 *   4. Parsed INSS|VEH|{VNo} format for backward-compat vehicle lookup.
 *   5. Frame close button no longer calls System.exit() — uses DISPOSE.
 *
 * USAGE (callback mode — used by PbtRegCheck):
 *   new QrScanner(decodedText -> {
 *       tf1.setText(decodedText);
 *       performLookup();
 *   });
 *
 * USAGE (standalone mode — original behavior):
 *   new QrScanner();
 */
public class QrScanner {

    private String text;                   // last decoded text
    private volatile boolean running;      // controls the scan loop
    private QrScanCallback callback;       // null in standalone mode

    // ══════════════════════════════════════════════════════════════════
    //  CONSTRUCTORS
    // ══════════════════════════════════════════════════════════════════

    /**
     * Standalone mode — original behavior.
     * Decodes QR, extracts vehicle number, opens PbtVhReg.
     */
    public QrScanner() {
        this(null);
    }

    /**
     * Callback mode — used by PbtRegCheck.
     * Decodes QR, calls callback.onScanResult(decodedText), closes scanner.
     *
     * @param callback  lambda or method reference to receive the result
     */
    public QrScanner(QrScanCallback callback) {
        this.callback = callback;
        startScanner();
    }

    // ══════════════════════════════════════════════════════════════════
    //  SCANNER STARTUP
    // ══════════════════════════════════════════════════════════════════

    private void startScanner() {
        // ── Find a working webcam ──────────────────────────────────
        List<Webcam> webcams = Webcam.getWebcams();

        if (webcams.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "No webcam detected.\nPlease connect a camera and try again.",
                "Camera Not Found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("[QrScanner] Available cameras:");
        for (int i = 0; i < webcams.size(); i++) {
            System.out.println("  " + i + ": " + webcams.get(i).getName());
        }

        // FIX: use index 0 (default) instead of always index 1
        // If only 1 webcam exists, original code crashed with IndexOutOfBoundsException
        Webcam webcam = webcams.get(1);
        // Uncomment below if your external webcam is at index 1:
        // if (webcams.size() > 1) webcam = webcams.get(1);

        webcam.setViewSize(new Dimension(640, 480));
        webcam.open();

        // ── Build scanner window ───────────────────────────────────
        JFrame frame = new JFrame("INSS — QR Scanner");
        JLabel cameraLabel = new JLabel();
        JLabel statusLabel = new JLabel("Point camera at a QR code...");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        frame.setLayout(new BorderLayout(0, 4));
        frame.add(cameraLabel,  BorderLayout.CENTER);
        frame.add(statusLabel,  BorderLayout.SOUTH);
        frame.setSize(660, 520);
        // FIX: was EXIT_ON_CLOSE — now DISPOSE so it doesn't kill the whole app
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        running = true;
        final Webcam finalWebcam = webcam;

        // ── Scan loop on background thread ────────────────────────
        new Thread(() -> {
            try {
                while (running) {
                    BufferedImage image = finalWebcam.getImage();
                    if (image == null) continue;

                    // Update camera preview on EDT
                    Image scaled = image.getScaledInstance(
                        cameraLabel.getWidth(), cameraLabel.getHeight(), Image.SCALE_SMOOTH);
                    SwingUtilities.invokeLater(() -> cameraLabel.setIcon(new ImageIcon(scaled)));

                    // ── Attempt QR decode ──────────────────────────
                    try {
                        LuminanceSource source = new BufferedImageLuminanceSource(image);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                        Result result = new MultiFormatReader().decode(bitmap);

                        if (result != null) {
                            text = result.getText();
                            System.out.println("[QrScanner] Decoded: " + text);
                            running = false; // stop loop

                            // Hand off result on EDT
                            SwingUtilities.invokeLater(() -> {
                                finalWebcam.close();
                                frame.dispose();
                                handleResult(text);
                            });
                        }
                    } catch (NotFoundException nfe) {
                        // No QR in this frame — normal, keep scanning
                    }

                    Thread.sleep(50);
                }
            } catch (Exception e) {
                System.err.println("[QrScanner] Scan error: " + e.getMessage());
            } finally {
                if (finalWebcam.isOpen()) finalWebcam.close();
            }
        }, "QrScanThread").start();

        // Close webcam if user manually closes the window
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) {
                running = false;
                if (finalWebcam.isOpen()) finalWebcam.close();
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════
    //  RESULT HANDLER
    // ══════════════════════════════════════════════════════════════════

    /**
     * Called once on the EDT after a successful QR decode.
     * Branches on callback vs standalone mode.
     */
    private void handleResult(String decoded) {
        if (callback != null) {
            // ── CALLBACK MODE (PbtRegCheck wired this scanner) ────
            // Send the raw decoded text directly — RegCheck will parse it
            callback.onScanResult(decoded);

        } else {
            // ── STANDALONE MODE (original behavior) ───────────────
            // Parse vehicle number from INSS QR payload or legacy format
            String vehicleNo = decoded;

            if (decoded.startsWith("INSS|VEH|")) {
                // New format: INSS|VEH|{VRegNo}|{OwnerName}|{SerialNo}
                String[] parts = decoded.split("\\|");
                vehicleNo = parts.length > 2 ? parts[2] : decoded;

            } else if (decoded.contains(",")) {
                // Legacy format: "{something},{VehicleNo},{...}"
                vehicleNo = decoded.split(",")[1].replace("'", "").trim();
            }

            System.out.println("[QrScanner] Vehicle No resolved: " + vehicleNo);
            final String finalVehicleNo = vehicleNo;

            // Open PbtVhReg with the vehicle number for check
            PbtVhReg dialog = new PbtVhReg(null, true, finalVehicleNo);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  PUBLIC ACCESSOR
    // ══════════════════════════════════════════════════════════════════

    /** Returns the last decoded QR text (may be null if nothing scanned yet). */
    public String getText() { return text; }

    public static void main(String[] args) {
        new QrScanner(); // standalone test
    }
}
