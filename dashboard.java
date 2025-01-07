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
import java.io.FileOutputStream;
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
    // Labels for the information on the dashboard
    JLabel engineTempLabel = new JLabel("Engine Temperature: 0°C");
    JLabel seatbeltLabel = new JLabel("Seatbelt: OFF");
    JLabel headLightLabel = new JLabel("Headlights: OFF");
    JLabel ABSLabel = new JLabel("ABS: OFF");
    JLabel gearLabel = new JLabel("Gear: N");
    JLabel coolantTempLabel = new JLabel("Coolant Temperature: 0°C");
    // Labels and icons for blinkers
    JLabel blinkersPanelLeft = new JLabel();
    JLabel blinkersPanelRight = new JLabel();
    JLabel hazardLightLabel = new JLabel();
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
    // Variables for the dashboard theme
    int currentDashboardTheme;
    // Buttons
    JButton themeButton = new JButton("Change Theme");
    JButton exitButton = new JButton("Exit");
    /*
     * TODO:
     * Fix the problem with saving into the config file
     * Improve the GUI
     * Simulate everything with the CAN bus and Run tests
     * Create a method to fetch information from the CAN Bus and update the values each second
     */
    dashboard() {
        loadConfig();
        changeTheme(0);
        changeEverythingToNewTheme();
        currentDashboardTheme = Integer.parseInt(config.getProperty("CurrentDashboardTheme"));
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
        hazardLightBlink();
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
        enginetempPanel.add(engineTempLabel);
        seatbeltPanel.add(seatbeltLabel);
        headLightPanel.add(headLightLabel);
        emergencyPanel.add(hazardLightLabel);
        ABSPanel.add(ABSLabel);
        gearPanel.add(gearLabel);
        coolantTempPanel.add(coolantTempLabel);
        // Add the panels to the main panel
        mainPanel.add(speedPanel);
        mainPanel.add(rpmPanel);
        mainPanel.add(fuelPanel);   
        mainPanel.add(leftBlinkerPanel);
        mainPanel.add(rightBlinkerPanel);
        mainPanel.add(emergencyPanel);
        mainPanel.add(enginetempPanel);
        mainPanel.add(seatbeltPanel);
        mainPanel.add(headLightPanel);
        mainPanel.add(ABSPanel);
        mainPanel.add(gearPanel);
        mainPanel.add(coolantTempPanel);    
    }
    // Method to customize the bottom panel
    public void customizeBottomPanel() {
        exitButton = new JButton("Exit");
        themeButton = new JButton("Change Theme");
        themeButton.addActionListener(e -> changeTheme(1));
        exitButton.addActionListener(e -> System.exit(0));
        CustomizeBtn(exitButton);
        CustomizeBtn(themeButton);
        bottomPanel.add(exitButton);
        bottomPanel.add(themeButton);
    }
    // Method to change the theme
    public void changeTheme(int addition) {
        currentDashboardTheme = (currentDashboardTheme + addition) % 4;
        config.setProperty("CurrentDashboardTheme", String.valueOf(currentDashboardTheme));
        try (FileOutputStream out = new FileOutputStream("config.properties")) {
            config.store(out, "Dashboard Theme changed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        if(currentDashboardTheme == 0) {
            backgroundColor = Color.BLACK;
            foregroundColor = Color.BLUE;
            changeEverythingToNewTheme();
        } else if(currentDashboardTheme == 1) {
            backgroundColor = Color.BLACK;
            foregroundColor = Color.RED;
            changeEverythingToNewTheme();
        } else if(currentDashboardTheme == 2) {
            backgroundColor = Color.WHITE;
            foregroundColor = Color.BLUE;
            changeEverythingToNewTheme();
        } else if(currentDashboardTheme == 3) {
            backgroundColor = Color.WHITE;
            foregroundColor = Color.RED;
            changeEverythingToNewTheme();
        }
    }
    // Method to change everything to the new theme
    public void changeEverythingToNewTheme() {
        // Change background of the panels
        topPanel.setBackground(backgroundColor);
        mainPanel.setBackground(backgroundColor);
        bottomPanel.setBackground(backgroundColor);
        speedPanel.setBackground(backgroundColor);
        rpmPanel.setBackground(backgroundColor);
        fuelPanel.setBackground(backgroundColor);
        enginetempPanel.setBackground(backgroundColor);
        leftBlinkerPanel.setBackground(backgroundColor);
        rightBlinkerPanel.setBackground(backgroundColor);
        emergencyPanel.setBackground(backgroundColor);
        seatbeltPanel.setBackground(backgroundColor);
        headLightPanel.setBackground(backgroundColor);
        ABSPanel.setBackground(backgroundColor);
        gearPanel.setBackground(backgroundColor);
        coolantTempPanel.setBackground(backgroundColor);
        blinkersPanelLeft.setBackground(backgroundColor);
        blinkersPanelRight.setBackground(backgroundColor);
        // Change foreground of the panels
        topPanel.setForeground(foregroundColor);
        mainPanel.setForeground(foregroundColor);
        bottomPanel.setForeground(foregroundColor);
        speedPanel.setForeground(foregroundColor);
        rpmPanel.setForeground(foregroundColor);
        fuelPanel.setForeground(foregroundColor);
        enginetempPanel.setForeground(foregroundColor);
        leftBlinkerPanel.setForeground(foregroundColor);
        rightBlinkerPanel.setForeground(foregroundColor);
        emergencyPanel.setForeground(foregroundColor);
        seatbeltPanel.setForeground(foregroundColor);
        headLightPanel.setForeground(foregroundColor);
        ABSPanel.setForeground(foregroundColor);
        gearPanel.setForeground(foregroundColor);
        coolantTempPanel.setForeground(foregroundColor);
        blinkersPanelLeft.setForeground(foregroundColor);
        blinkersPanelRight.setForeground(foregroundColor);
        engineTempLabel.setForeground(foregroundColor);
        seatbeltLabel.setForeground(foregroundColor);
        headLightLabel.setForeground(foregroundColor);
        // Change foreground of the labels
        ABSLabel.setForeground(foregroundColor);
        gearLabel.setForeground(foregroundColor);   
        coolantTempLabel.setForeground(foregroundColor);
        // Change background of the labels
        seatbeltLabel.setBackground(backgroundColor);
        headLightLabel.setBackground(backgroundColor);
        ABSLabel.setBackground(backgroundColor);
        gearLabel.setBackground(backgroundColor);
        coolantTempLabel.setBackground(backgroundColor);
        // Change border of the panels
        topPanel.setBorder(BorderFactory.createLineBorder(foregroundColor,2));
        bottomPanel.setBorder(BorderFactory.createLineBorder(foregroundColor,2));
        mainPanel.setBorder(BorderFactory.createLineBorder(foregroundColor,2));
        // Change foreground and background of the buttons
        exitButton.setBackground(backgroundColor);
        exitButton.setForeground(foregroundColor);
        themeButton.setBackground(backgroundColor);
        themeButton.setForeground(foregroundColor);
    }
    // Method to customize the buttons
    public void CustomizeBtn(JButton btn) {
        btn.setBackground(backgroundColor);
        btn.setForeground(foregroundColor);
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
    public void hazardLightBlink() {
        blink(true);
        blink(false);
        Timer timer = new Timer(500, null);
        JLabel blinkerPanel = hazardLightLabel;
        Icon indicator = triangle;
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
        timer.start();
        new Timer(5000, e -> {
            timer.stop(); 
            blinkerPanel.setIcon(null);
        });
    }
}