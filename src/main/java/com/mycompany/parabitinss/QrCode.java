/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.parabitinss;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author arvin
 */
class QrCode {
    ParabitDBC db1;
    public QrCode(String chk){
     db1=new ParabitDBC();
     qrCodeWriter(chk);  
    }
    void qrCodeWriter(String chk){
        String s="Select * from personalvehreg where VNo='"+chk+"';";
         try{
             db1.rs=db1.stm.executeQuery(s);
             if(db1.rs.next()){
                QRCodeWriter  qrWriter = new QRCodeWriter();
                String text="'"+db1.rs.getString("VRegNo")+"',"
                        + "'"+db1.rs.getString("VNo")+"',"
                        + "'"+db1.rs.getString("RTORegNo")+"',"
                        + "'"+db1.rs.getString("VName")+"',"
                        + "'"+db1.rs.getString("OwnerName")+"',"
                        + "'"+db1.rs.getString("OwnerMob")+"',"
                        + "'"+db1.rs.getString("EmergencyName")+"',"
                        + "'"+db1.rs.getString("EmergencyMob")+"'";
                
                BitMatrix myQr= qrWriter.encode(text,BarcodeFormat.QR_CODE,400,400);
                BufferedImage qrImg = MatrixToImageWriter.toBufferedImage(myQr); 
                ImageIcon icon = new ImageIcon(qrImg);
                
                JLabel label = new JLabel();
                label.setIcon(icon);
                JButton b1 = new JButton("Download");
                JFrame frame = new JFrame("QR Code");
                frame.setLayout(new BorderLayout());
                frame.add(label, BorderLayout.CENTER);
                frame.add(b1, BorderLayout.SOUTH);
                b1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Save QR Code");
                        int userSelection = fileChooser.showSaveDialog(frame);
                        if (userSelection == JFileChooser.APPROVE_OPTION) {
                            File fileToSave = fileChooser.getSelectedFile();
                            try {
                                ImageIO.write(qrImg, "png", new File(fileToSave.getAbsolutePath() + ".png"));
                                JOptionPane.showMessageDialog(frame, "QR Code saved successfully!");
                            } catch (IOException ex) {
                                JOptionPane.showMessageDialog(frame, "Error saving QR Code:\n" + ex.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                          } 
                       }
                 });
                
                frame.pack();  
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
             }
             else {
                    JOptionPane.showMessageDialog(null, "No vehicle found with number: " + chk);
                }
             
         } catch (WriterException ex) {
            JOptionPane.showMessageDialog(null, "Error generating QR code:\n" + ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
        }
         catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error generating QR code:\n" + ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
            
        }
     }
}
