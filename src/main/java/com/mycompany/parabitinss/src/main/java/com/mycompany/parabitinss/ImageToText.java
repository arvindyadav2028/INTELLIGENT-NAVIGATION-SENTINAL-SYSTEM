/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.parabitinss;

import com.github.sarxos.webcam.Webcam;
import net.sourceforge.tess4j.Tesseract;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImageToText {

    public static void main(String[] args) {
        new ImageToText().start();
    }

    public void start() {
        // Open the default webcam
        java.util.List<Webcam> webcams = Webcam.getWebcams();
        Webcam webcam = webcams.get(1);
        if (webcam == null) {
            System.out.println("No webcam detected");
            return;
        }

        webcam.setViewSize(new Dimension(640, 480));
        webcam.open();

        // Setup GUI
        JFrame frame = new JFrame("ImageToText");
        JLabel cameraLabel = new JLabel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 480);
        frame.add(cameraLabel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Setup OCR
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("D:/Coding/Java Project/Test-OCR/tessdata");
        tesseract.setLanguage("eng");

        // Start capture thread
        new Thread(() -> {
            long lastOCRTime = System.currentTimeMillis();

            while (true) {
                
                BufferedImage image = webcam.getImage();
                if (image == null) continue;

                // Show feed
                Image scaled = image.getScaledInstance(cameraLabel.getWidth(), cameraLabel.getHeight(), Image.SCALE_SMOOTH);
                cameraLabel.setIcon(new ImageIcon(scaled));

                long now = System.currentTimeMillis();
                if (now - lastOCRTime >= 2000) {
                    try {
                        // Preprocess
                        BufferedImage preprocessed = preprocessImage(image);

                        // OCR
                        String result = tesseract.doOCR(preprocessed);

                        // Extract plate
                        String plate = extractPlateNumber(result);

                        if (plate != null) {
                            JOptionPane.showMessageDialog(frame, "Vehicle Number Detected:\n" + plate,
                                    "Scan Result", JOptionPane.INFORMATION_MESSAGE);
                            break; // Stop after successful detection
                        }

                        lastOCRTime = now;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    Thread.sleep(30);
                } catch (InterruptedException ignored) {
                }
            }

            webcam.close();
            System.exit(0);
        }).start();
    }

    private BufferedImage preprocessImage(BufferedImage image) {
        // Convert to grayscale
        BufferedImage gray = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g2d = gray.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // Binarize
        BufferedImage binary = new BufferedImage(gray.getWidth(), gray.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
        g2d = binary.createGraphics();
        g2d.drawImage(gray, 0, 0, null);
        g2d.dispose();

        return binary;
    }

    private String extractPlateNumber(String ocrResult) {
        if (ocrResult == null) return null;
        String sanitized = ocrResult.replaceAll("\\s", "").toUpperCase();
        Pattern platePattern = Pattern.compile("[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}");
        Matcher matcher = platePattern.matcher(sanitized);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}

