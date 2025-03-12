import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.JFileChooser;
// TODO: Fix the pygame volume boundaries and coordinate it with the bar
public class playlist {
    // Declare the playlists and configuration
    public static String currentPathToPlaylist = "";
    public static Properties config = new Properties();
    public static boolean firstTime = true;

    // Constructor to initialize the playlist
    public playlist() throws IOException {
        System.out.println("Starting player..."); // Prompt to check for errors
        new ProcessBuilder("python","player.py").start(); // Start the Python server
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
            System.err.println("Error loading config file: " + e.getMessage()); // checking for errors
        }
    }
    // Helper method to shut down the server
    public static void shutdown() {
        sendCommandToPython("shutdown");
    }
    // Helper method to play the next song
    public static void next() {
        sendCommandToPython("next");
    }
    // Helper method to play the previous song
    public static void previous() {
        sendCommandToPython("prev");
    }
    // Helper method to set volume
    public static void setVolume(float volume) {
        sendCommandToPython(String.valueOf(volume));
    }
    // Helper method to mute/unmute
    public static void mute(){
        sendCommandToPython("mute");
    }
    // Helper method to reset the player
    public static void pause() {
        sendCommandToPython("pause");
    }
    // Helper method to resume the music
    public static void resume() {
        sendCommandToPython("resume");
    }
    // Helper method to select a playlist path 
    public static void selectPlaylist() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // allow only directories
        fileChooser.setDialogTitle("Select Playlist");
        fileChooser.setCurrentDirectory(new File(currentPathToPlaylist));
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String savePath = fileChooser.getSelectedFile().getAbsolutePath();
            overwritePlaylistPath(savePath); // Overwrite the current path at config
        }
    }
    
    // Helper method that writes the new path to the config file
    public static void overwritePlaylistPath(String savePath) {
        savePath = savePath.replace("\\", "/");
        config.setProperty("PlaylistPath", savePath); // Writing to the correct property
        try {
            config.store(new FileWriter("config.properties"), ""); // storing the information
        } catch (IOException e) {
            // DEBUGGING
        }
    }
    // Helper method that communicates commands to the Python server
    public static void sendCommandToPython(String command) {
        try (Socket socket = new Socket("localhost", 12347); // Connect to this port in the local machine
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println(command); // Send the command to the Python server
            UserInterface.readName();
            System.out.println("Sent command: " + command);
        } catch (IOException e) {
            // DEBUGGING
        }
    }
}
