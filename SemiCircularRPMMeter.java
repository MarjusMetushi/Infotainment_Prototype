import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.*;

public class SemiCircularRPMMeter extends JComponent {
    private int maxRpm = 8000; // Fixed maximum RPM
    private int currentRpm = 0; // Current RPM, initially set to 0
    private Color rpmMeterColor;
    Color foregroundColor;
    Color backgroundColor;
    private Properties config = new Properties();
   
    // Constructor to initialize RPM during object creation
    public SemiCircularRPMMeter(int currentRpm) {
        loadConfig();
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        setLayout(null); // Set layout to null for custom positioning
        setRpm(currentRpm); // Use setRpm to handle validation
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

    // Method to update the current RPM
    public void setRpm(int rpm) {
        if (rpm < 0) {
            rpm = -rpm;
        }
        this.currentRpm = Math.min(rpm, maxRpm); // Cap the RPM to maxRpm
       repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        loadConfig();
        Graphics2D g2d = (Graphics2D) g;

        if ("black".equalsIgnoreCase(config.getProperty("backgroundColor", "white"))) {
            rpmMeterColor = Color.decode(config.getProperty("borderColor1", "#FF0000")); // Default to red
        } else {
            rpmMeterColor = Color.decode(config.getProperty("borderColor2", "#0000FF")); // Default to blue
        }

        int width = getWidth();
        int height = getHeight();

        int arcStartAngle = 180;
        int arcAngle = 180;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setStroke(new BasicStroke(15));
        g2d.drawArc(20, 20, width - 40, height * 2 - 40, arcStartAngle, arcAngle);

        int redlineStartAngle = arcStartAngle - (int) (arcAngle * 0.8);
        int redlineAngle = (int) (arcAngle * 0.2);
        g2d.setColor(Color.RED);
        g2d.drawArc(20, 20, width - 40, height * 2 - 40, redlineStartAngle, redlineAngle);

        int rpmAngle = (int) ((double) currentRpm / maxRpm * arcAngle);

        g2d.setColor(rpmMeterColor);
        g2d.drawArc(20, 20, width - 40, height * 2 - 40, arcStartAngle, -Math.min(rpmAngle, arcAngle));

         // Customization for the speedometer
         g2d.setFont(getFont());
         g2d.setColor(foregroundColor); 
         String speedText = currentRpm + " rpm";
         int textWidth = g2d.getFontMetrics().stringWidth(speedText);
         g2d.drawString(speedText, (width - textWidth) / 2, height - 10);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 150);
    }
}
