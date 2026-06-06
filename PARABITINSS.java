package com.mycompany.parabitinss;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * INSS — PARABITINSS (Main Entry Point)
 * ---------------------------------------
 * This is the first class the JVM runs.
 *
 * STARTUP SEQUENCE:
 *   1. Apply system Look & Feel (makes the app look native on Windows/Linux)
 *   2. Show SplashScreen
 *   3. SplashScreen tests DB → if fail: show error and exit
 *   4. SplashScreen opens PbtEmpLog (login dialog)
 *   5. PbtEmpLog authenticates → sets UserSession → opens MainDashboard
 *
 * PREVIOUS VERSION:
 *   The original main() was essentially empty with a QrCode call
 *   commented out. This replaces it completely.
 */
public class PARABITINSS {

    public static void main(String[] args) {

        // ── Step 1: Apply Look & Feel ──────────────────────────────────
        // "Nimbus" gives a cleaner cross-platform appearance.
        // If Nimbus is unavailable, falls back to system default.
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());

                    // Override Nimbus dark defaults so our custom dark colors show correctly
                    UIManager.getLookAndFeelDefaults().put("control",         new java.awt.Color(15, 23, 42));
                    UIManager.getLookAndFeelDefaults().put("info",            new java.awt.Color(30, 41, 59));
                    UIManager.getLookAndFeelDefaults().put("nimbusBase",      new java.awt.Color(46, 116, 181));
                    UIManager.getLookAndFeelDefaults().put("nimbusBlueGrey",  new java.awt.Color(51, 65, 85));
                    UIManager.getLookAndFeelDefaults().put("nimbusOrange",    new java.awt.Color(234, 88, 12));
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus fails, fall through to system L&F — not a crash
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        // ── Step 2: Launch SplashScreen on the Event Dispatch Thread ──
        // ALL Swing components must be created on the EDT.
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);

            // runSequence() spins up its own background thread for
            // progress animation and DB testing, then opens PbtEmpLog.
            splash.runSequence();
        });
    }
}
