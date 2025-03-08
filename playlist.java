import java.io.*;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.JFileChooser;

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
        currentPathToPlaylist = config.getProperty("PlaylistPath").strip().trim();
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
                    toPlay.push(song); // Add songs to the 'toPlay' stack
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
            played.push(songToPlay); // Move the song to the 'played' stack

            callPythonScript(songToPlay);

            // If loop is enabled, play the last song again after finishing it
            if (loop) {
                playSong(played.peek()); // Repeat the song (loop)
            }
            if (!toPlay.isEmpty()) {
                playSong(toPlay.peek()); // Play the next song if there is one
            }
        }
    }

    // Example method to call a Python script to play the song
    public static void callPythonScript(String song) {
        sendCommandToPython("play "+song);
        if (!toPlay.isEmpty())
            playSong(toPlay.peek()); // Play the next song if there is one
    }

    public static void next() {
        if (!toPlay.isEmpty()) {
            String song = toPlay.pop();
            played.push(song);
            if (!toPlay.isEmpty()) {
                callPythonScript(toPlay.peek());
                System.out.println("Playing next song: " + toPlay.peek());
            }
        }
    }

    public static void previous() {
        if (!played.isEmpty()) {
            String song = played.pop();
            toPlay.push(song);
            callPythonScript(song);
            System.out.println("Playing previous song: " + played.peek());
        }
    }

    public static void pause() {
        callPythonScript("pause");
        System.out.println("Paused");
    }

    public static void resume() {
        callPythonScript("resume");
        System.out.println("Resumed");
    }

    public static void shuffle() {
        ArrayList<String> shuffled = new ArrayList<>(toPlay);
        shuffled.addAll(played);
        Collections.shuffle(shuffled);

        toPlay.clear();
        played.clear();

        toPlay.addAll(shuffled);

        if (!toPlay.isEmpty()) {
            callPythonScript(toPlay.peek());
            System.out.println("Shuffled");
        }
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
        try (Socket socket = new Socket("localhost", 12346);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(command); // Send the command to the Python server
            System.out.println("Sent command: " + command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
