import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.*;

public class SemiCircularRPMMeter extends JComponent {
    @SuppressWarnings("FieldMayBeFinal")
    private int maxRpm = 8000; // Fixed maximum RPM
    private int currentRpm = 0; // Current RPM, initially set to 0
    private Color rpmMeterColor;
    private Properties config = new Properties();

    // Constructor to initialize RPM during object creation
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public SemiCircularRPMMeter(int currentRpm) {
        setRpm(currentRpm); // Use setRpm to handle validation
    }

    // Method to update the current RPM
    public void setRpm(int rpm) {
        if (rpm < 0) {
            rpm = -rpm;
        }
        this.currentRpm = Math.min(rpm, maxRpm); // Cap the RPM to maxRpm
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        loadConfig();
        Graphics2D g2d = (Graphics2D) g;

        // Determine the color based on configuration
        if ("black".equalsIgnoreCase(config.getProperty("backgroundColor", "white"))) {
            rpmMeterColor = Color.decode(config.getProperty("borderColor1", "#FF0000")); // Default to red
        } else {
            rpmMeterColor = Color.decode(config.getProperty("borderColor2", "#0000FF")); // Default to blue
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

        // Redline region (last 20% of the arc)
        int redlineStartAngle = arcStartAngle - (int) (arcAngle * 0.8);
        int redlineAngle = (int) (arcAngle * 0.2);
        g2d.setColor(Color.RED);
        g2d.drawArc(20, 20, width - 40, height * 2 - 40, redlineStartAngle, redlineAngle);

        // Calculate the angle for the current RPM
        int rpmAngle = (int) ((double) currentRpm / maxRpm * arcAngle);

        // Draw the filled arc representing the RPM
        g2d.setColor(rpmMeterColor);
        g2d.drawArc(20, 20, width - 40, height * 2 - 40, arcStartAngle, -Math.min(rpmAngle, arcAngle)); // Ensure it doesn't exceed full arc

        // Customization for the RPM meter
        g2d.setFont(getFont());
        g2d.setColor(getForeground());
        String rpmText = currentRpm + " RPM";
        int textWidth = g2d.getFontMetrics().stringWidth(rpmText);
        g2d.drawString(rpmText, (width - textWidth) / 2, height - 10);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(300, 150); // Set default size for the component
    }
}
