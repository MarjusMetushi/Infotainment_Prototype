/*
 * TODO:
 * 2. Make separate classes for these devices 
 * 3. Android -> Chromecast
 * 4. Iphone -> Rpi Play 
 */

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class screenMirroring {
    static JDialog dialog;
    static JPanel mainPanel;
    static Color backgroundColor;
    static Color foregroundColor;
    static Properties config = new Properties();    
    public screenMirroring() {
        loadconfig();
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        promptUser();
    }
    //Method to load the config file
    @SuppressWarnings("ConvertToTryWithResources")
    public static void loadconfig() {
        config = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream("config.properties");
            config.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            // Print stack trace for debugging
            
        }
    }
    //Method to fetch a color based on the string
    public static Color getColorFromString(String color) {
        return switch (color.toLowerCase()) {
            case "black" -> Color.BLACK;
            case "white" -> Color.WHITE;
            case "gray" -> Color.GRAY;
            case "red" -> Color.RED;
            case "blue" -> Color.BLUE;
            default -> Color.WHITE;
        };
    }
    public static void promptUser() {
        dialog = new JDialog();

        dialog.setTitle("Screen Mirroring");
        dialog.setSize(500, 500);
        dialog.setBackground(backgroundColor);
        dialog.setForeground(foregroundColor);

        mainPanel = new JPanel();
        mainPanel.setBackground(backgroundColor);
        mainPanel.setForeground(foregroundColor);

        JTextArea text = new JTextArea("Please select your device below...");
        text.setEditable(false);
        text.setFocusable(false);
        text.setBackground(backgroundColor);
        text.setForeground(foregroundColor);
        text.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));

        mainPanel.add(text);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setForeground(foregroundColor);

        JButton iphone = new JButton("Iphone");
        JButton android = new JButton("Android");

        iphone.setFocusable(false);
        android.setFocusable(false);
        iphone.setBackground(backgroundColor);
        iphone.setForeground(foregroundColor);
        android.setBackground(backgroundColor);
        android.setForeground(foregroundColor);

        iphone.addActionListener(e -> {
            try {
                // Install and open 5kplayer
                iphoneScreenMirroring();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        android.addActionListener(e -> {
            try {
                //Use raspberry pi miraclecast to mirror the android screen and make an installer and a set up code that will run on the raspberry pi
                connectandroid();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });

        buttonPanel.add(iphone);
        buttonPanel.add(android);

        dialog.add(mainPanel, BorderLayout.NORTH);
        dialog.add(buttonPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    public static void iphoneScreenMirroring() throws IOException {}
    public static void connectandroid() throws IOException {}
}
