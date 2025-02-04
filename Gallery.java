import java.awt.*;
import java.io.*;
import java.util.Properties;
import javax.swing.*;
//Add the time panel
public class Gallery {
    // Variable declaration
    JPanel topPanel = new JPanel();
    JPanel centerPanel = new JPanel();
    JPanel bottomPanel = new JPanel();
    JTextField searchBar = new JTextField(40); // Increase the width of the search bar
    JPanel photoGridPanel = new JPanel(); // Panel to hold photos
    JScrollPane scrollPane; // Scroll pane for the photo grid
    JLabel searchLabel = new JLabel("Search: ");
    Properties config = new Properties();
    Color backgroundColor;
    Color foregroundColor;
    Color buttonBorderColor;
    int pics = 50;
    int cols = 5;
    // Gallery constructor
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public Gallery() {
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
        dialog.setTitle("Gallery");
        dialog.setSize(1280, 720);
        dialog.setLayout(new BorderLayout());
        // Customize the panels
        customizeThePanels();
        // Adding everything together
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(centerPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // Method to load the config file
    public void loadConfig() {
        try (FileInputStream fileInputStream = new FileInputStream("config.properties")) {
            config.load(fileInputStream);
        } catch (IOException e) {
            // For debugging
        }
    }

    // Method to fetch a color based on the string
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

    // Method to customize the panels
    public void customizeThePanels() {
        // Set background colors
        topPanel.setBackground(backgroundColor);
        topPanel.setForeground(foregroundColor);
        centerPanel.setBackground(backgroundColor);
        centerPanel.setForeground(foregroundColor);
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.setForeground(foregroundColor);
        searchBar.setBackground(backgroundColor);
        searchBar.setForeground(foregroundColor);
        // Add the search bar and do more Customization
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        searchLabel.setForeground(foregroundColor);
        searchLabel.setFont(new Font("Arial",Font.BOLD, 20));
        searchBar.setFont(new Font("Arial",Font.BOLD, 20));
        topPanel.add(searchLabel);
        topPanel.add(searchBar);
        searchBar.setPreferredSize(new Dimension(600, 30));
        // Set up the scrollable photo grid
        photoGridPanel.setLayout(new GridLayout(pics/cols, cols)); 
        photoGridPanel.setBackground(backgroundColor);
        // Add "test" photos to the grid
        for (int i = 1; i <= pics; i++) { 
            JLabel photoLabel = new JLabel("Photo " + i, SwingConstants.CENTER);
            photoLabel.setPreferredSize(new Dimension(150, 150)); 
            photoLabel.setOpaque(true);
            photoLabel.setBackground(Color.LIGHT_GRAY);
            photoLabel.setBorder(BorderFactory.createLineBorder(backgroundColor));
            photoGridPanel.add(photoLabel);
        }
        // Wrap the photo grid in a scroll pane
        scrollPane = new JScrollPane(photoGridPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Speed up the scroll bar
        JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
        scrollBar.setUnitIncrement(15);
        // Add the scroll pane to the center panel
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        // Set up buttons with more space for the bottom panel
        bottomPanel.setLayout(new GridLayout(1, 4, 0, 0)); 
        bottomPanel.setPreferredSize(new Dimension(1280, 100)); 
        JButton openButton = new JButton("Open");
        JButton deleteButton = new JButton("Delete");
        JButton renameButton = new JButton("Rename");
        JButton sortButton = new JButton("Sort");
        //Customize Buttons
        openButton.setBackground(backgroundColor);
        openButton.setForeground(foregroundColor);
        openButton.setBorder(BorderFactory.createLineBorder(buttonBorderColor));
        openButton.setFont(new Font("Arial",Font.BOLD, 20));
        deleteButton.setBackground(backgroundColor);
        deleteButton.setForeground(foregroundColor);
        deleteButton.setBorder(BorderFactory.createLineBorder(buttonBorderColor));
        deleteButton.setFont(new Font("Arial",Font.BOLD, 20));
        renameButton.setBackground(backgroundColor);
        renameButton.setForeground(foregroundColor);
        renameButton.setBorder(BorderFactory.createLineBorder(buttonBorderColor));
        renameButton.setFont(new Font("Arial",Font.BOLD, 20));
        sortButton.setBackground(backgroundColor);
        sortButton.setForeground(foregroundColor);
        sortButton.setBorder(BorderFactory.createLineBorder(buttonBorderColor));
        sortButton.setFont(new Font("Arial",Font.BOLD, 20));
        // Add buttons to the bottom panel
        bottomPanel.add(openButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(renameButton);
        bottomPanel.add(sortButton);
    }
}
