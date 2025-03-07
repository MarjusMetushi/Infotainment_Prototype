import java.io.*;
import java.util.*;

public class playlist {
    // Declare the playlists and configuration
    public static Stack<String> toPlay = new Stack<String>();
    public static Stack<String> played = new Stack<String>();
    public static boolean loop = false;
    public static boolean shuffle = false;
    public static String currentPathToPlaylist = "";
    public static Properties config = new Properties();

    // Constructor to initialize the playlist
    public playlist() {
        getPlaylistCurrentPath();
    }

    // Method to read the current path of the playlist from the config file
    public static void getPlaylistCurrentPath() {
        loadConfig();
        currentPathToPlaylist = config.getProperty("PlaylistPath");
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

    // Method to load the user's desired playlist
    public static void loadPlaylist() {
        toPlay.clear();
        played.clear();
        File file = new File(currentPathToPlaylist);

        if (file.exists() && file.isDirectory()) {
            String[] folder = file.list();
            if (folder != null) {
                for (String song : folder) {
                    toPlay.push(song);  // Add songs to the 'toPlay' stack
                }
            }
        } else {
            System.err.println("Invalid playlist path.");
        }
    }

    // Method to remove a song from the playlist and play it
    public static void playSong(String song) {
        if (!toPlay.isEmpty()) {
            String songToPlay = toPlay.pop();
            played.push(songToPlay);  // Move the song to the 'played' stack

            callPythonScript(songToPlay);

            // If loop is enabled, play the last song again after finishing it
            if (loop) {
                playSong(played.peek());  // Repeat the song (loop)
            }
        }
    }

    // Example method to call a Python script to play the song
    public static void callPythonScript(String song) {
        try {
            Process process = new ProcessBuilder("python", "play_music.py", song).start();
            process.waitFor();  // Wait for the process to finish
            if(!toPlay.isEmpty()) playSong(toPlay.peek()); // Play the next song if there is one
        } catch (IOException | InterruptedException e) {
            System.err.println("Error executing Python script: " + e.getMessage());
        }
    }

    //TODO: Method to switch to next song

    public static void next(){}

    //TODO: Method to switch to previous song
    public static void previous(){}

    //TODO: Method to shuffle the playlist
    public static void shuffle(){}
}
