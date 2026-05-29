package com.mycompany.parabitinss;

/**
 * INSS — UserRole Enum
 * --------------------
 * Defines every access level in the system.
 * Used by UserSession and MainDashboard to control
 * which modules each employee can open.
 *
 * HOW TO ASSIGN ROLES:
 *   In your MySQL empdesignation table, make sure the
 *   'Designation' column contains one of these exact strings:
 *   ADMIN, TOLL_OPERATOR, ANALYST, GROUND_STAFF, VIEWER
 *
 *   Example SQL:
 *   UPDATE empdesignation SET Designation = 'TOLL_OPERATOR'
 *   WHERE DesigID = 2;
 */
public enum UserRole {

    /** Full access — all modules, all controls */
    ADMIN,

    /** Toll booth operator — registration check, QR scan, toll director */
    TOLL_OPERATOR,

    /** Data analyst — charts, dashboards, reports (read-mostly) */
    ANALYST,

    /** On-ground staff — person/vehicle registration, entry check */
    GROUND_STAFF,

    /** Read-only viewer — Yatra dashboard, circle view */
    VIEWER;

    /**
     * Safely parse a role string from the database.
     * If the DB contains an unrecognised string, defaults to VIEWER
     * instead of crashing the login flow.
     *
     * @param value  the Designation string from empdesignation table
     * @return       matching UserRole, or VIEWER as fallback
     */
    public static UserRole fromString(String value) {
        if (value == null || value.isBlank()) return VIEWER;
        try {
            return UserRole.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("[UserRole] Unknown role in DB: '" + value + "' — defaulting to VIEWER");
            return VIEWER;
        }
    }
}
