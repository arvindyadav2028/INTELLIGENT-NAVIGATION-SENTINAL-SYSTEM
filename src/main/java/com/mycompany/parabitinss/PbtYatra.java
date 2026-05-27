/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.parabitinss;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author arvin
 */
public class PbtYatra extends javax.swing.JFrame implements Runnable{
    /**
     * Creates new form PbtYatri
     */
    ParabitDBC db1;
    private final JFXPanel fxPanel = new JFXPanel();
    
    public PbtYatra(String s) {
        initComponents();
        Border blackBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
        jl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        db1 = new ParabitDBC();
        try{
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            tableLoader();
            fxPanel.setPreferredSize(new Dimension(jPanel1.getWidth(), jPanel1.getHeight()));
            jPanel1.setLayout(new BorderLayout());
            jPanel1.add(fxPanel, BorderLayout.CENTER); // JavaFX panel takes bottom space

//            add(jPanel); // Add everything to the JFrame

            // Setup JavaFX Chart on FX thread
            Platform.runLater(() -> {
                try {
                    LiveChart lineChart = new LiveChart();
                    Scene scene = lineChart.initChart(); // new method that returns Scene
                    fxPanel.setScene(scene); // Set chart scene inside the fxPanel
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            setVisible(true);
//            SwingUtilities.invokeLater(ChartLive::new);
        }catch(Exception e){
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();  
            setSize(screenSize);
            setLocation(0, 0);
        }
        
        if(PbtEmpLog.a==1)
         {
           b1.setEnabled(false);
           b2.setEnabled(false);
          }
         else if(PbtEmpLog.a==7){
           b3.setEnabled(false);
         } 
         jl.setText(s);
    }

private void tableLoader(){
    Thread ob= new Thread(this);
    ob.start();
}
    
   @Override 
   public void run(){
       
       LocalDate lastDate=null;
       int d1= 0,d2= 0,d3= 0,d4= 0,d0= 0,rd1= 0,rd2= 0,rd3= 0,rd4= 0,rd0= 0;
        int ad1= 0,ad2= 0,ad3= 0,ad4= 0,ad0= 0,rrd1= 0,rrd2= 0,rrd3= 0,rrd4= 0,rrd0= 0;
        
       while(true){
       try{
           
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

                LocalDate curDate=LocalDate.now();
                
                if(!curDate.equals(lastDate))
                {
                    
                    //Arrival Date
                    db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ArrivalDate ='"+ curDate.minusDays(1) +"';");
                    if (db1.rs.next()) d0=db1.rs.getInt(1);
                    db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ArrivalDate ='"+(curDate)+"';");
                    if (db1.rs.next()) d1=db1.rs.getInt(1);
                    db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ArrivalDate ='"+ curDate.plusDays(1) +"';");
                    if (db1.rs.next()) d2=db1.rs.getInt(1);
                    db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ArrivalDate ='"+ curDate.plusDays(2) +"';");
                    if (db1.rs.next()) d3=db1.rs.getInt(1);
                    db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ArrivalDate ='"+ curDate.plusDays(3) +"';");
                    if (db1.rs.next()) d4=db1.rs.getInt(1);

                    // Return Date
                    db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ReturnDate ='"+ curDate.minusDays(1) +"' And Status=1;");
                    if (db1.rs.next()) rd0=db1.rs.getInt(1);
                    db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ReturnDate ='"+ curDate +"' And Status=1;");
                    if (db1.rs.next()) rd1=db1.rs.getInt(1);
                    db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ReturnDate ='"+ curDate.plusDays(1) +"' And Status=1;");
                    if (db1.rs.next()) rd2=db1.rs.getInt(1);
                    db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ReturnDate ='"+ curDate.plusDays(2) +"' And Status=1;");
                    if (db1.rs.next()) rd3=db1.rs.getInt(1);
                    db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ReturnDate ='"+ curDate.plusDays(3) +"' And Status=1;");
                    if (db1.rs.next()) rd4=db1.rs.getInt(1);
                    
                    lastDate=curDate;
                }
                
                db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ArrivalDate ='"+ curDate.minusDays(1) +"' And Status=1;");
                if (db1.rs.next()) ad0=db1.rs.getInt(1);
                db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ArrivalDate ='"+ curDate +"' And Status=1;");
                if (db1.rs.next()) ad1=db1.rs.getInt(1);
                db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ArrivalDate ='"+ curDate.plusDays(1) +"' And Status=1;");
                if (db1.rs.next()) ad2=db1.rs.getInt(1);
                db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ArrivalDate ='"+ curDate.plusDays(2) +"' And Status=1;");
                if (db1.rs.next()) ad3=db1.rs.getInt(1);
                db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ArrivalDate ='"+ curDate.plusDays(3) +"' And Status=1;");
                if (db1.rs.next()) ad4=db1.rs.getInt(1);

                db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ReturnDate ='"+ curDate.minusDays(1) +"' And Status=0;");
                if (db1.rs.next()) rrd0=db1.rs.getInt(1);
                db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ReturnDate ='"+ curDate +"' And SNo=0;");
                if (db1.rs.next()) rrd1=db1.rs.getInt(1);
                db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ReturnDate ='"+ curDate.plusDays(1) +"' And Status=0;");
                if (db1.rs.next()) rrd2=db1.rs.getInt(1);
                db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ReturnDate ='"+ curDate.plusDays(2) +"' And Status=0;");
                if (db1.rs.next()) rrd3=db1.rs.getInt(1);
                db1.rs=db1.stm.executeQuery("SELECT COUNT(*) FROM personalvehreg WHERE ReturnDate ='"+ curDate.plusDays(3) +"' And Status=0;");
                if (db1.rs.next()) rrd4=db1.rs.getInt(1);

                model.setValueAt(curDate.minusDays(1), 0, 5);
                model.setValueAt(curDate, 0, 1); 
                model.setValueAt(curDate.plusDays(1), 0, 2);  
                model.setValueAt(curDate.plusDays(2), 0, 3);  
                model.setValueAt(curDate.plusDays(3), 0, 4);

                model.setValueAt(d0, 1, 5);
                model.setValueAt(d1, 1, 1); 
                model.setValueAt(d2, 1, 2);  
                model.setValueAt(d3, 1, 3);  
                model.setValueAt(d4, 1, 4);

                model.setValueAt(ad0, 2, 5);
                model.setValueAt(ad1, 2, 1);    
                model.setValueAt(ad2, 2, 2);
                model.setValueAt(ad3, 2,3);
                model.setValueAt(ad4, 2, 4);

                model.setValueAt(d0-ad0, 3, 5);
                model.setValueAt(d1-ad1, 3, 1); 
                model.setValueAt(d2-ad2, 3, 2);  
                model.setValueAt(d3-ad3, 3, 3);  
                model.setValueAt(d4-ad4, 3, 4);

                model.setValueAt(rd0, 5, 5);
                model.setValueAt(rd1, 5, 1); 
                model.setValueAt(rd2, 5, 2);  
                model.setValueAt(rd3, 5, 3);  
                model.setValueAt(rd4, 5, 4);

                model.setValueAt(rrd0, 6, 5);
                model.setValueAt(rrd1, 6, 1);    
                model.setValueAt(rrd2, 6, 2);
                model.setValueAt(rrd3, 6,3);
                model.setValueAt(rrd4, 6, 4);

                model.setValueAt(rd0-rrd0, 7, 5);
                model.setValueAt(rd1-rrd1, 7, 1); 
                model.setValueAt(rd2-rrd2, 7, 2);  
                model.setValueAt(rd3-rrd3, 7, 3);  
                model.setValueAt(rd4-rrd4, 7, 4);
                
                Thread.sleep(100); 
                
             }
             catch(Exception e){
               System.out.print(e);
             }
       }
   }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        b1 = new javax.swing.JButton();
        b2 = new javax.swing.JButton();
        b3 = new javax.swing.JButton();
        jl = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        b4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        b1.setText("Vehicle Registration");
        b1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b1ActionPerformed(evt);
            }
        });

        b2.setText("Person Registration");
        b2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b2ActionPerformed(evt);
            }
        });

        b3.setText("Registration Check");
        b3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b3ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"", "02-06-2025", "03-06-2025", "04-06-2025", "05-06-2025", "01-06-2025"},
                {"Arive", "", "", "", "", ""},
                {"Arived", null, null, null, null, null},
                {"To Arive", null, null, null, null, null},
                {null, null, null, null, null, null},
                {"Return", null, null, null, null, null},
                {"Returned", null, null, null, null, null},
                {"To Return", null, null, null, null, null}
            },
            new String [] {
                "", "Today", "Tommorow", "The Day after Tommorow", "In Three Days", "Yesterday"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        b4.setText("Detailed Table");
        b4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 445, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 903, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(b1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(79, 79, 79)
                        .addComponent(b2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(74, 74, 74)
                        .addComponent(b3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 39, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(467, 467, 467)
                        .addComponent(b4))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(348, 348, 348)
                        .addComponent(jl, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(jl, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(63, 63, 63)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(b1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(b3, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(b4)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void b3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b3ActionPerformed
        PbtRegCheck check = new PbtRegCheck(new javax.swing.JFrame(), true);
        check.setLocationRelativeTo(null); 
        check.setVisible(true);
    }//GEN-LAST:event_b3ActionPerformed

    private void b2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_b2ActionPerformed

    private void b1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b1ActionPerformed
        PbtVhReg dialog = new PbtVhReg(new javax.swing.JFrame(), true);
        int width=(int) (this.getWidth()*0.9);
        int height=(int) (this.getHeight()*0.9);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(null); 
        dialog.setVisible(true);
    }//GEN-LAST:event_b1ActionPerformed

    private void b4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_b4ActionPerformed
        DetailedTable ob= new DetailedTable(this,true);
        ob.setLocationRelativeTo(this);
        ob.setVisible(true);
    }//GEN-LAST:event_b4ActionPerformed


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PbtYatra("Welcome"));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton b1;
    private javax.swing.JButton b2;
    private javax.swing.JButton b3;
    private javax.swing.JButton b4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel jl;
    // End of variables declaration//GEN-END:variables
}
