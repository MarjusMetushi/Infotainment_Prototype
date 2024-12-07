import java.awt.*;
import java.io.*;
import java.util.Properties;
import javax.swing.*;

public class Media {
    //Variable declaration
    Properties config = new Properties();
    Color backgroundColor;
    Color foregroundColor;
    Color buttonBorderColor;
    JPanel mainPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout
    JPanel topPanel = new JPanel(new BorderLayout());

    @SuppressWarnings("OverridableMethodCallInConstructor")
    Media() {
        // Load config and get colors
        loadconfig();
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        if (backgroundColor == Color.BLACK) buttonBorderColor = Color.decode(config.getProperty("borderColor1"));
        else buttonBorderColor = Color.decode(config.getProperty("borderColor2"));

        // Create and customize the dialog
        JDialog mediaDialog = new JDialog();
        mediaDialog.setTitle("Media");
        mediaDialog.setSize(1280, 720);
        mediaDialog.setBackground(backgroundColor);
        mediaDialog.setForeground(foregroundColor);
        mainPanel.setBackground(backgroundColor);
        mainPanel.setForeground(foregroundColor);

        // Customize interface
        setAndCustomizeComponents();
        addComponentsTop();

        mediaDialog.add(topPanel, BorderLayout.NORTH);
        mediaDialog.add(mainPanel, BorderLayout.CENTER);
        mediaDialog.setVisible(true);
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
    //Method to Customize and add Components
    public void setAndCustomizeComponents() {
        // Create buttons
        JButton ytmusicButton = new JButton("YouTube Music");
        JButton spotifyButton = new JButton("Spotify");
        JButton kodiButton = new JButton("Kodi");
        JButton auxButton = new JButton("AUX");
        JButton usbButton = new JButton("USB");
        JButton mirroringButton = new JButton("Mirroring");

        // Customize buttons
        CustomizeButton(ytmusicButton);
        CustomizeButton(spotifyButton);
        CustomizeButton(kodiButton);
        CustomizeButton(auxButton);
        CustomizeButton(usbButton);
        CustomizeButton(mirroringButton);

        // Add buttons to the main panel with GridBagLayout constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(40, 40, 40, 40); // Margins between buttons
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(ytmusicButton, gbc);

        gbc.gridx = 1;
        mainPanel.add(spotifyButton, gbc);

        gbc.gridx = 2;
        mainPanel.add(kodiButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(auxButton, gbc);

        gbc.gridx = 1;
        mainPanel.add(usbButton, gbc);

        gbc.gridx = 2;
        mainPanel.add(mirroringButton, gbc);
    }
    //Method to add components and Customize the topPanel
    public void addComponentsTop() {
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
    //Customizing the buttons
    public void CustomizeButton(JButton btn) {
        btn.setBackground(backgroundColor);
        btn.setForeground(foregroundColor);
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setBorder(BorderFactory.createLineBorder(buttonBorderColor, 2));
        btn.setPreferredSize(new Dimension(250, 150)); // Set button size
        btn.setFocusable(false);
    }
}
