package com.mycompany.parabitinss;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * INSS — UserSession (Singleton)
 * --------------------------------
 * Stores the currently logged-in employee's details for
 * the entire application lifetime.
 *
 * USAGE:
 *   // After login succeeds in PbtEmpLog:
 *   UserSession.getInstance().login("EMP001", "Akshat Shukla", UserRole.ADMIN);
 *
 *   // Anywhere else in the app — check who's logged in:
 *   UserSession s = UserSession.getInstance();
 *   String name = s.getEmpName();
 *   UserRole role = s.getRole();
 *   boolean isAdmin = s.hasRole(UserRole.ADMIN);
 *
 *   // On logout button click:
 *   UserSession.getInstance().logout();
 *
 * INACTIVITY AUTO-LOGOUT:
 *   Call UserSession.getInstance().resetInactivityTimer()
 *   inside every button click / form action in your app.
 *   After INACTIVITY_MINUTES of silence, the session is
 *   cleared and the login screen is shown again.
 */
public class UserSession {

    // ── Inactivity timeout in minutes ──────────────────────────────────
    private static final int INACTIVITY_MINUTES = 15;

    // ── Singleton instance ─────────────────────────────────────────────
    private static UserSession instance;

    // ── Session data ───────────────────────────────────────────────────
    private String empId;
    private String empName;
    private UserRole role;
    private LocalDateTime loginTime;
    private boolean loggedIn = false;

    // ── Inactivity timer ───────────────────────────────────────────────
    private Timer inactivityTimer;

    // ── Reference to the main dashboard (needed to dispose on logout) ──
    private MainDashboard dashboard;

    // ── Private constructor (singleton pattern) ────────────────────────
    private UserSession() {}

    /**
     * Returns the single shared instance of UserSession.
     * Thread-safe via synchronized.
     */
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // ══════════════════════════════════════════════════════════════════
    //  LOGIN
    // ══════════════════════════════════════════════════════════════════

    /**
     * Call this once after DB authentication succeeds in PbtEmpLog.
     *
     * @param empId    employee ID string (e.g. "EMP0001")
     * @param empName  full name from empreg table
     * @param role     UserRole determined from empdesignation table
     */
    public void login(String empId, String empName, UserRole role) {
        this.empId     = empId;
        this.empName   = empName;
        this.role      = role;
        this.loginTime = LocalDateTime.now();
        this.loggedIn  = true;

        System.out.println("[UserSession] Logged in: " + empName + " | Role: " + role
                + " | Time: " + loginTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));

        startInactivityTimer();
    }

    // ══════════════════════════════════════════════════════════════════
    //  LOGOUT
    // ══════════════════════════════════════════════════════════════════

    /**
     * Clears the session. Called on logout button click,
     * or automatically after inactivity timeout.
     */
    public void logout() {
        System.out.println("[UserSession] Logging out: " + empName);

        stopInactivityTimer();

        // Close the main dashboard if it's open
        if (dashboard != null) {
            SwingUtilities.invokeLater(() -> {
                dashboard.dispose();
                dashboard = null;
            });
        }

        // Clear all session data
        empId     = null;
        empName   = null;
        role      = null;
        loginTime = null;
        loggedIn  = false;

        // Show login screen again
        SwingUtilities.invokeLater(() -> {
            PbtEmpLog loginDialog = new PbtEmpLog(null, true);
            loginDialog.setVisible(true);
        });
    }

    // ══════════════════════════════════════════════════════════════════
    //  INACTIVITY TIMER
    // ══════════════════════════════════════════════════════════════════

    private void startInactivityTimer() {
        stopInactivityTimer(); // cancel any existing timer first
        inactivityTimer = new Timer("InactivityTimer", true);
        inactivityTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        null,
                        "Session expired due to " + INACTIVITY_MINUTES + " minutes of inactivity.\nPlease log in again.",
                        "Session Expired",
                        JOptionPane.WARNING_MESSAGE
                    );
                    logout();
                });
            }
        }, (long) INACTIVITY_MINUTES * 60 * 1000);
    }

    private void stopInactivityTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.cancel();
            inactivityTimer = null;
        }
    }

    /**
     * Call this inside every significant user action (button clicks, form submits)
     * to reset the 15-minute inactivity countdown.
     *
     * Example: place UserSession.getInstance().resetInactivityTimer();
     *          at the top of every b1ActionPerformed, b2ActionPerformed, etc.
     */
    public void resetInactivityTimer() {
        if (loggedIn) startInactivityTimer();
    }

    // ══════════════════════════════════════════════════════════════════
    //  GETTERS
    // ══════════════════════════════════════════════════════════════════

    public String getEmpId()      { return empId; }
    public String getEmpName()    { return empName; }
    public UserRole getRole()     { return role; }
    public LocalDateTime getLoginTime() { return loginTime; }
    public boolean isLoggedIn()   { return loggedIn; }

    /**
     * Quick role check — use this in every module that needs access control.
     *
     * Example:
     *   if (!UserSession.getInstance().hasRole(UserRole.ADMIN)) {
     *       JOptionPane.showMessageDialog(this, "Access Denied.");
     *       return;
     *   }
     */
    public boolean hasRole(UserRole requiredRole) {
        if (!loggedIn || role == null) return false;
        // ADMIN can access everything
        if (role == UserRole.ADMIN) return true;
        return role == requiredRole;
    }

    /**
     * Check if user has at least one of the given roles.
     * Use for modules accessible by multiple roles.
     *
     * Example:
     *   if (!UserSession.getInstance().hasAnyRole(UserRole.TOLL_OPERATOR, UserRole.ADMIN)) { ... }
     */
    public boolean hasAnyRole(UserRole... roles) {
        if (!loggedIn || role == null) return false;
        if (role == UserRole.ADMIN) return true;
        for (UserRole r : roles) {
            if (role == r) return true;
        }
        return false;
    }

    // ── Dashboard reference (set by MainDashboard after it opens) ──────
    public void setDashboard(MainDashboard d) { this.dashboard = d; }
    public MainDashboard getDashboard()       { return dashboard; }

    @Override
    public String toString() {
        return "UserSession{empId='" + empId + "', name='" + empName
                + "', role=" + role + ", loggedIn=" + loggedIn + "}";
    }
}
