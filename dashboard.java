/* 
 * Features:
 * time and date
 * display current gear
 * light indicators
 * Speed
 * Temperature of the engine
 * rpm
 * fuel level
 */

import java.io.*;
import java.util.Properties;
import java.awt.*;
import javax.swing.*;

public class dashboard {
    // Variable declaration
    Properties config = new Properties();
    JPanel topPanel = new JPanel();
    JPanel middlePanel = new JPanel();
    JPanel bottomPanel = new JPanel();

    // everything here goes to the middle panel
    JPanel leftmidPanel = new JPanel();
    JPanel rightmidPanel = new JPanel();
    JPanel centermidPanel = new JPanel();

    // everything here goes to the centermid panel
    JPanel topCMPanel = new JPanel();
    JPanel bottomCMPanel = new JPanel();
    JPanel midleftCMPanel = new JPanel();
    JPanel midrightCMPanel = new JPanel();

    // default values for the interface
    Color backgroundColor;
    Color foregroundColor;
    Color buttonBorderColor;
    
    // Panels to display the info
    JPanel gearPanel = new JPanel();
    JPanel engineTempPanel = new JPanel();
    JPanel fuelLevelPanel = new JPanel();

    dashboard() {
        // Call the method to load the config
        loadConfig();
        // Get the colors from the config file
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        buttonBorderColor = backgroundColor == Color.BLACK
                ? Color.decode(config.getProperty("borderColor1"))
                : Color.decode(config.getProperty("borderColor2"));
        // Set up the dialog
        JDialog dialog = new JDialog();
        dialog.setTitle("Dashboard");
        dialog.setSize(1280, 720);
        dialog.setLayout(new BorderLayout());
        dialog.setBackground(backgroundColor);
        dialog.setForeground(foregroundColor);
        // Customize the panels
        customizeTopPanel();
        customizeMiddlePanel();
        customizeBottomPanel();
        // Adding everything together
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(middlePanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    // method to load the config file
    public void loadConfig() {
        try (FileInputStream fileInputStream = new FileInputStream("config.properties")) {
            config.load(fileInputStream);
        } catch (IOException e) {
            // For debugging
        }
    }
    // method to fetch a color based on the string
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
    // public method to set up the time
    public void customizeTopPanel() {
        topPanel.setBackground(backgroundColor);
        topPanel.setForeground(foregroundColor);
        //Adding the time
        JTextField timeAndDateField = new JTextField("Time: 19:20, Date: 2024-11-22");
        timeAndDateField.setHorizontalAlignment(JTextField.CENTER);
        timeAndDateField.setEditable(false);
        timeAndDateField.setBackground(backgroundColor);
        timeAndDateField.setForeground(foregroundColor);
        timeAndDateField.setBorder(BorderFactory.createLineBorder(buttonBorderColor, 2));
        new Time(timeAndDateField, "HH:mm:ss, yyyy-MM-dd");
        topPanel.add(timeAndDateField, BorderLayout.CENTER);
    }
    // public method to set up the middle panel
    public void customizeMiddlePanel() {
        // set mid panels size
        leftmidPanel.setPreferredSize(new Dimension(200,750));
        rightmidPanel.setPreferredSize(new Dimension(800,750));
        centermidPanel.setPreferredSize(new Dimension(200,750));
        // Customize the middle panel
        middlePanel.setBackground(backgroundColor);
        middlePanel.setForeground(foregroundColor);
        // Add the left side of the middle panel
        leftmidPanel.setBackground(backgroundColor);
        middlePanel.add(leftmidPanel, BorderLayout.WEST);
        // Add the right side of the middle panel
        leftmidPanel.setBackground(Color.YELLOW);
        middlePanel.add(rightmidPanel, BorderLayout.EAST);
        // Add the center of the middle panel
        centermidPanel.setBackground(Color.GREEN);
        middlePanel.add(centermidPanel, BorderLayout.CENTER);

    }
    // public method to set up the bottom panel
    public void customizeBottomPanel() {
        // Customize the bottom panel
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.setForeground(foregroundColor);
        bottomPanel.setLayout(new GridLayout(0, 2));
        // Initialize the buttons
        JButton backButton = new JButton("Go Back");
        JButton themesButton = new JButton("Change Theme");
        // Add the buttons to the bottom panel
        bottomPanel.add(backButton);
        bottomPanel.add(themesButton);
        // Customize the buttons and add a function to them
    }
}