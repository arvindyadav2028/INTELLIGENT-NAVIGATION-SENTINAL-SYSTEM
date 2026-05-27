package com.mycompany.parabitinss;

import java.awt.Component;
import java.awt.Dimension;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class CircleDirector extends javax.swing.JFrame {

    public CircleDirector() {
        initComponents();

        // ========== TABLE UNCHANGED AS YOU ORDERED ==========
        DefaultTableModel model = new DefaultTableModel(
                new Object[][]{}, new String[]{"Toll Name","Toll ID","Alert","Graph"}
        ){
            @Override public Class<?> getColumnClass(int c){
                return c==2?javax.swing.JProgressBar.class:(c==3?JButton.class:Object.class);
            }
            @Override public boolean isCellEditable(int r,int c){ return c==3;}
        };

        t1.setModel(model);
        t1.getColumnModel().getColumn(2).setCellRenderer(new ProgressRenderer());
        t1.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        t1.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));
        t1.setRowHeight(23);

        try{
            ParabitDBC db = new ParabitDBC();
            db.rs=db.stm.executeQuery("SELECT LocationName,CPName FROM checkpointtoll");
            while(db.rs.next()) model.addRow(new Object[]{db.rs.getString(1),db.rs.getString(2),0,"Graph"});
        }catch(Exception e){e.printStackTrace();}

        // ======================================================
        //  REPLACED OLD CHART AREA WITH PIE + BAR STACKED
        // ======================================================
        p1.setLayout(new java.awt.BorderLayout());

        JFXPanel piePanel = new JFXPanel();
        JFXPanel barPanel = new JFXPanel();

        piePanel.setPreferredSize(new Dimension(700,280));
        barPanel.setPreferredSize(new Dimension(700,280));

        p1.add(piePanel, java.awt.BorderLayout.NORTH);
        p1.add(barPanel, java.awt.BorderLayout.SOUTH);

        Platform.runLater(() -> {
            VehiclePieChart pie = new VehiclePieChart();
            piePanel.setScene(pie.getScene());

            VehicleBarGraph bar = new VehicleBarGraph();
            barPanel.setScene(bar.getScene());
        });

        setSize(1350,700);
        setLocationRelativeTo(null);
    }

    // ======================================================
    // BELOW TABLE + BUTTON RENDERER SECTION ***UNTOUCHED***
    // ======================================================

    class ProgressRenderer extends javax.swing.JProgressBar implements TableCellRenderer{
        public ProgressRenderer(){super(0,100);setStringPainted(true);}
        public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){setValue((int)v);return this;}
    }
    class ButtonRenderer extends JButton implements TableCellRenderer{
        public ButtonRenderer(){setOpaque(true);}
        public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){setText(v==null?"":v.toString());return this;}
    }
    class ButtonEditor extends DefaultCellEditor{
        JButton b; String l; boolean c;
        public ButtonEditor(JCheckBox box){super(box);b=new JButton();b.addActionListener(e->fireEditingStopped());}
        public Component getTableCellEditorComponent(JTable t,Object v,boolean s,int r,int c){l=v.toString();b.setText(l);this.c=true;return b;}
        public Object getCellEditorValue(){
            if(c) JOptionPane.showMessageDialog(null,"Graph Clicked (You can attach live chart)");
            c=false; return l;
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        p1=new javax.swing.JPanel(); jScrollPane1=new javax.swing.JScrollPane(); t1=new javax.swing.JTable();
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        jScrollPane1.setViewportView(t1);

        javax.swing.GroupLayout layout=new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createSequentialGroup().addGap(40)
            .addComponent(p1,700,700,700).addGap(30)
            .addComponent(jScrollPane1,520,520,520).addGap(40));
        layout.setVerticalGroup(layout.createSequentialGroup().addGap(30)
            .addGroup(layout.createParallelGroup().addComponent(p1,600,600,600)
            .addComponent(jScrollPane1,600,600,600)).addGap(30));
        pack();
    }
    public static void main(String args[]){java.awt.EventQueue.invokeLater(()->new CircleDirector().setVisible(true));}
    private javax.swing.JScrollPane jScrollPane1; private javax.swing.JPanel p1; private javax.swing.JTable t1;
}
