import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
public class dashboard {
    // Main dialog panels
    JPanel topPanel = new JPanel();
    JPanel mainPanel = new JPanel(new GridLayout(4, 3));
    JPanel bottomPanel = new JPanel(new GridLayout(0,2));
    // Colors and properties
    Color backgroundColor;
    Color foregroundColor;
    Color buttonBorderColor;
    Properties config = new Properties();
    // Panels for the information on the dashboard
    JPanel speedPanel = new JPanel();
    JPanel rpmPanel = new JPanel();
    JPanel fuelPanel = new JPanel();
    JPanel enginetempPanel = new JPanel();
    JPanel leftBlinkerPanel = new JPanel();
    JPanel rightBlinkerPanel = new JPanel();
    JPanel emergencyPanel = new JPanel();
    JPanel seatbeltPanel = new JPanel();
    JPanel headLightPanel = new JPanel();
    JPanel ABSPanel = new JPanel();
    JPanel gearPanel = new JPanel();
    JPanel coolantTempPanel = new JPanel();
    // Variables for values
    int speed = 50;
    int rpm = 1200;
    int fuel = 0;
    int engineTemp = 0;
    boolean leftBlinker = false;
    boolean rightBlinker = false;
    boolean emergency = false;
    boolean seatbelt = false;
    boolean headLight = false;
    boolean ABS = false;
    char gear = '1';
    int coolantTemp = 0;
    dashboard() {
        loadConfig();
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        buttonBorderColor = getColorFromString(config.getProperty("borderColor2"));
        JDialog dialog = new JDialog();
        dialog.setBackground(backgroundColor);
        dialog.setForeground(foregroundColor);
        dialog.setTitle("Dashboard");
        dialog.setSize(1280, 720);
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        customizeTopPanel();
        customizeMainPanel();
        customizeBottomPanel();
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
    public void customizeTopPanel() {
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(backgroundColor);
        //Set up a textfield for time and date
        JTextField timeAndDateField = new JTextField("Time: 19:20, Date: 2024-11-22");
        timeAndDateField.setHorizontalAlignment(JTextField.CENTER);
        timeAndDateField.setEditable(false);
        timeAndDateField.setBackground(backgroundColor);
        timeAndDateField.setForeground(foregroundColor);
        timeAndDateField.setBorder(BorderFactory.createLineBorder(buttonBorderColor, 2));
        new Time(timeAndDateField, "HH:mm:ss, yyyy-MM-dd");
        topPanel.add(timeAndDateField, BorderLayout.CENTER);
    }
    public void customizeMainPanel() {
        // Set up the main panel
        mainPanel.setLayout(new GridLayout(4, 3));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setForeground(foregroundColor);
        // Customize the panels
        customizePanel(speedPanel);
        customizePanel(rpmPanel);
        customizePanel(fuelPanel);
        customizePanel(enginetempPanel);
        customizePanel(leftBlinkerPanel);
        customizePanel(rightBlinkerPanel);
        customizePanel(emergencyPanel);
        customizePanel(seatbeltPanel);
        customizePanel(headLightPanel);
        customizePanel(ABSPanel);
        customizePanel(gearPanel);
        customizePanel(coolantTempPanel);
        // Add the information to the panels
        speedPanel.add(new SemiCircularSpeedometer(speed));
        rpmPanel.add(new SemiCircularRPMMeter(rpm));
        fuelPanel.add(new FuelBarPanel(fuel));
        // Add the panels to the main panel
        mainPanel.add(speedPanel);
        mainPanel.add(rpmPanel);
        mainPanel.add(fuelPanel);   
        mainPanel.add(enginetempPanel);
        mainPanel.add(leftBlinkerPanel);
        mainPanel.add(rightBlinkerPanel);
        mainPanel.add(emergencyPanel);
        mainPanel.add(seatbeltPanel);
        mainPanel.add(headLightPanel);
        mainPanel.add(ABSPanel);
        mainPanel.add(gearPanel);
        mainPanel.add(coolantTempPanel);    
    }
    public void customizeBottomPanel() {
        JButton exitButton = new JButton("Exit");
        JButton themeButton = new JButton("Change Theme");
        CustomizeBtn(exitButton);
        CustomizeBtn(themeButton);
        bottomPanel.add(exitButton);
        bottomPanel.add(themeButton);
    }
    // Method to customize the buttons
    public void CustomizeBtn(JButton btn) {
        btn.setBackground(backgroundColor);
        btn.setForeground(foregroundColor);
        btn.setBorder(BorderFactory.createLineBorder(buttonBorderColor,2));
        btn.setFocusable(false);
        btn.setFont(new Font("Arial",Font.BOLD,20));
    }
    // Method to customize the panels
    public void customizePanel(JPanel panel) {
        panel.setBackground(backgroundColor);
        panel.setForeground(foregroundColor);
        panel.setBorder(BorderFactory.createLineBorder(buttonBorderColor,2));
    }
}