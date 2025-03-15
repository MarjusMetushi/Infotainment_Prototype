
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import org.json.JSONObject;

/*
    TO DO!
 *  Add the dashcam functionality
 *  Update the speed continuously in a thread
 *  IMPLEMENT THE LOGIC FOR THE MARQUEE
 */
//TODO: Fix previous song functionality

public class UserInterface extends JFrame {
    // Setting up basic settings
    int uiWidth = 1280;
    int uiHeight = 720;
    Properties config = new Properties();
    Color backgroundColor;
    Color foregroundColor;
    Color buttonBorderColor;
    // Setting up the panels
    JPanel topPanel = new JPanel(new BorderLayout());
    JPanel bottomPanel = new JPanel(new GridBagLayout());
    JPanel bottomLeftPanel = new JPanel(new GridLayout(2, 1));
    JPanel bottomRightPanel = new JPanel(new BorderLayout());
    Float currentVolume = 0.1f;
    VolumeBarPanel volumeBarPanel = new VolumeBarPanel((int)(currentVolume * 100));
    // Setting up the buttons
    JButton volumeUpButton = new JButton("+");
    JButton volumeDownButton = new JButton("-");
    JButton muteButton = new JButton("MUTE");
    boolean pause = false;
    public static MarqueeButton playing = new MarqueeButton("");
    watcher w = new watcher();

    // Constructor to set up the UI
    @SuppressWarnings({ "OverridableMethodCallInConstructor" })
    public UserInterface() {
        // Loading settings
        loadConfig();
        // Loading the playlist
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new playlist();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread1.start();
        // Getting the background and foreground colors from the properties file and
        // getting the color from a string
        currentVolume = Float.parseFloat(config.getProperty("volume"));
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        volumeBarPanel.setCurrentVolume((int) (currentVolume*100));
        if (backgroundColor == Color.BLACK)
            buttonBorderColor = Color.decode(config.getProperty("borderColor1"));
        else
            buttonBorderColor = Color.decode(config.getProperty("borderColor2"));
        // Setting up the frame
        setTitle("M37U5H1");
        setSize(uiWidth, uiHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setForeground(foregroundColor);
        // Adding components to the frame
        addComponentsTop();
        addComponentsBottomLeft();
        addComponentsBottomRight();
        // Customize the panels
        topPanel.setBackground(backgroundColor);
        topPanel.setForeground(foregroundColor);
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.setForeground(foregroundColor);
        bottomLeftPanel.setBackground(backgroundColor);
        bottomLeftPanel.setForeground(foregroundColor);
        bottomRightPanel.setBackground(backgroundColor);
        bottomRightPanel.setForeground(foregroundColor);
        // Add bottom panels with GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridwidth = 1; // Set grid width for left panel
        bottomPanel.add(bottomLeftPanel, gbc);
        gbc.gridwidth = 2; // Set grid width for right panel (the wider panel)
        bottomPanel.add(bottomRightPanel, gbc);
        // Adding everything together
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
        add(volumeBarPanel, BorderLayout.WEST);
    }

    // Method to load the configurations
    @SuppressWarnings("ConvertToTryWithResources")
    public void loadConfig() {
        config = new Properties();
        try {
            // Load properties from a file
            FileInputStream fileInputStream = new FileInputStream("config.properties");
            config.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            // For debugging
        }
    }

    // Method to add components to the top panel and Customize
    public void addComponentsTop() {
        // Set time and date
        JTextField timeAndDateField = new JTextField("Time: 12:00, Date: 2024-11-16");
        // Customize the JTextArea
        timeAndDateField.setHorizontalAlignment(JTextField.CENTER);
        timeAndDateField.setEditable(false);
        timeAndDateField.setBackground(backgroundColor);
        timeAndDateField.setForeground(foregroundColor);
        timeAndDateField.setBorder(new LineBorder(buttonBorderColor, 2));
        new Time(timeAndDateField, "HH:mm:ss, yyyy-MM-dd");
        topPanel.add(timeAndDateField, BorderLayout.CENTER);
    }

    // Method to Customize Bottom left panel
    @SuppressWarnings("Convert2Lambda")
    public void addComponentsBottomLeft() {
        // Setting up the panels
        JPanel speedometerPanel = new JPanel();
        JPanel quickAccessPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        JPanel wrapperPanel = new JPanel();
        JButton carHealthButton = new JButton("Car Health");
        JButton shutdown = new JButton("Shutdown");
        // Action listener to open the car health log
        carHealthButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                new healthLog();
            }

        });
        shutdown.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    watcher.stop();
                }));
                playlist.shutdown();
            }

        });
        // Customizing borders for reduced spacing
        quickAccessPanel.setBorder(BorderFactory.createEmptyBorder(40, -50, 10, 0));
        speedometerPanel.setBorder(BorderFactory.createEmptyBorder(180, -50, 0, 0));
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(20, -50, 85, 0));
        // Setting up the buttons and textareas
        int speed = 50;
        SemiCircularSpeedometer basicInfoArea = new SemiCircularSpeedometer(speed);
        JButton selfieButton = new JButton("CAM");
        JButton weatherButton = new JButton("WEATHER");
        // Add action listeners to the buttons
        selfieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ProcessBuilder pb = new ProcessBuilder("python", "camera.py");
                    pb.start();
                } catch (Exception e1) {
                    // For debugging
                }
            }
        });

        weatherButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new Weather();
                } catch (Exception e1) {
                    // For debugging
                }
            }
        });
        muteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    volumeCommands("mute");
                } catch (IOException e1) {
                    // For debugging
                }
            }
        });
        volumeUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    volumeCommands("increase");
                } catch (IOException e1) {
                    // For debugging
                }
            }
        });
        volumeDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    volumeCommands("decrease");
                } catch (IOException e1) {
                    // For debugging
                }
            }
        });
        // Customization for the buttons and JTextArea
        shutdown.setPreferredSize(new Dimension(100, 50));
        shutdown.setFont(new Font("Arial", Font.BOLD, 13));
        basicInfoArea.setFont(new Font("Arial", Font.BOLD, 15));
        basicInfoArea.setForeground(Color.decode("#AEDBF0"));
        basicInfoArea.setPreferredSize(new Dimension(200, 200));
        selfieButton.setPreferredSize(new Dimension(80, 50));
        weatherButton.setPreferredSize(new Dimension(80, 50));
        volumeDownButton.setPreferredSize(new Dimension(80, 50));
        volumeUpButton.setPreferredSize(new Dimension(80, 50));
        muteButton.setPreferredSize(new Dimension(80, 50));
        muteButton.setFont(new Font("Arial", Font.BOLD, 15));
        carHealthButton.setPreferredSize(new Dimension(100, 50));
        carHealthButton.setFont(new Font("Arial", Font.BOLD, 13));
        // Customize buttons
        CustomizeButtons(selfieButton);
        CustomizeButtons(weatherButton);
        CustomizeButtons(volumeUpButton);
        CustomizeButtons(volumeDownButton);
        CustomizeButtons(carHealthButton);
        CustomizeButtons(muteButton);
        CustomizeButtons(shutdown);
        // Customize panels
        wrapperPanel.setBackground(backgroundColor);
        wrapperPanel.setForeground(foregroundColor);
        speedometerPanel.setBackground(backgroundColor);
        speedometerPanel.setForeground(foregroundColor);
        quickAccessPanel.setBackground(backgroundColor);
        quickAccessPanel.setForeground(foregroundColor);
        bottomLeftPanel.setLayout(new BoxLayout(bottomLeftPanel, BoxLayout.Y_AXIS));
        // Adding everything together
        speedometerPanel.add(basicInfoArea);
        quickAccessPanel.add(muteButton);
        quickAccessPanel.add(volumeUpButton);
        quickAccessPanel.add(volumeDownButton);
        quickAccessPanel.add(selfieButton);
        quickAccessPanel.add(weatherButton);
        bottomLeftPanel.add(speedometerPanel);
        bottomLeftPanel.add(Box.createVerticalStrut(2));
        bottomLeftPanel.add(quickAccessPanel);
        bottomLeftPanel.add(Box.createVerticalStrut(2));
        wrapperPanel.add(carHealthButton);
        wrapperPanel.add(shutdown);
        bottomLeftPanel.add(wrapperPanel);
    }

    // Method to add the components to the bottom right panel
    @SuppressWarnings("Convert2Lambda")
    public void addComponentsBottomRight() {
        JPanel appsPanel = new JPanel(null);
        JButton maps = new JButton("Maps");
        JButton media = new JButton("Media");
        JButton dashcam = new JButton("Dashcam");
        JButton dashboard = new JButton("Dashboard");
        JButton gallery = new JButton("Gallery");
        JButton settings = new JButton("Settings");
        media.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Media();
            }
        });
        gallery.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Gallery();
            }
        });
        dashboard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new dashboard();
                } catch (IOException e1) {
                    // Debugging
                    e1.printStackTrace();
                }
            }
        });
        maps.addActionListener(new ActionListener() {
            @SuppressWarnings("static-access")
            @Override
            public void actionPerformed(ActionEvent e) {
                new maps().openMaps();
            }
        });
        // Customizing the buttons
        CustomizeButtons(maps);
        CustomizeButtons(media);
        CustomizeButtons(dashcam);
        CustomizeButtons(dashboard);
        CustomizeButtons(gallery);
        CustomizeButtons(settings);
        // Setting the bounds, spacing and offset
        int buttonWidth = 250;
        int buttonHeight = 150;
        int spacing = 25;
        int verticalOffset = 130;
        // Specifying the location of each button
        // Row 1
        maps.setBounds(0, verticalOffset, buttonWidth, buttonHeight);
        media.setBounds(0 + buttonWidth + spacing, verticalOffset, buttonWidth, buttonHeight);
        dashcam.setBounds(0 + 2 * (buttonWidth + spacing), verticalOffset, buttonWidth, buttonHeight);
        // Row 2
        dashboard.setBounds(0, verticalOffset + buttonHeight + spacing, buttonWidth, buttonHeight);
        gallery.setBounds(0 + buttonWidth + spacing, verticalOffset + buttonHeight + spacing, buttonWidth,
                buttonHeight);
        settings.setBounds(0 + 2 * (buttonWidth + spacing), verticalOffset + buttonHeight + spacing, buttonWidth,
                buttonHeight);
        // Adding everything together
        appsPanel.add(maps);
        appsPanel.add(media);
        appsPanel.add(dashcam);
        appsPanel.add(dashboard);
        appsPanel.add(gallery);
        appsPanel.add(settings);
        // Setting up the lower part of the panel
        JPanel musicPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        musicPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 110, 20));
        JButton pauseOrPlay = new JButton("Pause/Play");
        pauseOrPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pause) {
                    pause = false;
                    pauseOrPlay.setText("Play");
                    playlist.resume();
                } else {
                    pause = true;
                    pauseOrPlay.setText("Pause");
                    playlist.pause();
                }
            }
        });
        JButton previous = new JButton("<<");
        previous.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playlist.previous();
            }
        });
        // SET THE LOGIC TO MAKE A MARQUEE LIKE EFFECT FOR THE JTEXTFIELD!!!

        playing.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playlist.selectPlaylist();
            }
        });
        playing.setFont(new Font("Arial", Font.BOLD, 14));
        JButton next = new JButton(">>");
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playlist.next();
            }
        });
        // Customization for the buttons and panels
        playing.setHorizontalAlignment(JTextField.CENTER);
        playing.setBorder(BorderFactory.createLineBorder(buttonBorderColor, 2));
        playing.setMaximumSize(new Dimension(120, 30));
        playing.setMinimumSize(new Dimension(120, 30));
        musicPanel.setBackground(backgroundColor);
        musicPanel.setForeground(foregroundColor);
        appsPanel.setBackground(backgroundColor);
        appsPanel.setForeground(foregroundColor);
        playing.setBackground(backgroundColor);
        playing.setForeground(foregroundColor);
        CustomizeMusicButtons(pauseOrPlay);
        CustomizeMusicButtons(previous);
        CustomizeMusicButtons(next);
        // Adding everything together
        musicPanel.add(pauseOrPlay);
        musicPanel.add(previous);
        musicPanel.add(playing);
        musicPanel.add(next);
        bottomRightPanel.add(musicPanel, BorderLayout.SOUTH);
        bottomRightPanel.add(appsPanel, BorderLayout.CENTER);
        w = new watcher();
        Thread t = new Thread(w);
        t.start();
    }

    // Method to Customize musicPanel's Buttons
    public void CustomizeMusicButtons(JButton btn) {
        btn.setBackground(backgroundColor);
        btn.setForeground(foregroundColor);
        btn.setPreferredSize(new Dimension(10, 50));
        btn.setBorder(BorderFactory.createLineBorder(buttonBorderColor, 2));
        btn.setFocusable(false);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
    }

    // Method to Customize bottom right panel buttons
    public void CustomizeButtons(JButton btn) {
        btn.setBackground(backgroundColor);
        btn.setForeground(foregroundColor);
        btn.setBorder(BorderFactory.createLineBorder(buttonBorderColor, 2));
        btn.setFocusable(false);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
    }

    // Method to fetch the color based on the string
    public Color getColorFromString(String color) {
        return switch (color.toLowerCase()) {
            case "black" -> Color.BLACK;
            case "white" -> Color.WHITE;
            case "gray" -> Color.GRAY;
            case "red" -> Color.RED;
            case "blue" -> Color.BLUE;
            default -> Color.WHITE; // Default value to be returned
        };
    }

    // Method to control the volume
    public void volumeCommands(String command) throws IOException {
        if (command.equals("mute")) {
            if (muteButton.getText().equals("MUTE")) {
                muteButton.setText("UNMUTE");
                volumeBarPanel.setCurrentVolume(0);
            }else{
                muteButton.setText("MUTE");
                volumeBarPanel.setCurrentVolume((int) (0.1f * 100));
            }
            playlist.mute();
        }else if (command.equals("increase")) {
            currentVolume = Math.min(currentVolume + 0.1f, 1.0f);
            playlist.setVolume(currentVolume);
            volumeBarPanel.setCurrentVolume((int) (currentVolume * 100));
        } else if (command.equals("decrease")) {
            currentVolume = Math.max(currentVolume - 0.1f, 0.0f);
            playlist.setVolume(currentVolume);
            volumeBarPanel.setCurrentVolume((int) (currentVolume * 100));
        }   
        // write the changes to the config file
        config.setProperty("volume", String.valueOf(currentVolume));
        try {
            config.store(new FileWriter("config.properties"), "");
        } catch (IOException e) {
            // DEBUGGING
        }
    }

    // Method to play music by taking the file path
    public static void playMusic(String filePath)
            throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        try {
            // Turn input file path into a file object
            File audioFile = new File(filePath);

            if (!audioFile.exists()) {
                System.out.println("The audio file does not exist: " + filePath);
                return;
            }

            // Obtain an audio stream
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // Get the audio format
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            if (!AudioSystem.isLineSupported(info)) {
                return;
            }

            // Obtain the clip and open it with the audio stream
            Clip audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);

            // Add a listener to know when playback is complete
            audioClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    audioClip.close(); // Close the clip
                }
            });

            // Start the audio clip
            audioClip.start();

            // Keep the program running until the clip finishes playing
            while (audioClip.isRunning()) {
                Thread.sleep(100);
            }

            // Close resources
            audioStream.close();
        } catch (UnsupportedAudioFileException e) {
            // For debugging
        } catch (IOException e) {
            // For debugging
        } catch (LineUnavailableException e) {
            // For debugging
        } catch (InterruptedException e) {
            // For debugging
        }
    }
    // Helper method to read the name of the current song at the JSON file
    public static void readName() {
        try {
            // Get the JSON
            String content = new String(Files.readAllBytes(Paths.get("player_state.json")));
            // Check if the JSON is valid
            if(content.startsWith("{")){
                JSONObject jsonObject = new JSONObject(content);
                String lastSong = jsonObject.getString("last_song"); // Get the last song

                // Split the path by '\\' and store it in an array
                String[] pathArray = lastSong.split("\\\\");
                String musicName = pathArray[pathArray.length - 1];
                playing.updateText(musicName); // Set the text to the song playing
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}