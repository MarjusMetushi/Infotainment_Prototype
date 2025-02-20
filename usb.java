import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;
import javax.swing.*;
//TODO: FIX THE FRONT-END
//TODO: ADD THE FEATURE TO RECURSIVELY SHOW EVERY FILE
//TODO: FIX THE ISSUE WITH READING PDF/TEXT FILES
public class usb {
    static JDialog dialog;
    static String path = "";
    static Color backgroundColor;
    static Color foregroundColor;
    static Properties config = new Properties();
    static JPanel panel;

    public static void usb() {
        loadConfig();
        backgroundColor = getColorFromString(config.getProperty("backgroundColor", "white"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor", "black"));

        dialog = new JDialog();
        dialog.setTitle("USB Explorer");
        dialog.setSize(1280, 720);
        dialog.setLayout(new BorderLayout());

        panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(backgroundColor);

        dialog.add(new JScrollPane(panel), BorderLayout.CENTER);
        dialog.setVisible(true);

        getPath();
        showFiles();
    }

    private static void loadConfig() {
        try (FileInputStream fileInputStream = new FileInputStream("config.properties")) {
            config.load(fileInputStream);
        } catch (IOException ignored) {
            // DEBUGGING
        }
    }

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

    private static void getPath() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path = fileChooser.getSelectedFile().getAbsolutePath();
        }
    }

    private static void showFiles() {
        File[] files = new File(path).listFiles();
        if (files == null) return;

        for (File f : files) {
            JButton button = new JButton(f.getName());
            button.setPreferredSize(new Dimension(200, 100));
            setFunctionality(button, f);
            panel.add(button);
        }
        dialog.revalidate();
        dialog.repaint();
    }

    private static void setFunctionality(JButton button, File file) {
        button.addActionListener(evt1 -> {
            JDialog tempDialog = new JDialog();
            tempDialog.setTitle(button.getText());
            tempDialog.setSize(300, 200);
            tempDialog.setLayout(new FlowLayout());

            if (file.getName().endsWith(".txt") || file.getName().endsWith(".md") || file.getName().endsWith(".pdf")) {
                JButton openText = new JButton("Open Text");
                openText.addActionListener(e -> openTextFile(file));
                tempDialog.add(openText);
            } else if (file.getName().endsWith(".png") || file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg")) {
                JButton openImage = new JButton("Open Image");
                openImage.addActionListener(e -> openImageFile(file));
                tempDialog.add(openImage);
            } else if (file.getName().endsWith(".mp3") || file.getName().endsWith(".wav")) {
                // Add audio functionality if needed
            } else {
                JOptionPane.showMessageDialog(null, "System does not support this format");
            }

            JButton deleteButton = new JButton("Delete");
            //TODO: ALSO DELETE THE BUTTON
            deleteButton.addActionListener(e -> deleteFile(file));
            tempDialog.add(deleteButton);

            JButton goBack = new JButton("Go Back");
            goBack.addActionListener(e -> tempDialog.dispose());
            tempDialog.add(goBack);

            tempDialog.setVisible(true);
        });
    }

    private static void openTextFile(File file) {
        JDialog textDialog = new JDialog();
        textDialog.setTitle(file.getName());
        textDialog.setSize(400, 300);

        JTextArea textArea = new JTextArea();
        try {
            textArea.setText(Files.readString(file.toPath()));
        } catch (IOException e) {
            textArea.setText("Error reading file.");
        }

        JScrollPane scrollPane = new JScrollPane(textArea);
        textDialog.add(scrollPane);

        textDialog.setVisible(true);
    }

    private static void openImageFile(File file) {
        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "File does not exist");
            return;
        }

        JDialog imageDialog = new JDialog();
        imageDialog.setTitle(file.getName());
        imageDialog.setSize(500, 500);

        ImageIcon icon = new ImageIcon(file.getAbsolutePath());
        JLabel imageLabel = new JLabel(icon);

        JScrollPane scrollPane = new JScrollPane(imageLabel);
        imageDialog.add(scrollPane);

        imageDialog.setVisible(true);
    }

    private static void deleteFile(File file) {
        if (file.exists()) {
            int response = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to delete " + file.getName() + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                try {
                    Files.delete(file.toPath());
                    JOptionPane.showMessageDialog(null, "File deleted successfully!");
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Error deleting file: " + e1.getMessage());
                }
            }
        }
    }
}
