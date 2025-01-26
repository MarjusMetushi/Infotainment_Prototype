import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import javax.swing.Timer;
import java.util.Date;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
public class map {
    /*
        Just open the web page of the map using webview and javafx
        Use the js api for enhancement
     */
    // Declare the variables
    JDialog dialog;
    JPanel topPanel = new JPanel();
    JPanel mainPanel = new JPanel();
    JPanel bottomPanel = new JPanel();
    Color backgroundColor;
    Color foregroundColor;
    Color buttonBorderColor;
    Properties config = new Properties();
    JButton exitButton = new JButton("Exit");
    map(){
        // Load config and get colors
        loadconfig();
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        if (backgroundColor == Color.BLACK) buttonBorderColor = Color.decode(config.getProperty("borderColor1"));
        else buttonBorderColor = Color.decode(config.getProperty("borderColor2"));
        // Start the dialog and costumize the interface
        dialog = new JDialog();
        dialog.setTitle("Map");
        dialog.setSize(800, 600);
        // Methods to costumize the panels
        costumizeTopPanel();
        costumizeMainPanel();
        costumizeBottomPanel();
        // Add the panels to the dialog
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
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
    public void costumizeTopPanel() {
        JTextField timeAndDateField = new JTextField("Time: 19:20, Date: 2024-11-22");
        timeAndDateField.setHorizontalAlignment(JTextField.CENTER);
        timeAndDateField.setEditable(false);
        timeAndDateField.setBackground(backgroundColor);
        timeAndDateField.setForeground(foregroundColor);
        timeAndDateField.setBorder(BorderFactory.createLineBorder(buttonBorderColor, 2));

        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss, yyyy-MM-dd");
            String dateString = formatter.format(new Date());
            timeAndDateField.setText("Time: " + dateString);
        });
        timer.start();

        topPanel.add(timeAndDateField, BorderLayout.CENTER);
    }
    public void costumizeMainPanel() {
        JFXPanel jfxPanel = new JFXPanel(); // JavaFX panel inside Swing
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(jfxPanel, BorderLayout.CENTER);
    
        Platform.runLater(() -> {
            WebView webView = new WebView();
            webView.getEngine().load("https://maps.google.com"); // Load Google Maps
    
            // Resize the WebView to make it smaller
            webView.setPrefSize(200, 200); // Set width and height for a smaller view
    
            jfxPanel.setScene(new Scene(webView));
        });
    }
    // Method to costumize the bottom panel
    public void costumizeBottomPanel() {
        exitButton.setBackground(backgroundColor);
        exitButton.setForeground(foregroundColor);
        exitButton.setBorder(BorderFactory.createLineBorder(buttonBorderColor, 2));
        bottomPanel.add(exitButton, BorderLayout.CENTER);
    }
}
