import java.io.*;
import java.util.Properties;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
// Fix the fuelBar positioning
// Improve the GUI
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
    
    // Dialog val
    JDialog dialog;

    // values
    int currentSpeed = 0;
    int currentRpm = 0;
    int currentFuelLevel = 40;
    char gear = 0;
    int engineTemp = 0;

    // Theme array
    int currentTheme = 0;
    String[] backgroundColors = {"black", "white"};
    String[] foregroundColors = {"red", "blue"};
    
    // Get the light indicators
    ImageIcon leftIndicator = new ImageIcon("carIndicators\\leftIndicator.png");
    ImageIcon rightIndicator = new ImageIcon("carIndicators\\rightIndicator.png");
    
    // panel to display the blinkers
    JLabel blinkersPanelLeft = new JLabel();
    JLabel blinkersPanelRight = new JLabel();
    
    dashboard() throws InterruptedException {
        // Call the method to load the config
        loadConfig();
        // Get the colors from the config file
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        buttonBorderColor = backgroundColor == Color.BLACK
                ? Color.decode(config.getProperty("borderColor1"))
                : Color.decode(config.getProperty("borderColor2"));
        // Set up the dialog
        dialog = new JDialog();
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
    public void customizeMiddlePanel() throws InterruptedException {
        // set mid panels size
        leftmidPanel.setPreferredSize(new Dimension(200,750));
        rightmidPanel.setPreferredSize(new Dimension(200,750));
        centermidPanel.setPreferredSize(new Dimension(800,750));

        // Add the blinkers to the center of their respective panels
        leftmidPanel.add(blinkersPanelLeft, BorderLayout.CENTER);
        rightmidPanel.add(blinkersPanelRight, BorderLayout.CENTER);

        // Customize panels
        middlePanel.setBackground(backgroundColor);
        middlePanel.setForeground(foregroundColor);
        topCMPanel.setBackground(backgroundColor);
        bottomCMPanel.setBackground(backgroundColor);
        midleftCMPanel.setBackground(backgroundColor);
        midrightCMPanel.setBackground(backgroundColor);
        
        // Add the components to the centermid panel
        centermidPanel.add(topCMPanel, BorderLayout.NORTH);
        centermidPanel.add(bottomCMPanel, BorderLayout.SOUTH);
        centermidPanel.add(midleftCMPanel, BorderLayout.WEST);
        centermidPanel.add(midrightCMPanel, BorderLayout.EAST);

        // Add the left side of the middle panel
        leftmidPanel.setBackground(backgroundColor);
        middlePanel.add(leftmidPanel, BorderLayout.WEST);

        // Add the center of the middle panel
        centermidPanel.setBackground(backgroundColor);
        middlePanel.add(centermidPanel, BorderLayout.CENTER);

        // Add the right side of the middle panel
        rightmidPanel.setBackground(backgroundColor);
        middlePanel.add(rightmidPanel, BorderLayout.EAST);
        
        // Set panel sizes
        topCMPanel.setPreferredSize(new Dimension(800,200));
        bottomCMPanel.setPreferredSize(new Dimension(800,200));
        midleftCMPanel.setPreferredSize(new Dimension(300,200));
        midrightCMPanel.setPreferredSize(new Dimension(300,200));
        
        // Set colors
        fuelLevelPanel.setBackground(backgroundColor);
        gearPanel.setBackground(backgroundColor);
        engineTempPanel.setBackground(backgroundColor);
        midleftCMPanel.setBackground(backgroundColor);
        midrightCMPanel.setBackground(backgroundColor);

        //Test
        gearPanel.setPreferredSize(new Dimension(200,200));
        engineTempPanel.setPreferredSize(new Dimension(200,200));
        fuelLevelPanel.setPreferredSize(new Dimension(200,200));

        // Add everything together
        topCMPanel.add(gearPanel);
        topCMPanel.add(engineTempPanel);
        bottomCMPanel.add(fuelLevelPanel);
        midleftCMPanel.add(new SemiCircularSpeedometer(currentSpeed));
        midrightCMPanel.add(new SemiCircularRPMMeter(currentRpm));
        // TODO: add fuel level bar
        fuelLevelPanel.add(new FuelBarPanel(currentFuelLevel));
        bottomCMPanel.add(fuelLevelPanel);
        centermidPanel.add(topCMPanel, BorderLayout.NORTH);
        centermidPanel.add(midleftCMPanel, BorderLayout.WEST);
        centermidPanel.add(midrightCMPanel, BorderLayout.EAST);
        centermidPanel.add(bottomCMPanel, BorderLayout.SOUTH);
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
        backButton.addActionListener(e -> goback());
        themesButton.addActionListener(e -> changeTheme());
    }
    // Method to close the dialog
    public void goback(){
        dialog.dispose();
    }
   // Method for the blinkers to pulsate
    public void blink(boolean left) {
        // Timer to toggle the icon ON and OFF
        Timer timer = new Timer(500, null); // 500ms interval for blinking
        JLabel blinkerPanel = left ? blinkersPanelLeft : blinkersPanelRight;
        Icon indicator = left ? leftIndicator : rightIndicator;

        // ActionListener to toggle the icon
        timer.addActionListener(new ActionListener() {
            private boolean isOn = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (isOn) {
                    blinkerPanel.setIcon(null); // Turn OFF
                } else {
                    blinkerPanel.setIcon(indicator); // Turn ON
                    blinkerPanel.setHorizontalAlignment(SwingConstants.CENTER);
                    blinkerPanel.setVerticalAlignment(SwingConstants.CENTER);
                }
                isOn = !isOn; // Toggle state
            }
        });
        
        // Start the timer
        timer.start();
        // INTEGRATE IT WITH THE CAN BUS
        new Timer(5000, e -> {
            timer.stop(); 
            blinkerPanel.setIcon(null);
        }).start(); // Stops blinking after 5 seconds
    }

    // Method to change the theme
    public void changeTheme(){
        // TODO: change theme
    }
}