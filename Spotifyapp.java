import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.net.URI;
import java.util.Properties;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Spotifyapp {
    // Method to open Spotify if installed as an app otherwise as a website
    static Color foregroundColor;
    static Color backgroundColor;
    static Properties config = new Properties();
    public static void openSpotify() {
        loadconfig();
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        if (isSpotifyInstalled()) {
            openNativeApp();
        } else {
            promptUser();
        }
    }
    //Method to load the config file
    @SuppressWarnings("ConvertToTryWithResources")
    public static void loadconfig() {
        config = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream("config.properties");
            config.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            // Print stack trace for debugging
            
        }
    }
    //Method to fetch a color based on the string
    public static Color getColorFromString(String color) {
        return switch (color.toLowerCase()) {
            case "black" -> Color.BLACK;
            case "white" -> Color.WHITE;
            case "gray" -> Color.GRAY;
            case "red" -> Color.RED;
            case "blue" -> Color.BLUE;
            default -> Color.WHITE;
        };
    }
    // Method to check if the spotify app exists as an application
    private static boolean isSpotifyInstalled() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return checkPaths(
                System.getenv("LOCALAPPDATA") + "\\Microsoft\\WindowsApps\\Spotify.exe", 
                System.getenv("APPDATA") + "\\Spotify\\Spotify.exe",                     
                "C:\\Program Files\\Spotify\\Spotify.exe"                                 
            );
        } else if (os.contains("mac")) {
            return new File("/Applications/Spotify.app").exists();
        } else if (os.contains("nix") || os.contains("nux")) {
            return new File("/usr/bin/spotify").exists() || new File("/snap/bin/spotify").exists();
        }
        return false;
    }
    // Method to recursively check paths for spotify
    private static boolean checkPaths(String... paths) {
        for (String path : paths) {
            if (new File(path).exists()) {
                return true;
            }
        }
        return false;
    }

    // Method to start the app
    private static void openNativeApp() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                String spotifyPath = findSpotify();
                if (spotifyPath != null) {
                    try {
                        Runtime.getRuntime().exec(spotifyPath);
                        System.out.println("Spotify launched!");
                    } catch (IOException e) {
                        System.out.println("Failed to launch Spotify.");
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Spotify not found on this system.");
                }
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open Spotify://"); // macOS Spotify custom URI
            } else {
                System.out.println("Native Youtube app not supported on this OS.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Method to find a file
    public static String findSpotify() {
         String[] possiblePaths = {
            System.getenv("LOCALAPPDATA") + "\\Microsoft\\WindowsApps\\Spotify.exe",  // Microsoft Store
            System.getenv("APPDATA") + "\\Spotify\\Spotify.exe",                      // Roaming folder (winget/standalone)
            "C:\\Program Files\\Spotify\\Spotify.exe"                                 // Rare case
        };

        for (String path : possiblePaths) {
            File spotify = new File(path);
            if (spotify.exists()) {
                return spotify.getAbsolutePath();
            }
        }
        return null; // Spotify not found
    }
    // Method to open the website of Spotify
    private static void openURL(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                System.out.println("Desktop browsing is not supported on this system.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to prompt the user to confirm the installation via a dialog box
    public static void promptUser() {
        JDialog dialog = new JDialog();
        JPanel mainPanel = new JPanel();
        JPanel topPanel = new JPanel();

        mainPanel.setBackground(backgroundColor);
        mainPanel.setForeground(foregroundColor);

        topPanel.setBackground(backgroundColor);
        topPanel.setForeground(foregroundColor);

        dialog.setTitle("Do you want to install Spotify?");
        dialog.setSize(500, 200);
        dialog.setBackground(backgroundColor);
        dialog.setForeground(foregroundColor);

        JTextArea textField = new JTextArea(
            "Spotify is a free and open-source music\n" +
            "streaming service developed by Spotify AB. \n\n" +
            "Would you like to install it?"
        );
        
        textField.setEditable(false);
        textField.setFocusable(false);
        textField.setBackground(backgroundColor);
        textField.setForeground(foregroundColor);
        textField.setFont(new Font("Arial", Font.BOLD, 16));

        JButton yes = new JButton("Yes");
        JButton no = new JButton("No");
        yes.setFocusable(false);
        yes.setBackground(backgroundColor);
        yes.setForeground(foregroundColor);
        yes.setFont(new Font("Arial", Font.BOLD, 14));

        no.setFocusable(false);
        no.setBackground(backgroundColor);
        no.setForeground(foregroundColor);
        no.setFont(new Font("Arial", Font.BOLD, 14));

        yes.setPreferredSize(new Dimension(100, 50));
        no.setPreferredSize(new Dimension(100, 50));

        yes.addActionListener(e -> {
            try {
                downloadSpotify();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            dialog.dispose();
        });
        no.addActionListener(e -> {
            openURL("https://www.spotify.com");
            dialog.dispose();
        });
        topPanel.add(textField);
        mainPanel.add(yes);
        mainPanel.add(no);
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    // Method to download the Spotify app 
    public static void downloadSpotify() throws IOException {
        String path = "spotify.sh";
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;

        if (os.contains("win")) {
            pb = new ProcessBuilder("cmd", "/c",
                    "winget install -e --id Spotify.Spotify --accept-source-agreements --accept-package-agreements");
        } else if (os.contains("mac")) {
            pb = new ProcessBuilder("brew", "install", "--cask", "spotify");
        } else if (os.contains("nix") || os.contains("nux")) {
            pb = new ProcessBuilder("bash", path);
        } else {
            System.out.println("Downloading Spotify is not supported on this OS.");
            return;
        }

        pb.redirectErrorStream(true);
        Process p = pb.start();

        // For debugging
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String ln;
        while ((ln = br.readLine()) != null) {
            System.out.println(ln);
        }
    }
}
