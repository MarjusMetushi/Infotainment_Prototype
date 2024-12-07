import java.awt.*;
import java.io.*;
import java.util.Properties;
import javax.swing.*;

public class healthLog {
    Properties config = new Properties();
    JPanel topPanel = new JPanel();
    JPanel mainPanel = new JPanel();
    JScrollPane logScrollPane;
    Color backgroundColor;
    Color foregroundColor;
    Color buttonBorderColor;
    //Costructor to set up and Customize the interface
    @SuppressWarnings("OverridableMethodCallInConstructor")
    healthLog() {
        //Call the method to load the config
        loadconfig();
        //Get the colors from the config file
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        buttonBorderColor = backgroundColor == Color.BLACK
                ? Color.decode(config.getProperty("borderColor1"))
                : Color.decode(config.getProperty("borderColor2"));
        //Set up the dialog
        JDialog dialog = new JDialog();
        dialog.setSize(1280, 720);
        dialog.setLayout(new BorderLayout());
        //Methods to Customize the dialog
        addAndCustomizeTopPanelComponents();
        addAndCustomizeMainPanelComponents();

        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(logScrollPane, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    //Method to load the config file
    public void loadconfig() {
        try (FileInputStream fileInputStream = new FileInputStream("config.properties")) {
            config.load(fileInputStream);
        } catch (IOException e) {
            // Used for debugging
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
    //Method to add and customize main panel components
    public void addAndCustomizeMainPanelComponents() {
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setForeground(foregroundColor);

        // Add the main panel inside a scroll pane
        logScrollPane = new JScrollPane(mainPanel);
        logScrollPane.setBackground(backgroundColor);
        logScrollPane.setBorder(BorderFactory.createLineBorder(buttonBorderColor, 2));

        // Add some test notifications
        addNotification("Car needs water !");
        addNotification("Oil check time !");
    }
    //Method to add components and Customize them in the top panel
    public void addAndCustomizeTopPanelComponents() {
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

    public void addNotification(String message) {
        // Create a notification panel
        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new BorderLayout());
        notificationPanel.setBackground(backgroundColor);
        notificationPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(buttonBorderColor, 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20) // Padding inside the panel
        ));
    
        // Set a preferred size and align to center
        notificationPanel.setMaximumSize(new Dimension(1270, 50)); // Width and height
        notificationPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    
        // Create the label for the message
        JLabel notificationLabel = new JLabel(message);
        notificationLabel.setForeground(foregroundColor);
        notificationLabel.setFont(new Font("Arial", Font.PLAIN, 18));
    
        // Create the delete button
        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(Color.RED);
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFont(new Font("Arial",Font.BOLD,15));
        deleteButton.setFocusable(false);
        deleteButton.addActionListener(e -> {
            mainPanel.remove(notificationPanel);
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    
        // Add components to the notification panel
        notificationPanel.add(notificationLabel, BorderLayout.CENTER);
        notificationPanel.add(deleteButton, BorderLayout.EAST);
    
        // Add the notification panel to the main panel
        mainPanel.add(Box.createVerticalStrut(10)); // Space between notifications
        mainPanel.add(notificationPanel);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
