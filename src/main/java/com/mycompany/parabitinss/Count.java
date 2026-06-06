/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.parabitinss;

/**
 *
 * @author arvin
 */


import java.awt.Component;
import java.time.LocalDate;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class Count extends javax.swing.JFrame {

    public Count() {
        initComponents();

        // === Table Setup ===
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Toll Name", "Toll ID", "Alert", "Graph"}
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 2) return JProgressBar.class;
                if (columnIndex == 3) return JButton.class;
                return Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only Graph button editable
            }
        };

        t1.setModel(model);
        t1.getColumnModel().getColumn(2).setCellRenderer(new ProgressRenderer());
        t1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        t1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));
        t1.setRowHeight(23);

        // === Load Data into Table ===
        ParabitDBC db1 = new ParabitDBC();
        try {
            db1.rs = db1.stm.executeQuery("SELECT LocationName, CPName FROM checkpointtoll;");
            while (db1.rs.next()) {
                model.addRow(new Object[]{db1.rs.getString(1), db1.rs.getString(2), 0, "Graph"});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // === Setup JavaFX Chart Panel ===
        JFXPanel jfxPanel = new JFXPanel();
        p1.setLayout(new java.awt.BorderLayout());
        p1.add(jfxPanel, java.awt.BorderLayout.CENTER);

        setSize(1350, 700);
        setLocationRelativeTo(null);

        // === Run JavaFX UI Thread for Charts ===
        Platform.runLater(() -> {
            try {
                // -----------------------------
                // BAR CHART SECTION (Fixed)
                // -----------------------------
                CategoryAxis xAxis = new CategoryAxis();
                xAxis.setLabel("Time");

                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Vehicle Count");

                BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
                barChart.setTitle("Vehicle Count by Hour");

                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Today");

                ParabitDBC db = new ParabitDBC();
                String sql = "SELECT Time, SUM(APCount + ACCount) AS TotalCount "
                        + "FROM tollpassedveh WHERE TollCpNo = ? AND Date = ? "
                        + "GROUP BY Time ORDER BY CAST(Time AS UNSIGNED)";

                LocalDate currDate = LocalDate.now();
                java.sql.Date sqlDate = java.sql.Date.valueOf(currDate);

                db.ps = db.con.prepareStatement(sql);
                db.ps.setInt(1, 1);
                db.ps.setDate(2, sqlDate);
                db.rs = db.ps.executeQuery();

                while (db.rs.next()) {
                    String time = db.rs.getString("Time");
                    int count = db.rs.getInt("TotalCount");
                    series.getData().add(new XYChart.Data<>(time, count));
                }

                barChart.getData().add(series);
                barChart.setCategoryGap(10);
                barChart.setBarGap(2);
                barChart.setLegendVisible(true);

                VBox vbox = new VBox(barChart);
                vbox.setPadding(new Insets(10));
                vbox.setSpacing(10);

                Scene scene = new Scene(vbox, 800, 500);
                jfxPanel.setScene(scene);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // === Custom Cell Renderers ===
    class ProgressRenderer extends JProgressBar implements TableCellRenderer {
        public ProgressRenderer() {
            setStringPainted(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            int progress = 0;
            if (value instanceof Integer) {
                progress = (Integer) value;
            }
            setValue(progress);
            return this;
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                JOptionPane.showMessageDialog(button, "Graph clicked for row!");
            }
            clicked = false;
            return label;
        }
    }

    // === Auto-generated UI components ===
    private javax.swing.JTable t1;
    private javax.swing.JPanel p1;

    private void initComponents() {
        p1 = new javax.swing.JPanel();
        t1 = new javax.swing.JTable();
        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(p1, java.awt.BorderLayout.CENTER);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new Count().setVisible(true));
    }
}

