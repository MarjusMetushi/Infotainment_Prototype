import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Kodi {
    static Color backgroundColor;
    static Color foregroundColor;
    static Properties config = new Properties();
            
    public static void openKodi() throws IOException{
        loadconfig();
        backgroundColor = getColorFromString(config.getProperty("backgroundColor"));
        foregroundColor = getColorFromString(config.getProperty("foregroundColor"));
        if(iskodiInstalled()){
            openApp();
        }else{
            // Method to prompt the user to confirm the installation
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
    // Method to check if the Kodi app exists as an application
    public static boolean iskodiInstalled(){
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                Process process = Runtime.getRuntime().exec("cmd /c winget list --id XBMCFoundation.Kodi");
                return process.getInputStream().read() != -1; 
            } else if (os.contains("mac")) {
                Process process = Runtime.getRuntime().exec("mdfind 'kMDItemCFBundleIdentifier == \"org.xbmc.kodi\"'");
                return process.getInputStream().read() != -1;
            } else if (os.contains("nix") || os.contains("nux")) {
                Process process = Runtime.getRuntime().exec("which Kodi");
                return process.getInputStream().read() != -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    // Method to start the app
    public static void openApp() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                String[] paths = {"C:\\Program Files\\Kodi\\kodi.exe", "C:\\Program Files (x86)\\Kodi\\kodi.exe"};
                for (String path : paths) {
                    if (new File(path).exists()) {
                        new ProcessBuilder(path).start();
                        return;
                    }
                }
                String kodiPath = findFile(new File("C:\\Program Files"), "kodi.lnk");
                if(kodiPath != null) new ProcessBuilder(kodiPath).start();
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open -a Kodi");  
            } else if (os.contains("nix") || os.contains("nux")) {
                Runtime.getRuntime().exec("kodi");  
            } else {
                System.out.println("Native Kodi app not supported on this OS.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Method to find a file
    public static String findFile(File dir, String name) {
        if(!dir.exists()) return null;
        File[] files = dir.listFiles();
        if(files == null) return null;
        for(File file : files){
            if(file.isDirectory()){
                String found = findFile(file, name);
                if(found != null) return found;
            }
        }
        return null;
    }
    
    // Method to prompt the user to confirm the installation via a dialog box
    public static void promptUser(){
        JDialog dialog = new JDialog();
        JPanel mainPanel = new JPanel();
        JPanel topPanel = new JPanel();

        mainPanel.setBackground(backgroundColor);
        mainPanel.setForeground(foregroundColor);

        topPanel.setBackground(backgroundColor);
        topPanel.setForeground(foregroundColor);

        dialog.setTitle("Do you want to install Kodi?");
        dialog.setSize(400, 200);
        dialog.setBackground(backgroundColor);
        dialog.setForeground(foregroundColor);

        JTextArea textField = new JTextArea(
            "Kodi is a free and open-source media player \n" +
            "application developed by the XBMC Foundation. \n\n" +
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
        yes.setFont(new Font("Arial", Font.BOLD, 16));

        no.setFocusable(false);
        no.setBackground(backgroundColor);
        no.setForeground(foregroundColor);
        no.setFont(new Font("Arial", Font.BOLD, 16));

        yes.setPreferredSize(new Dimension(100, 50));
        no.setPreferredSize(new Dimension(100, 50));

        yes.addActionListener(e->{
            try {
                downloadKodi();
            } catch (IOException e1) {
                // Debugging
                e1.printStackTrace();
            }
            dialog.dispose();
        });
        no.addActionListener(e->{
            dialog.dispose();
        });
        topPanel.add(textField);
        mainPanel.add(yes);
        mainPanel.add(no);
        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    // Method to download the Kodi app by running the bash script (Only works on Linux)
    public static void downloadKodi() throws IOException {
        String path = "kodi.sh";
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;
    
        if (os.contains("win")) {
            pb = new ProcessBuilder("cmd", "/c", "winget install -e --id XBMCFoundation.Kodi --accept-source-agreements --accept-package-agreements");
        } else if (os.contains("mac")) {
            pb = new ProcessBuilder("brew", "install", "--cask", "kodi");
        } else if (os.contains("nix") || os.contains("nux")) {
            pb = new ProcessBuilder("bash", path);
        } else {
            System.out.println("Downloading Kodi is not supported on this OS.");
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
