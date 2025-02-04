
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Time {
    private final Timer timer;

    public Time(JTextField textField, String format) {
        // Formatter for time and date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        // Set initial time
        updateTime(textField, formatter);

        // Timer to update the time every second
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTime(textField, formatter);
            }
        });
        timer.start();
    }

    // Method to update the time on the provided text field
    private void updateTime(JTextField textField, DateTimeFormatter formatter) {
        LocalDateTime now = LocalDateTime.now();
        textField.setText("Time: " + now.format(formatter));
    }

    // Optional method to stop the timer if needed
    public void stop() {
        timer.stop();
    }
}
