import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

// Custom FuelBarPanel class
public class FuelBarPanel extends JPanel {
    // Variable declaration
    int currentFuel;
    Color backgroundColor;
    Color foregroundColor;
    Properties config = new Properties();

    // Constructor to set up the fuel bar panel
    public FuelBarPanel(int initialFuel) {
        loadConfig();
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = Color.decode(config.getProperty("borderColor1"));
        setPreferredSize(new Dimension(200, 30)); // Set default dimensions for horizontal bar
        currentFuel = initialFuel;
    }

    // Method to load the configurations
    @SuppressWarnings("ConvertToTryWithResources")
    public void loadConfig() {
        config = new Properties();
        try {
            // Load properties from a file
            FileInputStream fileInputStream = new FileInputStream("config.properties");
            config.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            // For debugging
        }
    }

    // Method to fetch the color based on the string
    public Color getColorFromString(String color) {
        return switch (color.toLowerCase()) {
            case "black" -> Color.BLACK;
            case "white" -> Color.WHITE;
            case "gray" -> Color.GRAY;
            case "red" -> Color.RED;
            case "blue" -> Color.BLUE;
            default -> Color.WHITE; // Default value to be returned
        };
    }

    // Setting the fuel level (0-100)
    public void setCurrentFuel(int fuel) {
        currentFuel = Math.max(0, Math.min(100, fuel));
        repaint();
    }

    // Method to paint the fuel bar
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background of the bar
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Calculate the filled portion based on current fuel percentage
        int fillWidth = (currentFuel * getWidth()) / 100;

        // Draw the segment to be filled from left to right
        g.setColor(foregroundColor);
        g.fillRect(0, 0, fillWidth, getHeight());
    }
}
