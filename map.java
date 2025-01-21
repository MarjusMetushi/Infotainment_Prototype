import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
public class map {
    /*
     * Map application step by step guide
     * 1. Create the frame and the main interface
     * 2. Download the jar file of OSMDroid and move it here
     * 3. Read the documentation of OSMDroid
     * 4. Create the map
     * 5. Create the marker
     * 6. Create the overlay
     * 7. Create the zoom control
     * 8. Create the scale control
     */
    // Declare the variables
    JDialog dialog;
    JPanel topPanel = new JPanel();
    JPanel mainPanel = new JPanel();
    JPanel bottomPanel = new JPanel();
    Color backgroundColor;
    Color foregroundColor;
    Color buttonBorderColor;
    Properties config = new Properties();
    JButton exitButton = new JButton("Exit");
    map(){
        // Load config and get colors
        loadconfig();
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        if (backgroundColor == Color.BLACK) buttonBorderColor = Color.decode(config.getProperty("borderColor1"));
        else buttonBorderColor = Color.decode(config.getProperty("borderColor2"));
        // Start the dialog and costumize the interface
        dialog = new JDialog();
        dialog.setTitle("Map");
        dialog.setSize(800, 600);
        // Methods to costumize the panels
        costumizeTopPanel();
        costumizeMainPanel();
        costumizeBottomPanel();
        // Add the panels to the dialog
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    //Method to load the config file
    @SuppressWarnings("ConvertToTryWithResources")
    public void loadconfig() {
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
    public Color getColorFromString(String color) {
        return switch (color.toLowerCase()) {
            case "black" -> Color.BLACK;
            case "white" -> Color.WHITE;
            case "gray" -> Color.GRAY;
            case "red" -> Color.RED;
            case "blue" -> Color.BLUE;
            default -> Color.WHITE;
        };
    }
    // Method to costumize the top panel
    public void costumizeTopPanel() {
        JTextField timeAndDateField = new JTextField("Time: 19:20, Date: 2024-11-22");
        timeAndDateField.setHorizontalAlignment(JTextField.CENTER);
        timeAndDateField.setEditable(false);
        timeAndDateField.setBackground(backgroundColor);
        timeAndDateField.setForeground(foregroundColor);
        timeAndDateField.setBorder(BorderFactory.createLineBorder(buttonBorderColor, 2));
        new Time(timeAndDateField, "HH:mm:ss, yyyy-MM-dd");
        topPanel.add(timeAndDateField, BorderLayout.CENTER);
    }
    // Method to costumize the main panel
    public void costumizeMainPanel() {
        
    }
    // Method to costumize the bottom panel
    public void costumizeBottomPanel() {
        exitButton.setBackground(backgroundColor);
        exitButton.setForeground(foregroundColor);
        exitButton.setBorder(BorderFactory.createLineBorder(buttonBorderColor, 2));
        bottomPanel.add(exitButton, BorderLayout.CENTER);
    }
}
