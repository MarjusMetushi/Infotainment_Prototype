import java.io.*;
import java.util.*;
import java.net.*;
import javax.swing.JFileChooser;

/*
 * Lets remodel the whole playlist class
 * The functions will include only pausing, resuming, skipping, looping  and restarting the mixer in case there is a new playlist
 * The playlist will be loaded with the last song played in the playlist
 * And it will start as soon as the user presses play
 * Python will do most of the work but these commands will interact with the server for the mixer
 * In case of pause -> python will pause
 * In case of resume -> python will resume or start the last song
 * In case of next -> python will play the next song
 * In case of previous -> python will play the previous song
 * In case of a loop -> python will play the last song again
 * The order will always be shuffled and start from the beginning of the shuffled queue if the system is closed and reopened
 */


public class playlist {
    // Declare the playlists and configuration
    public static Stack<String> toPlay = new Stack<String>();
    public static Stack<String> played = new Stack<String>();
    public static boolean loop = false;
    public static boolean shuffle = false;
    public static String currentPathToPlaylist = "";
    public static Properties config = new Properties();

    // Constructor to initialize the playlist
    public playlist() throws IOException {
        System.out.println("Starting player...");
        Process p = new ProcessBuilder("python","player.py").start();
        getPlaylistCurrentPath();
        loadPlaylist();
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
            File[] folder = file.listFiles();
            if (folder != null) {
                for (File song : folder) {
                    toPlay.push(song.getAbsolutePath()); // Add songs to the 'toPlay' stack
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
        }
    }
    //Helper method to call a Python script to play the song
    public static void callPythonScript(String song) {
        if ("pause".equals(song) || "resume".equals(song)) {
            sendCommandToPython(song);  // Directly send "pause" or "resume"
        } else {
            sendCommandToPython("play " + song);  // Send play command with the song
        }
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
        try (Socket socket = new Socket("localhost", 12347);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(command); // Send the command to the Python server
            System.out.println("Sent command: " + command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
