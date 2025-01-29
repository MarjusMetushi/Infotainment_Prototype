
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.*;

public class SemiCircularSpeedometer extends JComponent {
    @SuppressWarnings("FieldMayBeFinal")
    private int maxSpeed = 260; // Fixed maximum speed
    private int currentSpeed = 0; // Current speed, when car starts it is 0km/h
    private Color speedometerColor;
    Color backgroundColor;
    Color foregroundColor;
    private Properties config = new Properties();

    // Constructor to initialize speed during object creation
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public SemiCircularSpeedometer(int currentSpeed) {
        loadConfig();
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        setSpeed(currentSpeed); // Use setSpeed to handle validation
    }

    // Method to update the current speed
    public void setSpeed(int speed) {
        if (speed < 0) {
            speed = -speed;
        }
        this.currentSpeed = Math.min(speed, maxSpeed); // Cap the speed to maxSpeed
        repaint();
    }

    @SuppressWarnings({"ConvertToTryWithResources", "CallToPrintStackTrace"})
    public void loadConfig() {
        config = new Properties();
        try {
            // Load properties from a file
            FileInputStream fileInputStream = new FileInputStream("config.properties");
            config.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
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
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        loadConfig();
        Graphics2D g2d = (Graphics2D) g;

        // Determine the color based on configuration
        if ("black".equalsIgnoreCase(config.getProperty("backgroundColor", "white"))) {
            speedometerColor = Color.decode(config.getProperty("borderColor1", "#FF0000")); // Default to red
        } else {
            speedometerColor = Color.decode(config.getProperty("borderColor2", "#0000FF")); // Default to blue
        }

        int width = getWidth();
        int height = getHeight();

        int arcStartAngle = 180; // Start angle for semicircle
        int arcAngle = 180; // Total angle for semicircle

        // Anti-aliasing for smooth rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the background arc (semicircle)
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(15));
        g2d.drawArc(20, 20, width - 40, height * 2 - 40, arcStartAngle, arcAngle);

        // Calculate the angle for the current speed
        int speedAngle = (int) ((double) currentSpeed / maxSpeed * arcAngle);

        // Draw the filled arc representing the speed
        g2d.setColor(speedometerColor);
        g2d.drawArc(20, 20, width - 40, height * 2 - 40, arcStartAngle, -Math.min(speedAngle, arcAngle)); // Ensure it doesn't exceed full arc

        // Customization for the speedometer
        g2d.setFont(getFont());
        g2d.setColor(foregroundColor); 
        String speedText = currentSpeed + " km/h";
        int textWidth = g2d.getFontMetrics().stringWidth(speedText);
        g2d.drawString(speedText, (width - textWidth) / 2, height - 10);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(260, 130); // Set default size for the component
    }
}
