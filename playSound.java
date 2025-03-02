import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;

//TODO: Find a way to pause the sound at a specific frame and store that value

public class playSound {
    // Declare variables
    private AdvancedPlayer player;
    private FileInputStream fileInputStream;
    private String filepath;
    private boolean isPaused = false;
    private int pausedFrame = 0;
    private Thread playThread;
    static Properties config = new Properties();
    static Color backgroundColor;
    static Color foregroundColor;

    // Constructor
    public playSound(String filepath) {
        this.filepath = filepath;
        loadConfig();
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        setUI();

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

    // Helper method to setup the UI
    private void setUI() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Playing " + filepath);
        dialog.setSize(400, 300);
        dialog.setLayout(new FlowLayout());
        dialog.setBackground(backgroundColor);
        dialog.setForeground(foregroundColor);

        // Buttons for pausing, resuming and stopping the sound
        JButton pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> pause());
        customizeButton(pauseButton);
        JButton resumeButton = new JButton("Resume");
        resumeButton.addActionListener(e -> resume());
        customizeButton(resumeButton);
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stop();
                dialog.dispose();
                playThread = null;
                player = null;
            }
        });
        customizeButton(stopButton);

        dialog.add(pauseButton);
        dialog.add(resumeButton);
        dialog.add(stopButton);

        dialog.setVisible(true);
    }

    // Helper method to play the sound
    public void play() {
        if (playThread != null && playThread.isAlive()) {
            return;
        }

        playThread = new Thread(() -> {
            try {
                fileInputStream = new FileInputStream(filepath);
                player = new AdvancedPlayer(fileInputStream);

                player.setPlayBackListener(new PlaybackListener() {
                    @Override
                    public void playbackFinished(PlaybackEvent evt) {
                        pausedFrame = evt.getFrame(); // Get the frame
                    }
                });

                isPaused = false;
                player.play(pausedFrame, Integer.MAX_VALUE);
            } catch (JavaLayerException | IOException e) {
                e.printStackTrace();
            }
        });
        playThread.start();
    }

    // Pauses the sound and stores the current frame
    public void pause() {
        if (player != null) {
            isPaused = true;
            pausedFrame = getCurrentFrame();
            player.close();
        }
    }

    // Retrieve current frame before pausing
    public int getCurrentFrame() {

        return 0;
    }

    // Helper method to resume
    public void resume() {
        if (isPaused) {
            playAtPausedFrame(pausedFrame);
        }
    }

    // Plays the sound from the last frame it was paused at
    private void playAtPausedFrame(int frame) {

    }

    // Stops the sound
    public void stop() {
        if (player != null) {
            player.close();
            pausedFrame = 0; // Reset paused frame
        }
    }

    // Helper method to costumize buttons
    private static void customizeButton(JButton button) {
        button.setPreferredSize(new Dimension(100, 70));
        button.setFont(new Font("Arial", Font.BOLD, 10)); // Set font
        button.setBackground(backgroundColor);
        button.setForeground(foregroundColor);
    }
}
