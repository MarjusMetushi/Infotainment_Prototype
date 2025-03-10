import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.JFileChooser;

public class playlist {
    // Declare the playlists and configuration
    public static String currentPathToPlaylist = "";
    public static Properties config = new Properties();
    public static boolean firstTime = true;

    // Constructor to initialize the playlist
    public playlist() throws IOException {
        System.out.println("Starting player...");
        //Process p = new ProcessBuilder("python","player.py").start();
    }

    // Method to read the current path of the playlist from the config file
    public static void getPlaylistCurrentPath() {
        loadConfig();
        currentPathToPlaylist = config.getProperty("PlaylistPath").strip().trim();
        sendCommandToPython("reset");
    }

    // Helper method to load the config file
    private static void loadConfig() {
        try (FileInputStream fileInputStream = new FileInputStream("config.properties")) {
            config.load(fileInputStream);
        } catch (IOException e) {
            // Handle missing config file or error loading it
            System.err.println("Error loading config file: " + e.getMessage());
        }
    }
    
    public static void shutdown() {
        sendCommandToPython("shutdown");
    }

    public static void next() {
        sendCommandToPython("next");
    }

    public static void previous() {
        sendCommandToPython("prev");
    }

    public static void pause() {
        sendCommandToPython("pause");
    }

    public static void resume() {
        sendCommandToPython("resume");
    }

    public static void selectPlaylist() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Select Playlist");
        fileChooser.setCurrentDirectory(new File(currentPathToPlaylist));
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String savePath = fileChooser.getSelectedFile().getAbsolutePath();
            overwritePlaylistPath(savePath);
        }
    }
    

    public static void overwritePlaylistPath(String savePath) {
        savePath = savePath.replace("\\", "/");
        config.setProperty("PlaylistPath", savePath);
        try {
            config.store(new FileWriter("config.properties"), "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendCommandToPython(String command) {
        try (Socket socket = new Socket("localhost", 12347);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                if (firstTime) {
                    out.println("start");
                    firstTime = false;
                    return;
                }
            out.println(command); // Send the command to the Python server
            System.out.println("Sent command: " + command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
