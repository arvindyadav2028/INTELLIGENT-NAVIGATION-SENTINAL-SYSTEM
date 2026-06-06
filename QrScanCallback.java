package com.mycompany.parabitinss;

/**
 * INSS — QrScanCallback
 * ----------------------
 * Functional interface used by QrScanner to return
 * a decoded QR result to whatever opened the scanner.
 *
 * USAGE in PbtRegCheck:
 *   QrScanner scanner = new QrScanner(decodedText -> {
 *       tf1.setText(decodedText);
 *       performLookup();
 *   });
 */
@FunctionalInterface
public interface QrScanCallback {
    /**
     * Called exactly once when a QR code is successfully decoded.
     * Always invoked on the Event Dispatch Thread (SwingUtilities.invokeLater).
     *
     * @param decodedText  the raw string content of the QR code
     */
    void onScanResult(String decodedText);
}
