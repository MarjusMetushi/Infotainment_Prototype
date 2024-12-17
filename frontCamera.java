import java.util.Properties;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;


public class frontCamera {
    /*
     * Requirements:
     * USE PYTHON
     *  Create a method to display the front camera in the GUI c++ to access the driver and camera but java to display it as a thread
     *  Create a method to take a picture with the front camera and save it somewhere c++
     *  Create a method to increase/decrease the brightness of the front camera java 
     *  Create a method to add a timer to the front camera java and call the c++ executable
     */
    // Variable declaration
    JPanel topPanel = new JPanel(new BorderLayout());
    JPanel middlePanel = new JPanel();
    JPanel bottomPanel = new JPanel();
    Color backgroundColor;
    Color foregroundColor;
    Color buttonBorderColor;
    Properties config = new Properties();
    // Method to create the interface
    frontCamera() {
        // Load config and get colors
        loadconfig();
        // Start the camera as a thread and keep showing it at the middle panel all the time
        initializeCamera();
        // Getting the colors
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        if (backgroundColor == Color.BLACK) buttonBorderColor = Color.decode(config.getProperty("borderColor1"));
        else buttonBorderColor = Color.decode(config.getProperty("borderColor2"));
        // Create and customize the dialog
        JDialog dialog = new JDialog();
        dialog.setTitle("Front Camera");
        dialog.setSize(1280, 720);
        dialog.setBackground(backgroundColor);
        dialog.setForeground(foregroundColor);
        dialog.setLayout(new BorderLayout());
        // Customization for the panels
        costumizetopPanel();
        costumizeMiddlePanel();
        costumizebottomPanel();
        // Adding everything together
        dialog.add(topPanel,BorderLayout.NORTH);
        dialog.add(middlePanel, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
    }
    // Method to load the config file
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
    // Method to Customize and add Components for the top panel
    public void costumizetopPanel() {
        // Customize the panel
        topPanel.setBackground(backgroundColor);
        topPanel.setForeground(foregroundColor);
        // Set time and date
        JTextField timeAndDateField = new JTextField("Time: 12:00, Date: 2024-11-16");
        // Customize the JTextArea
        timeAndDateField.setHorizontalAlignment(JTextField.CENTER);
        timeAndDateField.setEditable(false);
        timeAndDateField.setBackground(backgroundColor);
        timeAndDateField.setForeground(foregroundColor);
        timeAndDateField.setBorder(new LineBorder(buttonBorderColor,2));
        new Time(timeAndDateField, "HH:mm:ss, yyyy-MM-dd");
        topPanel.add(timeAndDateField, BorderLayout.CENTER);
    }
    // Method to Customize and add Components for the middle panel
    public void costumizeMiddlePanel() {
        // Just show the camera's view here as a thread
    }
    // Method to Customize and add Components for the bottom panel
    public void costumizebottomPanel() {
        // Take all the buttons here
        JButton brightnessButton = new JButton("Brightness");
        JButton timerButton = new JButton("Timer");
        JButton TakePicture = new JButton("Take Picture");
        JButton startStopRec = new JButton("Start/Stop Recording");
        JButton goBackButton = new JButton("Back");
        JButton mediaButton = new JButton("Media");
    }
    public void initializeCamera(){
        // Create a thread to show the camera to the middle panel
    }
    public void takePicture(){
        // take the picture/Frame of the camera
        // call the method to save the picture to the path
    }
    public void startRec(){
        // Start the recording and capture the frame for as long as the thread is going on 
    }
    public void stopRec(){
        // Save the recording to the path and end the timer thing
    }
    public void brightnessControl(int val){
        // Control the brightness of the camera here based on fixed values passed as arguments 
    }
    public void timerControl(int option){
        // Add a configuration for how long until the timer is done then call take picture method
    }
    public void goBack(){
        // Method to shut down this instance and return to the main menu
    }
    public void accessGallery(){
        // Method to open the pictures and videos (Gallery)
    }
    public void savePictureOrRecording(){
        // Method to save the the picture/recording to the specified path from settings
    }
}
