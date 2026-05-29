package com.mycompany.parabitinss;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * INSS — SplashScreen
 * --------------------
 * A borderless loading window shown when the app first starts.
 *
 * PHASES:
 *   Phase 1 (0–30%)  : Show logo + "Starting INSS..."
 *   Phase 2 (30–70%) : Test DB connection — shows success or failure
 *   Phase 3 (70–100%): "Loading dashboard..." then open PbtEmpLog
 *
 * If DB connection fails at Phase 2, an error message is shown
 * and the app exits cleanly instead of crashing later with a
 * mysterious NullPointerException.
 *
 * CALLED FROM:
 *   PARABITINSS.java main() method.
 */
public class SplashScreen extends JWindow {

    // ── UI Components ──────────────────────────────────────────────────
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;

    // ── Colors matching Parabit brand ──────────────────────────────────
    private static final Color BG_DARK       = new Color(15, 23, 42);      // deep navy
    private static final Color BG_CARD       = new Color(30, 41, 59);      // card background
    private static final Color ACCENT_BLUE   = new Color(46, 116, 181);    // Parabit blue
    private static final Color ACCENT_ORANGE = new Color(234, 88, 12);     // Parabit orange/red
    private static final Color TEXT_WHITE    = new Color(248, 250, 252);
    private static final Color TEXT_MUTED    = new Color(148, 163, 184);
    private static final Color PROGRESS_BG   = new Color(51, 65, 85);

    public SplashScreen() {
        buildUI();
        setSize(560, 340);
        setLocationRelativeTo(null);

        // Rounded window corners (works on Windows & Linux)
        try {
            setShape(new RoundRectangle2D.Double(0, 0, 560, 340, 20, 20));
        } catch (UnsupportedOperationException ignored) {}
    }

    // ══════════════════════════════════════════════════════════════════
    //  UI CONSTRUCTION
    // ══════════════════════════════════════════════════════════════════

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                // Gradient background: dark navy → slightly lighter
                GradientPaint gp = new GradientPaint(
                    0, 0, BG_DARK,
                    0, getHeight(), new Color(20, 30, 55)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        root.setOpaque(false);
        root.setBorder(BorderFactory.createLineBorder(ACCENT_BLUE, 2));

        // ── TOP: Logo area ──────────────────────────────────────────
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));

        // Parabit logo (text-based — replace with ImageIcon if you have logo.png)
        JPanel logoBox = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Orange "P" circle
                g2.setColor(ACCENT_ORANGE);
                g2.fillOval(0, 0, 52, 52);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 28));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("P", (52 - fm.stringWidth("P")) / 2, 36);
            }
            @Override public Dimension getPreferredSize() { return new Dimension(52, 52); }
        };
        logoBox.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel company = new JLabel("ParaBiT Technology Pvt. Ltd.");
        company.setFont(new Font("Segoe UI", Font.BOLD, 13));
        company.setForeground(TEXT_MUTED);
        company.setAlignmentX(Component.LEFT_ALIGNMENT);

        titleLabel = new JLabel("INSS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        subtitleLabel = new JLabel("Intelligent Navigation Sentinel System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(ACCENT_BLUE);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(company);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(titleLabel);
        textPanel.add(subtitleLabel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 16);
        topPanel.add(logoBox, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        topPanel.add(textPanel, gbc);

        // ── MIDDLE: Tagline ─────────────────────────────────────────
        JPanel midPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        midPanel.setOpaque(false);
        JLabel tagline = new JLabel("\"Gatipath: Orchestrating Movement, Empowering Millions\"");
        tagline.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        tagline.setForeground(TEXT_MUTED);
        midPanel.add(tagline);

        // ── BOTTOM: Progress bar + status ───────────────────────────
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 30, 40));

        statusLabel = new JLabel("Starting INSS...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_MUTED);
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        progressBar = new JProgressBar(0, 100) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Background track
                g2.setColor(PROGRESS_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                // Filled portion
                int filled = (int) ((getValue() / 100.0) * getWidth());
                g2.setColor(ACCENT_BLUE);
                g2.fillRoundRect(0, 0, filled, getHeight(), 8, 8);
            }
        };
        progressBar.setPreferredSize(new Dimension(480, 8));
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        progressBar.setOpaque(false);
        progressBar.setBorderPainted(false);
        progressBar.setValue(0);
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Version + event label
        JPanel infoRow = new JPanel(new BorderLayout());
        infoRow.setOpaque(false);
        JLabel version = new JLabel("v1.0.0  |  TechKumbh – Simhastha'28");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(new Color(71, 85, 105));
        infoRow.add(version, BorderLayout.WEST);

        bottomPanel.add(statusLabel);
        bottomPanel.add(Box.createVerticalStrut(8));
        bottomPanel.add(progressBar);
        bottomPanel.add(Box.createVerticalStrut(8));
        bottomPanel.add(infoRow);

        root.add(topPanel,    BorderLayout.NORTH);
        root.add(midPanel,    BorderLayout.CENTER);
        root.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ══════════════════════════════════════════════════════════════════
    //  LAUNCH SEQUENCE
    // ══════════════════════════════════════════════════════════════════

    /**
     * Run the full splash sequence on a background thread.
     * Call this AFTER setVisible(true).
     */
    public void runSequence() {
        new Thread(() -> {
            try {
                // ── Phase 1: Initializing ──────────────────────────
                setStatus("Starting INSS...", 5);
                Thread.sleep(400);
                setStatus("Loading configuration...", 20);
                Thread.sleep(400);

                // ── Phase 2: DB Connection Test ────────────────────
                setStatus("Connecting to database...", 35);
                Thread.sleep(300);

                boolean dbOk = testDatabaseConnection();

                if (!dbOk) {
                    setStatus("❌  Database connection failed!", 40);
                    progressBar.setForeground(new Color(220, 38, 38)); // red
                    Thread.sleep(2000);

                    // Show error dialog on EDT then exit
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                            this,
                            "Cannot connect to MySQL database.\n\n"
                            + "Please check:\n"
                            + "  • MySQL / XAMPP is running\n"
                            + "  • Database 'parabitinss' exists\n"
                            + "  • Credentials in ParabitDBC.java are correct\n\n"
                            + "Application will exit.",
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                        System.exit(1);
                    });
                    return;
                }

                setStatus("✔  Database connected.", 55);
                Thread.sleep(400);

                // ── Phase 3: Load resources ────────────────────────
                setStatus("Loading modules...", 70);
                Thread.sleep(300);
                setStatus("Preparing login screen...", 88);
                Thread.sleep(300);
                setStatus("Ready.", 100);
                Thread.sleep(300);

                // ── Hand off to login screen ───────────────────────
                SwingUtilities.invokeLater(() -> {
                    dispose(); // close splash
                    PbtEmpLog login = new PbtEmpLog(null, true);
                    login.setVisible(true);
                });

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "SplashThread").start();
    }

    // ══════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════

    /** Update the status label and progress bar from any thread. */
    private void setStatus(String message, int progress) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            progressBar.setValue(progress);
        });
    }

    /**
     * Attempt a real DB connection using ParabitDBC.
     * Returns true if successful, false if any exception occurs.
     */
    private boolean testDatabaseConnection() {
        try {
            ParabitDBC test = new ParabitDBC();
            // Run a trivial query to confirm the connection is live
            test.stm.executeQuery("SELECT 1");
            return true;
        } catch (Exception e) {
            System.err.println("[SplashScreen] DB test failed: " + e.getMessage());
            return false;
        }
    }
}
