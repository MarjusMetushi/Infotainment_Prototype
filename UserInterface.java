

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;
import javax.swing.*;
import javax.swing.border.LineBorder;
/*
    TO DO!
 *  Make the volume work
 *  Add the dashcam functionality
 *  Add the functionality for the music
 *  Update the speed continuously in a thread
 *  IMPLEMENT THE LOGIC FOR THE MARQUEE
 */
public class UserInterface extends JFrame {
    //Setting up basic settings
    int uiWidth = 1280;
    int uiHeight = 720;
    Properties config = new Properties();
    Color backgroundColor;
    Color foregroundColor;
    Color buttonBorderColor;
    //Setting up the panels
    JPanel topPanel = new JPanel(new BorderLayout());
    JPanel bottomPanel = new JPanel(new GridBagLayout()); 
    JPanel bottomLeftPanel = new JPanel(new GridLayout(2, 1));
    JPanel bottomRightPanel = new JPanel(new BorderLayout());
    //Constructor to set up the UI
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public UserInterface() {
        //Loading settings 
        loadConfig();
        //Getting the background and foreground colors from the properties file and getting the color from a string
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        if(backgroundColor == Color.BLACK)buttonBorderColor = Color.decode(config.getProperty("borderColor1"));
        else buttonBorderColor = Color.decode(config.getProperty("borderColor2"));
        //Setting up the frame
        setTitle("M37U5H1");
        setSize(uiWidth, uiHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setForeground(foregroundColor);
        //Adding components to the frame
        addComponentsTop();
        addComponentsBottomLeft();
        addComponentsBottomRight();
        //Costumize the panels
        topPanel.setBackground(backgroundColor);
        topPanel.setForeground(foregroundColor);
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.setForeground(foregroundColor);
        bottomLeftPanel.setBackground(backgroundColor);
        bottomLeftPanel.setForeground(foregroundColor);
        bottomRightPanel.setBackground(backgroundColor);
        bottomRightPanel.setForeground(foregroundColor);
        // Add bottom panels with GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 1; // Set grid width for left panel
        bottomPanel.add(bottomLeftPanel, gbc);
        gbc.gridwidth = 2; // Set grid width for right panel (the wider panel)
        bottomPanel.add(bottomRightPanel, gbc);
        //Adding everything together
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
    }
    //Method to load the configurations
    @SuppressWarnings("ConvertToTryWithResources")
    public void loadConfig(){
        config = new Properties();
        try {
            // Load properties from a file 
            FileInputStream fileInputStream = new FileInputStream("config.properties");
            config.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            //No catch
        }
    }
    //Method to add components to the top panel and costumize
    public void addComponentsTop() {
        //Set time and date
        JTextField timeAndDateField = new JTextField("Time: 12:00, Date: 2024-11-16");
        //Costumize the JTextArea
        timeAndDateField.setHorizontalAlignment(JTextField.CENTER);
        timeAndDateField.setEditable(false);
        timeAndDateField.setBackground(backgroundColor);
        timeAndDateField.setForeground(foregroundColor);
        timeAndDateField.setBorder(new LineBorder(buttonBorderColor,2));
        new Time(timeAndDateField, "HH:mm:ss, yyyy-MM-dd");
        topPanel.add(timeAndDateField, BorderLayout.CENTER);
    }
    //Method to costumize Bottom left panel
    @SuppressWarnings("Convert2Lambda")
    public void addComponentsBottomLeft() {
        // Setting up the panels
        JPanel speedometerPanel = new JPanel();
        JPanel quickAccessPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        JPanel wrapperPanel = new JPanel();
        JButton carHealthButton = new JButton("Car Health");
        //Action listener to open the car health log
        carHealthButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new healthLog();
            }
            
        });
        // Customizing borders for reduced spacing
        quickAccessPanel.setBorder(BorderFactory.createEmptyBorder(40, -50, 10, 0)); 
        speedometerPanel.setBorder(BorderFactory.createEmptyBorder(180, -50, 0, 0)); 
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(20,-50,85,0));
        // Setting up the buttons and textareas
        int speed = 50;
        SemiCircularSpeedometer basicInfoArea = new SemiCircularSpeedometer(speed);
        JButton selfieButton = new JButton("[◉¯]");
        JButton weatherButton = new JButton("🌡️");
        JButton volumeUpButton = new JButton("+");
        JButton volumeDownButton = new JButton("-");
        //
        weatherButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new Weather();
                } catch (Exception e1) {
                    System.out.println("Error: " + e1.getMessage());
                }
            }
        });
        // Customization for the buttons and JTextArea
        carHealthButton.setPreferredSize(new Dimension(150,70));
        carHealthButton.setFont(new Font("Arial",Font.BOLD,15));
        basicInfoArea.setFont(new Font("Arial", Font.BOLD, 15));
        basicInfoArea.setForeground(Color.decode("#AEDBF0"));
        basicInfoArea.setPreferredSize(new Dimension(200, 200));
        selfieButton.setPreferredSize(new Dimension(80, 50));
        weatherButton.setPreferredSize(new Dimension(80, 50));
        volumeDownButton.setPreferredSize(new Dimension(80, 50));
        volumeUpButton.setPreferredSize(new Dimension(80, 50));
        carHealthButton.setPreferredSize(new Dimension(100, 50));
        carHealthButton.setFont(new Font("Arial", Font.BOLD, 13));
        //Costumize buttons
        costumizeButtons(selfieButton);
        costumizeButtons(weatherButton);
        costumizeButtons(volumeUpButton);
        costumizeButtons(volumeDownButton);
        costumizeButtons(carHealthButton);
        //Costumize panels
        wrapperPanel.setBackground(backgroundColor);
        wrapperPanel.setForeground(foregroundColor);
        speedometerPanel.setBackground(backgroundColor);
        speedometerPanel.setForeground(foregroundColor);
        quickAccessPanel.setBackground(backgroundColor);
        quickAccessPanel.setForeground(foregroundColor);
        bottomLeftPanel.setLayout(new BoxLayout(bottomLeftPanel, BoxLayout.Y_AXIS));
        // Adding everything together
        speedometerPanel.add(basicInfoArea);
        quickAccessPanel.add(volumeUpButton);
        quickAccessPanel.add(volumeDownButton);
        quickAccessPanel.add(selfieButton);
        quickAccessPanel.add(weatherButton);
        bottomLeftPanel.add(speedometerPanel);
        bottomLeftPanel.add(Box.createVerticalStrut(2)); 
        bottomLeftPanel.add(quickAccessPanel);
        bottomLeftPanel.add(Box.createVerticalStrut(2)); 
        wrapperPanel.add(carHealthButton);
        bottomLeftPanel.add(wrapperPanel);
    }
    //Method to add the components to the bottom right panel
    @SuppressWarnings("Convert2Lambda")
    public void addComponentsBottomRight() {
        JPanel appsPanel = new JPanel(null);
        JButton maps = new JButton("Maps");
        JButton media = new JButton("Media");
        JButton dashcam = new JButton("Dashcam");
        JButton dashboard = new JButton("Dashboard");
        JButton gallery = new JButton("Gallery");
        JButton settings = new JButton("Settings");
        media.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Media();
            }
        });
        gallery.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                new Gallery();
            }
        });
        //Costumizing the buttons
        costumizeButtons(maps);
        costumizeButtons(media);
        costumizeButtons(dashcam);
        costumizeButtons(dashboard);
        costumizeButtons(gallery);
        costumizeButtons(settings);
        //Setting the bounds, spacing and offset
        int buttonWidth = 250;
        int buttonHeight = 150;
        int spacing = 25;
        int verticalOffset = 130;
        //Specifying the location of each button
        //Row 1
        maps.setBounds(0, verticalOffset, buttonWidth, buttonHeight);
        media.setBounds(0 + buttonWidth + spacing, verticalOffset, buttonWidth, buttonHeight);
        dashcam.setBounds(0 + 2 * (buttonWidth + spacing), verticalOffset, buttonWidth, buttonHeight);
        //Row 2
        dashboard.setBounds(0, verticalOffset + buttonHeight + spacing, buttonWidth, buttonHeight);
        gallery.setBounds(0 + buttonWidth + spacing, verticalOffset + buttonHeight + spacing, buttonWidth, buttonHeight);
        settings.setBounds(0 + 2 * (buttonWidth + spacing), verticalOffset + buttonHeight + spacing, buttonWidth, buttonHeight);
        //Adding everything together
        appsPanel.add(maps);
        appsPanel.add(media);
        appsPanel.add(dashcam);
        appsPanel.add(dashboard);
        appsPanel.add(gallery);
        appsPanel.add(settings);
        //Setting up the lower part of the panel
        JPanel musicPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        musicPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 110, 20));
        JButton pauseOrPlay = new JButton("Pause/Play");
        JButton previous = new JButton("<<");
        //SET THE LOGIC TO MAKE A MARQUEE LIKE EFFECT FOR THE JTEXTFIELD!!!
        JTextField playing = new JTextField("Now Playing: Song 1234");
        playing.setFont(new Font("Arial",Font.BOLD,12));
        JButton next = new JButton(">>");
        //Customization for the buttons and panels
        playing.setEditable(false);
        playing.setHorizontalAlignment(JTextField.CENTER);
        playing.setBorder(BorderFactory.createLineBorder(buttonBorderColor,2));
        musicPanel.setBackground(backgroundColor);
        musicPanel.setForeground(foregroundColor);
        appsPanel.setBackground(backgroundColor);
        appsPanel.setForeground(foregroundColor);
        playing.setBackground(backgroundColor);
        playing.setForeground(foregroundColor);
        costumizeMusicButtons(pauseOrPlay);
        costumizeMusicButtons(previous);
        costumizeMusicButtons(next);
        //Adding everything together
        musicPanel.add(pauseOrPlay);
        musicPanel.add(previous);
        musicPanel.add(playing);
        musicPanel.add(next);
        bottomRightPanel.add(musicPanel, BorderLayout.SOUTH);
        bottomRightPanel.add(appsPanel, BorderLayout.CENTER);
    }
    //Method to costumize musicPanel's Buttons
    public void costumizeMusicButtons(JButton btn){
        btn.setBackground(backgroundColor);
        btn.setForeground(foregroundColor);
        btn.setPreferredSize(new Dimension(10,50));
        btn.setBorder(BorderFactory.createLineBorder(buttonBorderColor,2));
        btn.setFocusable(false);
        btn.setFont(new Font("Arial",Font.BOLD,15));
    }
    //Method to costumize bottom right panel buttons
    public void costumizeButtons(JButton btn) {
        btn.setBackground(backgroundColor);
        btn.setForeground(foregroundColor);
        btn.setBorder(BorderFactory.createLineBorder(buttonBorderColor,2));
        btn.setFocusable(false);
        btn.setFont(new Font("Arial",Font.BOLD,15));
    }
    //Method to fetch the color based on the string
    public Color getColorFromString(String color) {
        return switch (color.toLowerCase()) {
            case "black" -> Color.BLACK;
            case "white" -> Color.WHITE;
            case "gray" -> Color.GRAY;
            case "red" -> Color.RED;
            case "blue" -> Color.BLUE;
            default -> Color.WHITE; //Default value to be returned
        };
    }
}