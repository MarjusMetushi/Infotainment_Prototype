import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

// Custom VolumeBarPanel class
public class VolumeBarPanel extends JPanel {
    // Variable declaration
    int currentVolume;
    Color backgroundColor;
    Color foregroundColor;
    Properties config = new Properties();
    // Method to set up the volume bar panel
    public VolumeBarPanel(int currentvol) {
        loadConfig();
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = Color.decode(config.getProperty("borderColor1"));
        setPreferredSize(new Dimension(10, 200)); // Set default dimensions
        currentVolume = currentvol;
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
            // For debugging
        }
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

    // Setting the volume (0-100)
    public void setCurrentVolume(int volume) {
        currentVolume = Math.max(0, Math.min(100, volume));
        repaint();
    }
    // Method to paint the volume bar
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background of the bar
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Calculate the filled portion based on current volume percentage
        int fillHeight = (currentVolume * getHeight()) / 100;

        // Draw the segment to be filled from the bottom up
        g.setColor(foregroundColor);
        g.fillRect(0, getHeight() - fillHeight, getWidth(), fillHeight);
    }
}
