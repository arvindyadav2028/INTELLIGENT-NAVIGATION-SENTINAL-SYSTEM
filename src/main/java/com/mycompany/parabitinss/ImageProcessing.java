/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.parabitinss;

import com.github.sarxos.webcam.Webcam;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import net.sourceforge.tess4j.Tesseract;

/**
 *
 * @author arvin
 */
public class ImageProcessing {
    public ImageProcessing(){
         imgToText();
    }
    
    public void imgToText() {
    List<Webcam> webcams = Webcam.getWebcams();
    Webcam webcam = webcams.get(1);
    webcam.setViewSize(new Dimension(640, 480));
    webcam.open();

    JFrame frame = new JFrame("ImageToText");
    JLabel cameraLabel = new JLabel();
    frame.setSize(640, 480);
    frame.add(cameraLabel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);

    Tesseract convertor = new Tesseract();
    convertor.setDatapath("D:/Coding/Java Project/Test-OCR/tessdata");
    convertor.setLanguage("eng");

    new Thread(() -> {
    
        long lastOCRTime = System.currentTimeMillis();
        String text="";
        while (true) {
            BufferedImage image = webcam.getImage();
            if (image == null) {
                continue;
            }

            // Show camera feed
            Image scaled = image.getScaledInstance(cameraLabel.getWidth(), cameraLabel.getHeight(), Image.SCALE_SMOOTH);
            cameraLabel.setIcon(new ImageIcon(scaled));

            // Check if 2 seconds have passed
            long now = System.currentTimeMillis();
            if (now - lastOCRTime >= 2000) {
                try {
                    File myFile = new File("C:/Users/arvin/Desktop/imageoutput.txt");
                    String result = convertor.doOCR(image);
                    int i= 0;
                    FileWriter writer = new FileWriter(myFile, true);
                     if (result!=null) {
                         text="";
                         while(i<result.length()){
                            if((result.charAt(i)>='A' && result.charAt(i)<='Z') || (result.charAt(i)>='0' && result.charAt(i)<='9')){
                                if(result.charAt(i)=='O'){
                                    text=text+'0';
                                }
                                else if(result.charAt(i)=='S' || result.charAt(i)=='s'){
                                    text=text+'5';
                                }
                                else{
                                    text+=result.charAt(i);
                                }
                            }
                            i++;
                            if(text.length()==10){
                                break;
                            }
                         }               
                   }
                  if(text.length()==10){
                     JOptionPane.showMessageDialog(frame,"Vehicle Number Detected:\n" + text,"Scan Result",JOptionPane.INFORMATION_MESSAGE);
                     writer.append(result);
                     writer.close(); 
                     break; 
                  }
                  lastOCRTime = now;
                  Thread.sleep(30);
                } catch (Exception e) {
                    System.out.println(e);
                } 
            }
            try {
                Thread.sleep(30);
            } 
            catch (InterruptedException e){
                System.out.println(e);
            }
        }
      webcam.close();
      PbtRegCheck check = new PbtRegCheck(new javax.swing.JFrame(), true);
      check.tf1.setText(text);
      check.b1.doClick();
      check.setLocationRelativeTo(null); 
      System.exit(0); 
    }).start(); 
   }
}
