import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
// Order: speed, fuel level, coolant temp, engine temp, gear, rpm, throttle pos, mass air flow, fuel rate, timing advance, intake air temp, oxygen sensors, fuel pressure, ambient air temp, engine coolant temp, barometric pressure, short term fuel trim, long term fuel trim, fuel type, control module voltage
public class dashboard {
    JDialog dialog;
    // Main dialog panels
    JPanel topPanel = new JPanel();
    JPanel mainPanel = new JPanel(new GridLayout(4, 4));
    JPanel bottomPanel = new JPanel(new GridLayout(0,1));
    // Colors and properties
    Color backgroundColor = Color.BLACK;
    Color foregroundColor = Color.WHITE;
    Color txtfieldbackgroundColor;
    Color txtfieldforegroundColor;
    // Panels for the information on the dashboard
    JPanel speedPanel = new JPanel();
    JPanel fuelPanel = new JPanel();
    JPanel coolantTempPanel = new JPanel();
    JPanel rpmPanel = new JPanel();
    JPanel throttlePositionPanel = new JPanel();
    JPanel massAirFlowPanel = new JPanel();
    JPanel fuelRatePanel = new JPanel();
    JPanel timingAdvancePanel = new JPanel();
    JPanel intakeAirTempPanel = new JPanel();
    JPanel fuelPressurePanel = new JPanel();
    JPanel ambientAirTempPanel = new JPanel();
    JPanel barometricPressurePanel = new JPanel();
    JPanel shortTermFuelTrimPanel = new JPanel();
    JPanel longTermFuelTrimPanel = new JPanel();
    JPanel controlModuleVoltagePanel = new JPanel();
    
    // Labels for the information on the dashboard
    JLabel coolantTempLabel = new JLabel();
    JLabel throttlePositionLabel = new JLabel();
    JLabel massAirFlowLabel = new JLabel();
    JLabel fuelRateLabel = new JLabel();
    JLabel timingAdvanceLabel = new JLabel();
    JLabel intakeAirTempLabel = new JLabel();
    JLabel fuelPressureLabel = new JLabel();
    JLabel ambientAirTempLabel = new JLabel();
    JLabel barometricPressureLabel = new JLabel();
    JLabel shortTermFuelTrimLabel = new JLabel();
    JLabel longTermFuelTrimLabel = new JLabel();
    JLabel controlModuleVoltageLabel = new JLabel();
    
    // Variables for values
    int speed = 50;
    int rpm = 1200;
    int fuel = 40;
    int coolantTemp = 0;
    int throttlePosition = 0;
    int massAirFlow = 0;
    int fuelRate = 0;
    int timingAdvance = 0;
    int intakeAirTemp = 0;
    int fuelPressure = 0;
    int ambientAirTemp = 0;
    int barometricPressure = 0;
    int shortTermFuelTrim = 0;
    int longTermFuelTrim = 0;
    int controlModuleVoltage = 0;
    
    // Exit Button
    JButton exitButton = new JButton("Exit");
    // Textfield for time and date
    JTextField timeAndDateField = new JTextField();
    /*
     * TODO:
     * Simulate everything with the CAN bus and Run tests
     * Create a method/Class to fetch information from the CAN Bus and update the values each second
     */
    dashboard() throws FileNotFoundException, IOException {
        // Start the thread to fetch information from the CAN bus
        //fetchInformation();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    fetchInformation();
                } catch (IOException e) {
                    // Debugging
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        // set up the main interface
        dialog = new JDialog();
        dialog.setBackground(backgroundColor);
        dialog.setForeground(foregroundColor);
        dialog.setTitle("Dashboard");
        dialog.setSize(1280, 720);
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        // Customize the panels
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
        mainPanel.setLayout(new GridLayout(4, 4));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setForeground(foregroundColor);
        // Customize the panels
        customizePanel(speedPanel);
        customizePanel(rpmPanel);
        customizePanel(fuelPanel);
        customizePanel(coolantTempPanel);
        customizePanel(throttlePositionPanel);
        customizePanel(massAirFlowPanel);
        customizePanel(fuelRatePanel);
        customizePanel(timingAdvancePanel);
        customizePanel(intakeAirTempPanel);
        customizePanel(fuelPressurePanel);
        customizePanel(ambientAirTempPanel);
        customizePanel(barometricPressurePanel);
        customizePanel(shortTermFuelTrimPanel);
        customizePanel(longTermFuelTrimPanel);
        customizePanel(controlModuleVoltagePanel);
    
        // Add the information to the panels and center the labels
        speedPanel.add(new SemiCircularSpeedometer(speed));
        
        rpmPanel.add(new SemiCircularRPMMeter(rpm));
        
        fuelPanel.setLayout(new BoxLayout(fuelPanel, BoxLayout.Y_AXIS));
        fuelPanel.removeAll();
        fuelPanel.add(Box.createVerticalGlue());
        fuelPanel.add(new FuelBarPanel(fuel));
        fuelPanel.add(Box.createVerticalGlue());
        fuelPanel.revalidate();
        fuelPanel.repaint();
        
        // Coolant Temperature
        coolantTempLabel.setText("Coolant Temperature: \n" + coolantTemp);
        coolantTempPanel.setLayout(new BoxLayout(coolantTempPanel, BoxLayout.Y_AXIS));
        Box coolantTempBox = Box.createHorizontalBox(); 
        coolantTempBox.add(Box.createHorizontalGlue()); 
        coolantTempBox.add(coolantTempLabel);  
        coolantTempBox.add(Box.createHorizontalGlue()); 
        coolantTempPanel.add(Box.createVerticalGlue()); 
        coolantTempPanel.add(coolantTempBox); 
        coolantTempPanel.add(Box.createVerticalGlue()); 
        customizeLabel(coolantTempLabel);
        // Throttle Position
        throttlePositionLabel.setText("Throttle Position: \n" + throttlePosition);
        throttlePositionPanel.setLayout(new BoxLayout(throttlePositionPanel, BoxLayout.Y_AXIS));
        Box throttlePositionBox = Box.createHorizontalBox();
        throttlePositionBox.add(Box.createHorizontalGlue());
        throttlePositionBox.add(throttlePositionLabel);
        throttlePositionBox.add(Box.createHorizontalGlue());
        throttlePositionPanel.add(Box.createVerticalGlue());
        throttlePositionPanel.add(throttlePositionBox);
        throttlePositionPanel.add(Box.createVerticalGlue());
        customizeLabel(throttlePositionLabel);
        // Mass Air Flow
        massAirFlowLabel.setText("Mass Air Flow: \n" + massAirFlow);
        massAirFlowPanel.setLayout(new BoxLayout(massAirFlowPanel, BoxLayout.Y_AXIS));
        Box massAirFlowBox = Box.createHorizontalBox();
        massAirFlowBox.add(Box.createHorizontalGlue());
        massAirFlowBox.add(massAirFlowLabel);
        massAirFlowBox.add(Box.createHorizontalGlue());
        massAirFlowPanel.add(Box.createVerticalGlue());
        massAirFlowPanel.add(massAirFlowBox);
        massAirFlowPanel.add(Box.createVerticalGlue());
        customizeLabel(massAirFlowLabel);
        // Fuel Rate
        fuelRateLabel.setText("Fuel Rate: \n" + fuelRate);
        fuelRatePanel.setLayout(new BoxLayout(fuelRatePanel, BoxLayout.Y_AXIS));
        Box fuelRateBox = Box.createHorizontalBox();
        fuelRateBox.add(Box.createHorizontalGlue());
        fuelRateBox.add(fuelRateLabel);
        fuelRateBox.add(Box.createHorizontalGlue());
        fuelRatePanel.add(Box.createVerticalGlue());
        fuelRatePanel.add(fuelRateBox);
        fuelRatePanel.add(Box.createVerticalGlue());
        customizeLabel(fuelRateLabel);
        // Timing Advance
        timingAdvanceLabel.setText("Timing Advance: \n" + timingAdvance);
        timingAdvancePanel.setLayout(new BoxLayout(timingAdvancePanel, BoxLayout.Y_AXIS));
        Box timingAdvanceBox = Box.createHorizontalBox();
        timingAdvanceBox.add(Box.createHorizontalGlue());
        timingAdvanceBox.add(timingAdvanceLabel);
        timingAdvanceBox.add(Box.createHorizontalGlue());
        timingAdvancePanel.add(Box.createVerticalGlue());
        timingAdvancePanel.add(timingAdvanceBox);
        timingAdvancePanel.add(Box.createVerticalGlue());
        customizeLabel(timingAdvanceLabel);
        // Intake Air Temperature
        intakeAirTempLabel.setText("Intake Air Temperature: \n" + intakeAirTemp);
        intakeAirTempPanel.setLayout(new BoxLayout(intakeAirTempPanel, BoxLayout.Y_AXIS));
        Box intakeAirTempBox = Box.createHorizontalBox();
        intakeAirTempBox.add(Box.createHorizontalGlue());
        intakeAirTempBox.add(intakeAirTempLabel);
        intakeAirTempBox.add(Box.createHorizontalGlue());
        intakeAirTempPanel.add(Box.createVerticalGlue());
        intakeAirTempPanel.add(intakeAirTempBox);
        intakeAirTempPanel.add(Box.createVerticalGlue());
        customizeLabel(intakeAirTempLabel);
        // Fuel Pressure
        fuelPressureLabel.setText("Fuel Pressure: \n" + fuelPressure);
        fuelPressurePanel.setLayout(new BoxLayout(fuelPressurePanel, BoxLayout.Y_AXIS));
        Box fuelPressureBox = Box.createHorizontalBox();
        fuelPressureBox.add(Box.createHorizontalGlue());
        fuelPressureBox.add(fuelPressureLabel);
        fuelPressureBox.add(Box.createHorizontalGlue());
        fuelPressurePanel.add(Box.createVerticalGlue());
        fuelPressurePanel.add(fuelPressureBox);
        fuelPressurePanel.add(Box.createVerticalGlue());
        customizeLabel(fuelPressureLabel);
        // Ambient Air Temperature
        ambientAirTempLabel.setText("Ambient Air Temperature: \n" + ambientAirTemp);
        ambientAirTempPanel.setLayout(new BoxLayout(ambientAirTempPanel, BoxLayout.Y_AXIS));
        Box ambientAirTempBox = Box.createHorizontalBox();
        ambientAirTempBox.add(Box.createHorizontalGlue());
        ambientAirTempBox.add(ambientAirTempLabel);
        ambientAirTempBox.add(Box.createHorizontalGlue());
        ambientAirTempPanel.add(Box.createVerticalGlue());
        ambientAirTempPanel.add(ambientAirTempBox);
        ambientAirTempPanel.add(Box.createVerticalGlue());
        customizeLabel(ambientAirTempLabel);
        // Barometric Pressure
        barometricPressureLabel.setText("Barometric Pressure: \n" + barometricPressure);
        barometricPressurePanel.setLayout(new BoxLayout(barometricPressurePanel, BoxLayout.Y_AXIS));
        Box barometricPressureBox = Box.createHorizontalBox();
        barometricPressureBox.add(Box.createHorizontalGlue());
        barometricPressureBox.add(barometricPressureLabel);
        barometricPressureBox.add(Box.createHorizontalGlue());
        barometricPressurePanel.add(Box.createVerticalGlue());
        barometricPressurePanel.add(barometricPressureBox);
        barometricPressurePanel.add(Box.createVerticalGlue());
        customizeLabel(barometricPressureLabel);
        // Short Term Fuel Trim
        shortTermFuelTrimLabel.setText("Short Term Fuel Trim: \n" + shortTermFuelTrim);
        shortTermFuelTrimPanel.setLayout(new BoxLayout(shortTermFuelTrimPanel, BoxLayout.Y_AXIS));
        Box shortTermFuelTrimBox = Box.createHorizontalBox();
        shortTermFuelTrimBox.add(Box.createHorizontalGlue());
        shortTermFuelTrimBox.add(shortTermFuelTrimLabel);
        shortTermFuelTrimBox.add(Box.createHorizontalGlue());
        shortTermFuelTrimPanel.add(Box.createVerticalGlue());
        shortTermFuelTrimPanel.add(shortTermFuelTrimBox);
        shortTermFuelTrimPanel.add(Box.createVerticalGlue());
        customizeLabel(shortTermFuelTrimLabel);
        // Long Term Fuel Trim
        longTermFuelTrimLabel.setText("Long Term Fuel Trim: \n" + longTermFuelTrim);
        longTermFuelTrimPanel.setLayout(new BoxLayout(longTermFuelTrimPanel, BoxLayout.Y_AXIS));
        Box longTermFuelTrimBox = Box.createHorizontalBox();
        longTermFuelTrimBox.add(Box.createHorizontalGlue());
        longTermFuelTrimBox.add(longTermFuelTrimLabel);
        longTermFuelTrimBox.add(Box.createHorizontalGlue());
        longTermFuelTrimPanel.add(Box.createVerticalGlue());
        longTermFuelTrimPanel.add(longTermFuelTrimBox);
        longTermFuelTrimPanel.add(Box.createVerticalGlue());
        customizeLabel(longTermFuelTrimLabel);
        // Control Module Voltage
        controlModuleVoltageLabel.setText("Control Module Voltage: \n" + controlModuleVoltage);
        controlModuleVoltagePanel.setLayout(new BoxLayout(controlModuleVoltagePanel, BoxLayout.Y_AXIS));
        Box controlModuleVoltageBox = Box.createHorizontalBox();
        controlModuleVoltageBox.add(Box.createHorizontalGlue());
        controlModuleVoltageBox.add(controlModuleVoltageLabel);
        controlModuleVoltageBox.add(Box.createHorizontalGlue());
        controlModuleVoltagePanel.add(Box.createVerticalGlue());
        controlModuleVoltagePanel.add(controlModuleVoltageBox);
        controlModuleVoltagePanel.add(Box.createVerticalGlue());
        customizeLabel(controlModuleVoltageLabel);
    
        // Add all panels to the main panel
        mainPanel.add(speedPanel);
        mainPanel.add(rpmPanel);
        mainPanel.add(fuelPanel);
        mainPanel.add(coolantTempPanel);
        mainPanel.add(throttlePositionPanel);
        mainPanel.add(massAirFlowPanel);
        mainPanel.add(fuelRatePanel);
        mainPanel.add(timingAdvancePanel);
        mainPanel.add(intakeAirTempPanel);
        mainPanel.add(fuelPressurePanel);
        mainPanel.add(ambientAirTempPanel);
        mainPanel.add(barometricPressurePanel);
        mainPanel.add(shortTermFuelTrimPanel);
        mainPanel.add(longTermFuelTrimPanel);
        mainPanel.add(controlModuleVoltagePanel);
    }
    
    
    // Method to customize the bottom panel
    public void customizeBottomPanel() {
        exitButton = new JButton("Exit");
        exitButton.addActionListener(
            new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            }
        );
        CustomizeBtn(exitButton);
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
    }
    public void customizeLabel(JLabel label) {
        label.setBackground(backgroundColor);
        label.setForeground(foregroundColor);
    }
   
    public void fetchInformation() throws FileNotFoundException, IOException {
        int[] data = new int[15];
        try (BufferedReader br = new BufferedReader(new FileReader("carInfo.txt"))) {
            String ln;
            int indx = 0;

            // Read each line
            while ((ln = br.readLine()) != null && indx < data.length) {
                String[] parts = ln.split(":");
                if (parts.length > 1) {
                    try {
                        data[indx] = Integer.parseInt(parts[1].trim());
                        indx += 1;
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing " + parts[1].trim());
                    }
                }
            }
        }

        // Update values
        speed = data[0];
        fuel = data[1];
        coolantTemp = data[2];
        rpm = data[3];
        throttlePosition = data[4];
        massAirFlow = data[5];
        fuelRate = data[6];
        timingAdvance = data[7];
        intakeAirTemp = data[8];
        fuelPressure = data[9];
        ambientAirTemp = data[10];
        barometricPressure = data[11];
        shortTermFuelTrim = data[12];
        longTermFuelTrim = data[13];
        controlModuleVoltage = data[14];

        // Dynamically update the UI
        SwingUtilities.invokeLater(() -> customizeMainPanel());
    }

}