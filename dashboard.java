import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    // Labels and icons for blinkers
    JLabel blinkersPanelLeft = new JLabel();
    JLabel blinkersPanelRight = new JLabel();
    Icon leftIndicator = new ImageIcon("carIndicators\\leftIndicator.png");
    Icon rightIndicator = new ImageIcon("carIndicators\\rightIndicator.png");
    Icon triangle = new ImageIcon("carIndicators\\hazardLight.png");
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
    /*
     * TODO:
     * Fix the blinking method to work for the blinkers and hazard lights - Problem: assign separate panels to separate icons
     * Simulate everything with the CAN bus and Run tests
     * Create a method to change the theme and add the theme information to the config file
     * Create a method to fetch information from the CAN Bus and update the values each second
     */
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
        rightBlinkerPanel.add(blinkersPanelRight);
        leftBlinkerPanel.add(blinkersPanelLeft);
        blink(true);
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
        enginetempPanel.add(new JLabel("Engine Temperature: "+(engineTemp + "°C")));
        headLightPanel.add(new JLabel("Headlights: "+(headLight ? "ON" : "OFF")));
        seatbeltPanel.add(new JLabel("Seatbelt: " + (seatbelt ? "ON" : "OFF")));
        emergencyPanel.add(new JLabel("Emergency: " + (emergency ? "ON" : "OFF")));
        ABSPanel.add(new JLabel("Seatbelt: " + (ABS ? "ON" : "OFF")));
        gearPanel.add(new JLabel("Gear: "+gear));
        coolantTempPanel.add(new JLabel("Coolant Temperature: "+coolantTemp + "°C"));   
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
    // Method to customize the bottom panel
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
}