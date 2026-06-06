package com.mycompany.parabitinss;

import java.sql.ResultSet;
import javax.swing.JOptionPane;

/**
 * INSS — PbtEmpLog (Employee Login Dialog)
 * -----------------------------------------
 * ORIGINAL FILE: Keep all NetBeans GEN-BEGIN/GEN-END sections unchanged.
 *
 * CHANGES MADE IN THIS VERSION:
 *   1. b1ActionPerformed now queries empreg + empdesignation to
 *      authenticate AND determine the employee's role.
 *   2. On success: calls UserSession.getInstance().login(...)
 *      then opens MainDashboard.
 *   3. On failure: shows a clear error message without crashing.
 *
 * DB QUERY USED:
 *   SELECT e.EmpID, e.EmpName, d.Designation
 *   FROM empreg e
 *   JOIN empdesignation d ON e.DesigFK = d.DesigID
 *   WHERE e.EmpLogin = ? AND e.EmpPassword = ?
 *
 * NOTE: Your empdesignation.Designation column must contain one of:
 *   ADMIN, TOLL_OPERATOR, ANALYST, GROUND_STAFF, VIEWER
 *   (UserRole.fromString handles case-insensitive matching)
 */
public class PbtEmpLog extends javax.swing.JDialog {

    /** Kept for backward compatibility with any code that reads PbtEmpLog.a */
    public static int a;

    public PbtEmpLog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("INSS — Employee Login");
        setLocationRelativeTo(null);
    }

    // ══════════════════════════════════════════════════════════════════
    //  !! DO NOT TOUCH ANYTHING BETWEEN GEN-BEGIN AND GEN-END !!
    //  NetBeans regenerates that block automatically from the .form file.
    // ══════════════════════════════════════════════════════════════════

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tf1 = new javax.swing.JTextField();
        tf2 = new javax.swing.JTextField();
        tf3 = new javax.swing.JPasswordField();
        b1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Emp Login");
        jLabel2.setText("Emp ID");
        jLabel3.setText("Emp Password");

        tf1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf1ActionPerformed(evt);
            }
        });

        tf2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf2ActionPerformed(evt);
            }
        });

        tf3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tf3ActionPerformed(evt);
            }
        });

        b1.setText("Login");
        b1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18));
        jLabel4.setForeground(new java.awt.Color(46, 116, 181));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("INSS — Staff Login");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(b1)
                            .addComponent(tf1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tf2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tf3, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(40, 40, 40))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel4)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(tf1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tf2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(tf3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(b1)
                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // ══════════════════════════════════════════════════════════════════
    //  ACTION HANDLERS — EDIT THESE FREELY
    // ══════════════════════════════════════════════════════════════════

    private void tf1ActionPerformed(java.awt.event.ActionEvent evt) {
        tf2.requestFocus(); // Tab to next field on Enter
    }

    private void tf2ActionPerformed(java.awt.event.ActionEvent evt) {
        tf3.requestFocus();
    }

    private void tf3ActionPerformed(java.awt.event.ActionEvent evt) {
        b1ActionPerformed(evt); // Enter in password field triggers login
    }

    /**
     * LOGIN BUTTON — Core authentication logic.
     *
     * Step 1: Read the three fields.
     * Step 2: Query empreg JOIN empdesignation — match login + empId + password.
     * Step 3: If match found → call UserSession.login() → open MainDashboard.
     * Step 4: If no match → show error, clear password field, let user retry.
     */
    private void b1ActionPerformed(java.awt.event.ActionEvent evt) {

        // ── Step 1: Read input ─────────────────────────────────────
        String empLogin    = tf1.getText().trim();
        String empId       = tf2.getText().trim();
        String empPassword = new String(tf3.getPassword()).trim();

        if (empLogin.isEmpty() || empId.isEmpty() || empPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all fields.",
                "Incomplete Login",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // ── Step 2: DB query ───────────────────────────────────────
        try {
            ParabitDBC db = new ParabitDBC();

            /*
             * JOIN empreg with empdesignation to get:
             *   - EmpID       (to store in session)
             *   - EmpName     (to display in dashboard header)
             *   - Designation (to determine UserRole)
             *
             * IMPORTANT: Your empreg table must have columns:
             *   EmpLogin, EmpID, EmpPassword, EmpName, DesigFK
             * Your empdesignation table must have:
             *   DesigID, Designation
             *
             * Adjust column names below if yours differ.
             */
            String sql =
                "SELECT e.EmpID, e.EmpName, d.Designation " +
                "FROM empreg e " +
                "JOIN empdesignation d ON e.EmpDesignationFK = d.DesigID " +
                "WHERE e.EmpLogin = ? AND e.EmpID = ? AND e.EmpPassword = ?";

            db.ps = db.con.prepareStatement(sql);
            db.ps.setString(1, empLogin);
            db.ps.setString(2, empId);
            db.ps.setString(3, empPassword);

            ResultSet rs = db.ps.executeQuery();

            // ── Step 3: Check result ───────────────────────────────
            if (rs.next()) {
                String dbEmpId       = rs.getString("EmpID");
                String dbEmpName     = rs.getString("EmpName");
                String dbDesignation = rs.getString("Designation");

                // Map designation string → UserRole enum
                UserRole role = UserRole.fromString(dbDesignation);

                // Store in backward-compat static field (existing code may use this)
                a = Integer.parseInt(dbEmpId.replaceAll("[^0-9]", ""));

                // ── Step 4: Open dashboard ─────────────────────────
                UserSession.getInstance().login(dbEmpId, dbEmpName, role);

                dispose(); // Close login dialog

                // Open main dashboard on the Event Dispatch Thread
                javax.swing.SwingUtilities.invokeLater(() -> {
                    MainDashboard dashboard = new MainDashboard();
                    UserSession.getInstance().setDashboard(dashboard);
                    dashboard.setVisible(true);
                });

            } else {
                // ── Step 4 (fail): Wrong credentials ──────────────
                JOptionPane.showMessageDialog(this,
                    "Invalid credentials. Please check your\nEmployee Login, ID, and Password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
                tf3.setText(""); // Clear password, keep other fields
                tf3.requestFocus();
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Database error during login:\n" + e.getMessage(),
                "System Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  VARIABLES — DO NOT MODIFY (NetBeans managed)
    // ══════════════════════════════════════════════════════════════════

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton b1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField tf1;
    private javax.swing.JTextField tf2;
    private javax.swing.JPasswordField tf3;
    // End of variables declaration//GEN-END:variables
}
