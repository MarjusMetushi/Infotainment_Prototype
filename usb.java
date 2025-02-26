import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.*;
//TODO: fix the buttons looks
//TODO: try to make a jfilechooser 

public class usb {
    // Declare variables
    static JDialog dialog;
    static String path = "";
    static Color backgroundColor;
    static Color foregroundColor;
    static Properties config = new Properties();
    static JPanel panel;
    static JPanel lowerPanel;
    static GridBagConstraints gbc = new GridBagConstraints();
    static int elements = 0;
    
    public static void usb() {
        // Load config to load system's variables
        loadConfig(); 
        backgroundColor = getColorFromString(config.getProperty("backgroundColor", "white"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor", "black"));
        
        // UI and costumization
        dialog = new JDialog(); 
        dialog.setTitle("USB Explorer");
        dialog.setSize(1280, 720);
        dialog.setLayout(new BorderLayout());

        // Create the top/biggest panel
        panel = new JPanel();
        panel.setBackground(backgroundColor);

        // Create the lower panel with buttons
        lowerPanel = new JPanel();
        lowerPanel.setLayout(new GridLayout(1, 2));
        lowerPanel.setBackground(backgroundColor);

        JButton goBack = new JButton("Exit");
        JButton again = new JButton("Open");

        // Add action listeners to buttons
        goBack.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose(); // Quits the application
            }

        });

        again.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showFiles(getPath()); // Loads the files
            }

        });

        // Add everything together
        lowerPanel.add(goBack);
        lowerPanel.add(again);

        dialog.add(new JScrollPane(panel), BorderLayout.CENTER);
        dialog.add(lowerPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
        // Start by prompting the user to select a directorys
        showFiles(getPath());
    }

    // Helper method to load the config file
    private static void loadConfig() {
        try (FileInputStream fileInputStream = new FileInputStream("config.properties")) {
            config.load(fileInputStream);
        } catch (IOException ignored) {
            // DEBUGGING
        }
    }
    // Helper method to fetch a color based on the string
    private static Color getColorFromString(String color) {
        return switch (color.toLowerCase()) {
            case "black" -> Color.BLACK;
            case "white" -> Color.WHITE;
            case "gray" -> Color.GRAY;
            case "red" -> Color.RED;
            case "blue" -> Color.BLUE;
            default -> Color.WHITE;
        };
    }
    // Helper method to get the path of the directory
    private static File getPath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path = fileChooser.getSelectedFile().getAbsolutePath(); // Store the path
        }
        return fileChooser.getSelectedFile(); // Return the selected file
    }

    private static void showFiles(File directory) {
        // Clear previous components
        for (Component c : panel.getComponents()) {
            panel.remove(c);
        }

        // Recursively collect all files and directories
        ArrayList<File> allFiles = new ArrayList<>();
        collectFiles(directory, allFiles); // Call helper method to group all the files together
        
        panel.setLayout(new GridLayout(0,3,5,5));
        // Add all files and directories to the panel
        for (int i = 0; i < allFiles.size(); i++) {
            File f = allFiles.get(i);
            JButton button = new JButton(f.getName()); // Create a button for each file
            button.setPreferredSize(new Dimension(100, 70));
            button.setFont(new Font("Arial", Font.BOLD, 20)); // Set font
            setFunctionality(button, f); // Set button functionality based on file
            
            panel.add(button);
        }
        // Update layout
        dialog.revalidate();
        dialog.repaint();
    }

    // Helper method to collect all files and directories recursively
    private static void collectFiles(File directory, ArrayList<File> allFiles) {
        // Get all files and directories in the current directory
        File[] files = directory.listFiles();
        if (files == null)
            return;

        for (File f : files) {
            // Add current file or directory to the list
            allFiles.add(f);

            // If directory -> recursion
            if (f.isDirectory()) {
                collectFiles(f, allFiles); // Recursively search subdirectories
            }
        }
    }

    // Helper method to set button functionality based on file type
    private static void setFunctionality(JButton button, File file) {
        button.addActionListener(evt1 -> { // Add action listener to button
            // UI adjustments
            JDialog tempDialog = new JDialog();
            tempDialog.setTitle(button.getText());
            tempDialog.setSize(300, 200);
            tempDialog.setLayout(new FlowLayout());
            // Text files can be opened to be read
            if (file.getName().endsWith(".txt") || file.getName().endsWith(".md") || file.getName().endsWith(".pdf")) {
                JButton openText = new JButton("Open Text");
                openText.addActionListener(e -> {
                    try {
                        openTextFile(file);
                    } catch (IOException e1) {
                        // Debugging
                        e1.printStackTrace();
                    }
                });
                tempDialog.add(openText);
            // Image files can be opened to be viewed
            } else if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg")
                    || file.getName().endsWith(".jpeg")) {
                JButton openImage = new JButton("Open Image");
                openImage.addActionListener(e -> openImageFile(file));
                tempDialog.add(openImage);
                // Audio files can be stored in memory or playlist and played later OR played as long as the audio player is opened
            } else if (file.getName().endsWith(".mp3") || file.getName().endsWith(".wav")) {
                // Add audio functionality if needed
            } else {
                JOptionPane.showMessageDialog(null, "System does not support this format"); // Ignore other formats
            }

            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // Clear the button and everything away after deleting
                    deleteFile(file);
                    panel.remove(button);
                    tempDialog.dispose();
                    dialog.revalidate();
                    dialog.repaint();
                }
                
            });
            tempDialog.add(deleteButton);
            // Button to clear the temporary dialog
            JButton goBack = new JButton("Go Back");
            goBack.addActionListener(e -> tempDialog.dispose());
            tempDialog.add(goBack);

            tempDialog.setVisible(true);
        });
    }
    // Helper method to open pdf files differently from txt and md files
    private static void openTextFile(File file) throws IOException {
        if (file.getName().endsWith(".pdf")) {
            Desktop.getDesktop().open(file); // Use the system's default PDF viewer
            return;
        }
        // Open the file as a text file
        JDialog textDialog = new JDialog();
        textDialog.setTitle(file.getName());
        textDialog.setSize(400, 300);

        JTextArea textArea = new JTextArea();
        try {
            textArea.setText(Files.readString(file.toPath()));
        } catch (IOException e) {
            textArea.setText("Error reading file.");
        }
        textArea.setFont(new Font("Arial", Font.BOLD, 14)); // Set font
        textArea.setEditable(false); // Make the text area non-editable
        JScrollPane scrollPane = new JScrollPane(textArea); // Make it scrollable in case the text is too long
        textDialog.add(scrollPane);

        textDialog.setVisible(true);
    }
    // Helper method to open image files
    private static void openImageFile(File file) {
        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "File does not exist");
            return;
        }
        // Open the file as an image in a dialog
        JDialog imageDialog = new JDialog();
        imageDialog.setTitle(file.getName());
        imageDialog.setSize(1280, 720);

        // Create an image label with the file path and copy the image to the label
        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        JLabel imageLabel = new JLabel(icon);

        // Make the label scrollable if its too large
        JScrollPane scrollPane = new JScrollPane(imageLabel);
        imageDialog.add(scrollPane);

        imageDialog.setVisible(true);
    }
    // Helper method to delete files
    private static void deleteFile(File file) {
        if (file.exists()) {
            // Prompt user for confirmation
            int response = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to delete " + file.getName() + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                try {
                    Files.delete(file.toPath()); // Delete the file
                    JOptionPane.showMessageDialog(null, "File deleted successfully!");
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Error deleting file: " + e1.getMessage());
                }
            }
        }
    }
}
