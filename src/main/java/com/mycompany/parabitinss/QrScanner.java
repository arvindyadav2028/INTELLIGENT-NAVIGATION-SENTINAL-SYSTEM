package com.mycompany.parabitinss;

import com.github.sarxos.webcam.Webcam;
import com.google.zxing.*;
import com.google.zxing.client.j2se.*;
import com.google.zxing.common.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.List;

public class QrScanner {
    String text;
    public QrScanner() {
//        Webcam webcam = Webcam.getDefault();
        List<Webcam> webcams = Webcam.getWebcams();

        if (webcams.isEmpty()) {
            System.out.println("No cameras found!");
            return;
        }

        System.out.println("Available cameras:");
        for (int i = 0; i < webcams.size(); i++) {
            System.out.println(i + ": " + webcams.get(i).getName());
        }
        
        int i=Integer.parseInt(JOptionPane.showInputDialog("Select WebCame"));
        
        Webcam webcam = webcams.get(i); 
        webcam.setViewSize(new java.awt.Dimension(640,480));
        webcam.open();

        JFrame frame = new JFrame("Camera QR Scanner");
        JLabel cameraLabel = new JLabel();
        frame.setSize(640,480);
        frame.add(cameraLabel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

         new Thread(()->{
            try{
            while (true) {
                BufferedImage image = webcam.getImage();
                if (image == null) {
                    continue;
                }

                Image scaled = image.getScaledInstance(cameraLabel.getWidth(), cameraLabel.getHeight(), Image.SCALE_SMOOTH);
                cameraLabel.setIcon(new ImageIcon(scaled));

                 // QR decoding
                    LuminanceSource source = new BufferedImageLuminanceSource(image);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                    Result result = null;
                    try {
                        result = new MultiFormatReader().decode(bitmap);
                    } catch (NotFoundException nfe) {
                        // No QR code found in this frame → ignore
                    }

                    if (result != null) {
                        text = result.getText();
                        System.out.println("Scanned: " + text);

                        if (text.contains(",")) {
                            // take second value and remove quotes
                            text = text.split(",")[1].replace("'", "").trim();
                            System.out.println("Vehicle No: " + text);
                            
                            PbtVhReg dialog = new PbtVhReg(null, true, text);
                            dialog.setLocationRelativeTo(null);
                            dialog.setVisible(true);
                        }
                    }

                    Thread.sleep(50); // smooth loop
                }
            }catch(Exception e){
                System.out.println(e);
            }
            webcam.close();
        }).start();   
    }
    
   public static void main(String[] args) {
        new QrScanner();
    }
}