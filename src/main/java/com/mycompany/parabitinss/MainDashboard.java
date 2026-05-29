package com.mycompany.parabitinss;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * INSS — MainDashboard
 * ----------------------
 * The central navigation window opened after a successful login.
 *
 * LAYOUT:
 *   ┌─────────────────────────────────────────────────────┐
 *   │  HEADER: Logo | "Welcome, [Name]" | Role | Logout   │
 *   ├─────────────────────────────────────────────────────┤
 *   │                                                     │
 *   │   [Module Button Grid — filtered by UserRole]       │
 *   │                                                     │
 *   ├─────────────────────────────────────────────────────┤
 *   │  STATUS BAR: DB status | Current time | Session     │
 *   └─────────────────────────────────────────────────────┘
 *
 * ROLE → VISIBLE BUTTONS:
 *   ADMIN        → All 10 modules
 *   TOLL_OP      → Toll Director, Reg Check, QR Scanner, Vehicle Reg
 *   ANALYST      → Yatra Dashboard, Circle Director, Toll Director, Charts
 *   GROUND_STAFF → Person Reg, Vehicle Reg, Reg Check, QR Scanner
 *   VIEWER       → Yatra Dashboard, Circle Director (read-only)
 *
 * HOW TO ADD A NEW MODULE BUTTON:
 *   1. Create a new ModuleButton entry in buildModuleGrid()
 *   2. Add the allowed roles to its roles[] array
 *   3. Write the openXxx() method at the bottom of this file
 */
public class MainDashboard extends JFrame {

    // ── Colors ──────────────────────────────────────────────────────────
    private static final Color BG_MAIN       = new Color(15, 23, 42);
    private static final Color BG_HEADER     = new Color(20, 30, 55);
    private static final Color BG_CARD       = new Color(30, 41, 59);
    private static final Color BG_CARD_HOVER = new Color(46, 60, 82);
    private static final Color ACCENT_BLUE   = new Color(46, 116, 181);
    private static final Color ACCENT_ORANGE = new Color(234, 88, 12);
    private static final Color ACCENT_GREEN  = new Color(34, 197, 94);
    private static final Color ACCENT_RED    = new Color(239, 68, 68);
    private static final Color ACCENT_PURPLE = new Color(139, 92, 246);
    private static final Color ACCENT_TEAL   = new Color(20, 184, 166);
    private static final Color TEXT_WHITE    = new Color(248, 250, 252);
    private static final Color TEXT_MUTED    = new Color(148, 163, 184);
    private static final Color DIVIDER       = new Color(51, 65, 85);

    // ── Status bar components (updated by timer) ─────────────────────
    private JLabel clockLabel;
    private JLabel sessionLabel;
    private Timer clockTimer;

    // ══════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR
    // ══════════════════════════════════════════════════════════════════

    public MainDashboard() {
        super("INSS — Command Dashboard");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        // Confirm before closing
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        buildUI();
        startClockTimer();
    }

    // ══════════════════════════════════════════════════════════════════
    //  UI CONSTRUCTION
    // ══════════════════════════════════════════════════════════════════

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_MAIN);

        root.add(buildHeader(),    BorderLayout.NORTH);
        root.add(buildModuleGrid(),BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    // ── HEADER ────────────────────────────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout(0, 0));
        header.setBackground(BG_HEADER);
        header.setPreferredSize(new Dimension(0, 72));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_BLUE));

        // Left: Logo + App Name
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 14));
        left.setOpaque(false);

        JLabel logoLabel = new JLabel("P") {{
            setFont(new Font("Arial", Font.BOLD, 22));
            setForeground(Color.WHITE);
            setOpaque(true);
            setBackground(ACCENT_ORANGE);
            setHorizontalAlignment(SwingConstants.CENTER);
            setPreferredSize(new Dimension(40, 40));
            setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
        }};

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel appTitle = new JLabel("INSS Command Center");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        appTitle.setForeground(TEXT_WHITE);
        JLabel appSub = new JLabel("Intelligent Navigation Sentinel System");
        appSub.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        appSub.setForeground(TEXT_MUTED);
        titleBlock.add(appTitle);
        titleBlock.add(appSub);

        left.add(logoLabel);
        left.add(titleBlock);

        // Right: User info + Logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 14));
        right.setOpaque(false);

        UserSession s = UserSession.getInstance();

        JPanel userBlock = new JPanel();
        userBlock.setOpaque(false);
        userBlock.setLayout(new BoxLayout(userBlock, BoxLayout.Y_AXIS));
        JLabel userName = new JLabel("Welcome, " + s.getEmpName());
        userName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userName.setForeground(TEXT_WHITE);
        userName.setAlignmentX(Component.RIGHT_ALIGNMENT);
        JLabel userRole = new JLabel(s.getRole().toString() + "  |  ID: " + s.getEmpId());
        userRole.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        userRole.setForeground(ACCENT_BLUE);
        userRole.setAlignmentX(Component.RIGHT_ALIGNMENT);
        userBlock.add(userName);
        userBlock.add(userRole);

        JButton logoutBtn = createStyledButton("Logout", ACCENT_RED, 90, 34);
        logoutBtn.addActionListener(e -> confirmLogout());

        right.add(userBlock);
        right.add(logoutBtn);

        header.add(left,  BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        return header;
    }

    // ── MODULE GRID ───────────────────────────────────────────────────

    /**
     * Defines ALL modules and which roles can see each one.
     * To add a new module: add a new ModuleInfo entry here.
     */
    private JPanel buildModuleGrid() {
        UserRole currentRole = UserSession.getInstance().getRole();

        // ── Define every module ──────────────────────────────────────
        ModuleInfo[] modules = {
            new ModuleInfo(
                "🎯  Yatra Dashboard",
                "Live arrivals, returns & zone overview",
                ACCENT_BLUE,
                new UserRole[]{ UserRole.ADMIN, UserRole.ANALYST, UserRole.VIEWER },
                this::openYatraDashboard
            ),
            new ModuleInfo(
                "🏗  Circle Director",
                "Zone density & concentric ring control",
                ACCENT_TEAL,
                new UserRole[]{ UserRole.ADMIN, UserRole.ANALYST, UserRole.VIEWER },
                this::openCircleDirector
            ),
            new ModuleInfo(
                "🛣  Toll Director",
                "Highway inflow & toll monitoring",
                ACCENT_GREEN,
                new UserRole[]{ UserRole.ADMIN, UserRole.TOLL_OPERATOR, UserRole.ANALYST },
                this::openTollDirector
            ),
            new ModuleInfo(
                "🚗  Vehicle Registration",
                "Register personal & commercial vehicles",
                new Color(245, 158, 11),
                new UserRole[]{ UserRole.ADMIN, UserRole.TOLL_OPERATOR, UserRole.GROUND_STAFF },
                this::openVehicleReg
            ),
            new ModuleInfo(
                "👤  Person Registration",
                "Register pilgrims & group members",
                ACCENT_PURPLE,
                new UserRole[]{ UserRole.ADMIN, UserRole.GROUND_STAFF },
                this::openPersonReg
            ),
            new ModuleInfo(
                "🔍  Registration Check",
                "Verify QR, Aadhaar or vehicle number",
                new Color(236, 72, 153),
                new UserRole[]{ UserRole.ADMIN, UserRole.TOLL_OPERATOR, UserRole.GROUND_STAFF },
                this::openRegCheck
            ),
            new ModuleInfo(
                "📷  QR Scanner",
                "Webcam-based QR code entry check",
                new Color(6, 182, 212),
                new UserRole[]{ UserRole.ADMIN, UserRole.TOLL_OPERATOR, UserRole.GROUND_STAFF },
                this::openQrScanner
            ),
            new ModuleInfo(
                "🅿  Parking Monitor",
                "Parking availability across zones",
                new Color(251, 146, 60),
                new UserRole[]{ UserRole.ADMIN, UserRole.ANALYST },
                this::openParkingMonitor
            ),
            new ModuleInfo(
                "🚂  Railway Center",
                "Train arrivals & platform crowd control",
                new Color(52, 211, 153),
                new UserRole[]{ UserRole.ADMIN, UserRole.ANALYST },
                this::openRailwayCenter
            ),
            new ModuleInfo(
                "⚙  Admin Panel",
                "Employee management & system settings",
                ACCENT_RED,
                new UserRole[]{ UserRole.ADMIN },
                this::openAdminPanel
            ),
        };

        // ── Build grid panel ─────────────────────────────────────────
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_MAIN);
        wrapper.setBorder(BorderFactory.createEmptyBorder(24, 30, 16, 30));

        JLabel sectionTitle = new JLabel("Select a Module");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        sectionTitle.setForeground(TEXT_MUTED);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        // Responsive grid: 4 columns
        JPanel grid = new JPanel(new GridLayout(0, 4, 16, 16));
        grid.setOpaque(false);

        for (ModuleInfo m : modules) {
            if (m.isVisibleTo(currentRole)) {
                grid.add(buildModuleCard(m));
            }
        }

        // If no modules visible (shouldn't happen), show message
        if (grid.getComponentCount() == 0) {
            JLabel noAccess = new JLabel("No modules available for your role. Contact administrator.");
            noAccess.setForeground(TEXT_MUTED);
            noAccess.setHorizontalAlignment(SwingConstants.CENTER);
            wrapper.add(noAccess, BorderLayout.CENTER);
            return wrapper;
        }

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        wrapper.add(sectionTitle, BorderLayout.NORTH);
        wrapper.add(scroll,       BorderLayout.CENTER);
        return wrapper;
    }

    /** Build a single clickable module card. */
    private JPanel buildModuleCard(ModuleInfo module) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(BG_CARD);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER, 1),
            BorderFactory.createEmptyBorder(20, 18, 20, 18)
        ));
        card.setPreferredSize(new Dimension(220, 110));

        // Accent stripe on left edge
        JPanel stripe = new JPanel();
        stripe.setBackground(module.accentColor);
        stripe.setPreferredSize(new Dimension(4, 0));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel titleLbl = new JLabel(module.title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLbl.setForeground(TEXT_WHITE);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLbl = new JLabel("<html><p style='width:160px'>" + module.description + "</p></html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLbl.setForeground(TEXT_MUTED);
        descLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(titleLbl);
        content.add(Box.createVerticalStrut(6));
        content.add(descLbl);

        card.add(stripe,  BorderLayout.WEST);
        card.add(content, BorderLayout.CENTER);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                card.setBackground(BG_CARD_HOVER);
                card.repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                card.setBackground(BG_CARD);
                card.repaint();
            }
            @Override public void mouseClicked(MouseEvent e) {
                UserSession.getInstance().resetInactivityTimer();
                module.action.run();
            }
        });

        return card;
    }

    // ── STATUS BAR ────────────────────────────────────────────────────

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_HEADER);
        bar.setPreferredSize(new Dimension(0, 30));
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER));

        // Left: DB status
        JLabel dbStatus = new JLabel("  ●  Database Connected  |  parabitinss @ localhost");
        dbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dbStatus.setForeground(ACCENT_GREEN);

        // Center: session info
        sessionLabel = new JLabel();
        sessionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sessionLabel.setForeground(TEXT_MUTED);
        sessionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateSessionLabel();

        // Right: live clock
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        clockLabel.setForeground(TEXT_MUTED);
        updateClock();

        bar.add(dbStatus,     BorderLayout.WEST);
        bar.add(sessionLabel, BorderLayout.CENTER);
        bar.add(clockLabel,   BorderLayout.EAST);

        return bar;
    }

    // ══════════════════════════════════════════════════════════════════
    //  MODULE OPEN METHODS
    //  — Each method opens the corresponding module window.
    //  — Modules not yet built show a "Coming in Sprint X" dialog.
    // ══════════════════════════════════════════════════════════════════

    private void openYatraDashboard() {
        PbtYatra frame = new PbtYatra("welcome");
        frame.setVisible(true);
    }

    private void openCircleDirector() {
        CircleDirector frame = new CircleDirector();
        frame.setVisible(true);
    }

    private void openTollDirector() {
        TollDirector frame = new TollDirector();
//        frame.setVisible(true);
    }

    private void openVehicleReg() {
        PbtVhReg frame = new PbtVhReg(new javax.swing.JFrame(), true);
        frame.setVisible(true);
    }

    private void openPersonReg() {
        PersonReg dialog = new PersonReg(this, false);
        dialog.setVisible(true);
    }

    private void openRegCheck() {
        PbtRegCheck dialog = new PbtRegCheck(this, false);
        dialog.setVisible(true);
    }

    private void openQrScanner() {
        QrScanner dialog = new QrScanner();
//        dialog.setVisible(true);
    }

    private void openParkingMonitor() {
        // TODO Sprint 6 — ParkingMonitor not yet built
        showComingSoon("Parking Monitor", "Sprint 6");
    }

    private void openRailwayCenter() {
        // TODO Sprint 6 — RailwayCenter not yet built
        showComingSoon("Railway Center", "Sprint 6");
    }

    private void openAdminPanel() {
        // TODO Sprint 6 — AdminPanel not yet built
        showComingSoon("Admin Panel", "Sprint 6");
    }

    private void showComingSoon(String moduleName, String sprint) {
        JOptionPane.showMessageDialog(this,
            moduleName + " is planned for " + sprint + ".\n"
            + "See the INSS Implementation Roadmap document for details.",
            "Coming Soon",
            JOptionPane.INFORMATION_MESSAGE);
    }

    // ══════════════════════════════════════════════════════════════════
    //  UTILITIES
    // ══════════════════════════════════════════════════════════════════

    /** Styled button helper used in the header. */
    private JButton createStyledButton(String text, Color bg, int w, int h) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setPreferredSize(new Dimension(w, h));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void startClockTimer() {
        clockTimer = new Timer(1000, e -> {
            updateClock();
            updateSessionLabel();
        });
        clockTimer.start();
    }

    private void updateClock() {
        if (clockLabel != null) {
            String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy  HH:mm:ss  "));
            clockLabel.setText(time);
        }
    }

    private void updateSessionLabel() {
        if (sessionLabel != null) {
            UserSession s = UserSession.getInstance();
            if (s.isLoggedIn() && s.getLoginTime() != null) {
                String loginTime = s.getLoginTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm"));
                sessionLabel.setText("Session started: " + loginTime
                    + "  |  Auto-logout after 15 min inactivity");
            }
        }
    }

    private void confirmLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to log out?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            if (clockTimer != null) clockTimer.stop();
            UserSession.getInstance().logout();
            dispose();
        }
    }

    private void confirmExit() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Exit INSS?\n\nMake sure all checkpoint operators have been notified.",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            if (clockTimer != null) clockTimer.stop();
            System.exit(0);
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  INNER CLASS: ModuleInfo
    // ══════════════════════════════════════════════════════════════════

    /**
     * Describes a single navigation module card.
     * All modules are defined as ModuleInfo objects in buildModuleGrid().
     */
    private static class ModuleInfo {
        final String title;
        final String description;
        final Color  accentColor;
        final UserRole[] allowedRoles;
        final Runnable action;

        ModuleInfo(String title, String description, Color accentColor,
                   UserRole[] allowedRoles, Runnable action) {
            this.title        = title;
            this.description  = description;
            this.accentColor  = accentColor;
            this.allowedRoles = allowedRoles;
            this.action       = action;
        }

        boolean isVisibleTo(UserRole role) {
            if (role == UserRole.ADMIN) return true;
            for (UserRole r : allowedRoles) {
                if (r == role) return true;
            }
            return false;
        }
    }
}
