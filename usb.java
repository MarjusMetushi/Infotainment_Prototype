import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Properties;
import javax.swing.*; 

public class usb {
    // Declare variables
    static JDialog dialog;
    static String path = "";
    static Color backgroundColor;
    static Color foregroundColor;
    static Properties config = new Properties();
    static JPanel panel;
    static JPanel lowerPanel;
    static int elements = 0;
    static String playlistPath = "";

    public static void startusb() {
        // Load config to load system's variables
        loadConfig();
        backgroundColor = getColorFromString(config.getProperty("backgroundColor", "white"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor", "black"));
        playlistPath = config.getProperty("PlaylistPath").strip().trim();

        // UI and costumization
        dialog = new JDialog();
        dialog.setTitle("USB Explorer");
        dialog.setSize(1280, 720);
        dialog.setLayout(new BorderLayout());


        // Create the files panel
        panel = new JPanel();
        panel.setBackground(backgroundColor);
        panel.setForeground(backgroundColor);

        // Create the lower panel with buttons
        lowerPanel = new JPanel();
        lowerPanel.setLayout(new GridLayout(1, 2));
        lowerPanel.setBackground(backgroundColor);

        JButton goBack = new JButton("Exit");
        customizeButton(goBack);
        JButton again = new JButton("Open");
        customizeButton(again);
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

        panel.setLayout(new GridLayout(0, 3, 5, 5));
        // Add all files and directories to the panel
        for (int i = 0; i < allFiles.size(); i++) {
            File f = allFiles.get(i);
            JButton button = new JButton(f.getName()); // Create a button for each file
            customizeButton(button);
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
        button.addActionListener(evt1 -> { 
            // Create dialog with modern styling
            JDialog tempDialog = new JDialog();
            tempDialog.setTitle(button.getText());
            tempDialog.setSize(400, 300);
            tempDialog.setLayout(new BorderLayout());
            tempDialog.getContentPane().setBackground(backgroundColor);
    
            // Panel to hold buttons with vertical alignment
            JPanel tempPanel = new JPanel();
            tempPanel.setLayout(new BoxLayout(tempPanel, BoxLayout.Y_AXIS));
            tempPanel.setBackground(backgroundColor);
            tempPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Add padding
    
            // Wrapper panel for centering buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(0, 1, 10, 10)); // Vertical layout with spacing
            buttonPanel.setBackground(backgroundColor);
    
            // Text files
            if (file.getName().endsWith(".txt") || file.getName().endsWith(".md") || file.getName().endsWith(".pdf")) {
                JButton openText = new JButton("Open Text");
                customizeButton(openText);
                openText.addActionListener(e -> {
                    try {
                        openTextFile(file);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });
                buttonPanel.add(openText);
            } 
            // Image files
            else if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
                JButton openImage = new JButton("Open Image");
                customizeButton(openImage);
                openImage.addActionListener(e -> openImageFile(file));
                buttonPanel.add(openImage);
            } 
            // Audio files
            else if (file.getName().endsWith(".mp3")) {
                JButton playButton = new JButton("Play audio");
                customizeButton(playButton);
                playButton.addActionListener(e -> {
                    try {
                        System.out.println("Playing " + file.getAbsolutePath());
                        ProcessBuilder processBuilder = new ProcessBuilder("python", "audio.py", file.getAbsolutePath());
                        processBuilder.inheritIO();
                        processBuilder.start();
                        tempDialog.dispose();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                });
                JButton addToPlaylist = new JButton("Add to playlist");
                customizeButton(addToPlaylist);
                addToPlaylist.addActionListener(e -> {
                    try {
                        promptPlaylists(file.getAbsolutePath());
                    } catch (IOException e1) {
                        e1.printStackTrace(); // DEBUGGING
                    }
                });
                buttonPanel.add(playButton);
                buttonPanel.add(addToPlaylist);
            } 
            // Unsupported formats
            else {
                JOptionPane.showMessageDialog(null, "System does not support this format", 
                                              "Unsupported Format", JOptionPane.WARNING_MESSAGE);
                return;
            }
    
            // Delete button
            JButton deleteButton = new JButton("Delete");
            customizeButton(deleteButton);
            deleteButton.addActionListener(e -> {
                if (deleteFile(file)) {
                    panel.remove(button);
                    tempDialog.dispose();
                    dialog.revalidate();
                    dialog.repaint();
                }
            });
            buttonPanel.add(deleteButton);
    
            // Go Back button
            JButton goBack = new JButton("Go Back");
            customizeButton(goBack);
            goBack.addActionListener(e -> tempDialog.dispose());
            buttonPanel.add(goBack);
    
            // Add button panel to the dialog
            tempPanel.add(buttonPanel);
            tempDialog.add(tempPanel, BorderLayout.CENTER);
    
            // Center the dialog on screen
            tempDialog.setLocationRelativeTo(null);
            tempDialog.setVisible(true);
        });
    }
    
    // Helper method to prompt the user to select a playlist
    private static void promptPlaylists(String sourcePath) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Only allow directories to be selected
        fileChooser.setDialogTitle("Select Playlist"); // Set the title of the dialog
        fileChooser.setCurrentDirectory(new File(playlistPath));
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String savePath = fileChooser.getSelectedFile().getAbsolutePath();
            saveSongToPlaylist(sourcePath, savePath);
        }
    }

    // Helper method to save a song to the playlist
    private static void saveSongToPlaylist(String sourcePath, String savePath) throws IOException{
        Files.copy(Paths.get(sourcePath), Paths.get(savePath, new File(sourcePath).getName()), StandardCopyOption.REPLACE_EXISTING);
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
    private static boolean deleteFile(File file) {
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
                    return true;
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Error deleting file: " + e1.getMessage());
                }
            } else {
                return false;
            }
        }
        return false;
    }

    // Helper method to costumize buttons
    private static void customizeButton(JButton button) {
        button.setPreferredSize(new Dimension(100, 70));
        button.setFont(new Font("Arial", Font.BOLD, 16)); // Set font
        button.setBackground(backgroundColor);
        button.setForeground(foregroundColor);

        JLabel label = new JLabel(button.getText()){
            @Override
            public void setText(String text) {
                super.setText("<html><body><p style='width: 100px;'>" + text + "</p></body></html>");
            }
        };
        label.setForeground(foregroundColor);
        label.setBackground(backgroundColor);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        button.setText("");
        button.add(label);
    }
}