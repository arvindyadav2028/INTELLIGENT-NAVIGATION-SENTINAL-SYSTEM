package com.mycompany.parabitinss;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * INSS — InputValidator
 * ----------------------
 * Static utility methods for validating form fields
 * before any DB insert. Call these at the top of every
 * save/submit action handler.
 *
 * USAGE:
 *   List<String> errors = new ArrayList<>();
 *   InputValidator.requireNotEmpty(errors, tf1.getText(), "Owner Name");
 *   InputValidator.requirePhone(errors, tf2.getText(), "Mobile Number");
 *   InputValidator.requireAadhaar(errors, tf3.getText(), "Aadhaar Number");
 *   if (!errors.isEmpty()) {
 *       InputValidator.showErrors(parentComponent, errors);
 *       return;
 *   }
 *   // safe to proceed with DB insert
 */
public class InputValidator {

    private InputValidator() {} // utility class — no instances

    // ══════════════════════════════════════════════════════════════════
    //  FIELD VALIDATORS  (each adds an error string to the list)
    // ══════════════════════════════════════════════════════════════════

    /**
     * Field must not be null, blank, or "Select".
     */
    public static void requireNotEmpty(List<String> errors, String value, String fieldName) {
        if (value == null || value.isBlank() || value.equalsIgnoreCase("Select")) {
            errors.add("• " + fieldName + " is required.");
        }
    }

    /**
     * Field must be exactly 10 digits (Indian mobile number).
     */
    public static void requirePhone(List<String> errors, String value, String fieldName) {
        requireNotEmpty(errors, value, fieldName);
        if (value != null && !value.isBlank()) {
            String digits = value.replaceAll("\\D", "");
            if (digits.length() != 10) {
                errors.add("• " + fieldName + " must be a 10-digit mobile number.");
            }
        }
    }

    /**
     * Field must be exactly 12 digits (Aadhaar number).
     */
    public static void requireAadhaar(List<String> errors, String value, String fieldName) {
        requireNotEmpty(errors, value, fieldName);
        if (value != null && !value.isBlank()) {
            String digits = value.replaceAll("\\D", "");
            if (digits.length() != 12) {
                errors.add("• " + fieldName + " must be a 12-digit Aadhaar number.");
            }
        }
    }

    /**
     * Field must be a valid Indian vehicle registration number.
     * Format: XX-00-XX-0000  e.g. MP-15-AB-1234
     */
    public static void requireVehicleNumber(List<String> errors, String value, String fieldName) {
        requireNotEmpty(errors, value, fieldName);
        if (value != null && !value.isBlank()) {
            // Allow formats: MP15AB1234 or MP-15-AB-1234 or MP 15 AB 1234
            String normalized = value.toUpperCase().replaceAll("[\\s-]", "");
            if (!normalized.matches("[A-Z]{2}[0-9]{2}[A-Z]{1,3}[0-9]{4}")) {
                errors.add("• " + fieldName + " format invalid. Expected: MP-15-AB-1234");
            }
        }
    }

    /**
     * ComboBox must have a selection other than index 0 (the "Select" placeholder).
     */
    public static void requireComboSelection(List<String> errors, JComboBox<?> combo, String fieldName) {
        if (combo.getSelectedIndex() <= 0) {
            errors.add("• " + fieldName + " must be selected.");
        }
    }

    /**
     * Date spinner / picker must not be null.
     */
    public static void requireDate(List<String> errors, java.util.Date date, String fieldName) {
        if (date == null) {
            errors.add("• " + fieldName + " must be selected.");
        }
    }

    /**
     * Integer field must be > 0.
     */
    public static void requirePositiveInt(List<String> errors, String value, String fieldName) {
        requireNotEmpty(errors, value, fieldName);
        try {
            int v = Integer.parseInt(value.trim());
            if (v <= 0) errors.add("• " + fieldName + " must be greater than 0.");
        } catch (NumberFormatException e) {
            errors.add("• " + fieldName + " must be a valid number.");
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  DUPLICATE CHECKERS
    // ══════════════════════════════════════════════════════════════════

    /**
     * Returns true if the given vehicleNo already exists in personalvehreg.
     * Call this before INSERT to prevent duplicates.
     */
    public static boolean isDuplicateVehicle(String vehicleNo) {
        try {
            ParabitDBC db = new ParabitDBC();
            db.ps = db.con.prepareStatement(
                "SELECT COUNT(*) FROM personalvehreg WHERE VNo = ?");
            db.ps.setString(1, vehicleNo.trim().toUpperCase());
            db.rs = db.ps.executeQuery();
            if (db.rs.next()) return db.rs.getInt(1) > 0;
        } catch (Exception e) {
            System.err.println("[InputValidator] Duplicate vehicle check failed: " + e.getMessage());
        }
        return false;
    }

    /**
     * Returns true if the given Aadhaar number already exists in personreg.
     */
    public static boolean isDuplicateAadhaar(String aadhaar) {
        try {
            ParabitDBC db = new ParabitDBC();
            db.ps = db.con.prepareStatement(
                "SELECT COUNT(*) FROM personreg WHERE AadharNo = ?");
            db.ps.setString(1, aadhaar.trim());
            db.rs = db.ps.executeQuery();
            if (db.rs.next()) return db.rs.getInt(1) > 0;
        } catch (Exception e) {
            System.err.println("[InputValidator] Duplicate Aadhaar check failed: " + e.getMessage());
        }
        return false;
    }

    // ══════════════════════════════════════════════════════════════════
    //  ERROR DISPLAY
    // ══════════════════════════════════════════════════════════════════

    /**
     * Shows all validation errors in a single dialog.
     * Call this when errors list is not empty.
     */
    public static void showErrors(java.awt.Component parent, List<String> errors) {
        StringBuilder sb = new StringBuilder("Please fix the following:\n\n");
        for (String e : errors) sb.append(e).append("\n");
        JOptionPane.showMessageDialog(parent, sb.toString(),
            "Validation Error", JOptionPane.WARNING_MESSAGE);
    }

    // ══════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════

    /**
     * Parses the StateCode integer from a ComboBox item string.
     * ComboBox items are loaded as: "23,Madhya Pradesh,MP"
     * Returns the StateCode (23), or -1 if parsing fails.
     */
    public static int parseStateCode(Object selectedItem) {
        if (selectedItem == null) return -1;
        try {
            String s = selectedItem.toString().trim();
            return Integer.parseInt(s.split(",")[0].trim());
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Looks up the primary key ID for a vehicle type name in personalvehtype.
     * Returns 1 as fallback if not found.
     */
    public static int lookupVehicleTypeId(String vecTypeName) {
        try {
            ParabitDBC db = new ParabitDBC();
            db.ps = db.con.prepareStatement(
                "SELECT VecTypeID FROM personalvehtype WHERE VecType = ? LIMIT 1");
            db.ps.setString(1, vecTypeName);
            db.rs = db.ps.executeQuery();
            if (db.rs.next()) return db.rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[InputValidator] VehicleType lookup failed: " + e.getMessage());
        }
        return 1;
    }

    /**
     * Looks up the primary key ID for a fuel type name in fueltype.
     * Returns 1 as fallback if not found.
     */
    public static int lookupFuelTypeId(String fuelTypeName) {
        try {
            ParabitDBC db = new ParabitDBC();
            db.ps = db.con.prepareStatement(
                "SELECT FuelTypeID FROM fueltype WHERE FuelName = ? LIMIT 1");
            db.ps.setString(1, fuelTypeName);
            db.rs = db.ps.executeQuery();
            if (db.rs.next()) return db.rs.getInt(1);
        } catch (Exception e) {
            System.err.println("[InputValidator] FuelType lookup failed: " + e.getMessage());
        }
        return 1;
    }

    /**
     * Generates a short unique 8-character group code.
     * Format: GRP + 5 uppercase alphanumeric chars  e.g. "GRPX7K2M9"
     */
    public static String generateGroupCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder("GRP");
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < 6; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}
