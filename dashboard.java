import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import java.util.Properties;
public class dashboard {
    // Main dialog panels
    JPanel topPanel = new JPanel();
    JPanel mainPanel = new JPanel(new GridLayout(4, 3));
    JPanel bottomPanel = new JPanel(new GridLayout(0,1));
    // Colors and properties
    Color backgroundColor = Color.BLACK;
    Color foregroundColor = Color.RED;
    Color buttonBorderColor = Color.RED;
    Color txtfieldbackgroundColor;
    Color txtfieldforegroundColor;
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
    int fuel = 40;
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
    // Textfield for time and date
    JTextField timeAndDateField = new JTextField();
    /*
     * TODO:
     * Improve the GUI
     * Simulate everything with the CAN bus and Run tests
     * Create a method to fetch information from the CAN Bus and update the values each second
     */
    dashboard() {
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
    public void customizeTopPanel() {
        topPanel.setLayout(new BorderLayout());
        topPanel.setBackground(backgroundColor);
        //Set up a textfield for time and date
        timeAndDateField = new JTextField("Time: 19:20, Date: 2024-11-22");
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
        fuelPanel.setLayout(new BoxLayout(fuelPanel, BoxLayout.Y_AXIS));
        fuelPanel.add(Box.createVerticalGlue()); 
        fuelPanel.add(new FuelBarPanel(fuel));
        fuelPanel.add(Box.createVerticalGlue()); 
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
        exitButton.addActionListener(e -> System.exit(0));
        CustomizeBtn(exitButton);
        CustomizeBtn(themeButton);
        bottomPanel.add(exitButton);
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