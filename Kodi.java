import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.JButton;
import javax.swing.JDialog;

public class Kodi {
    private static boolean allows = false;
    public static void openKodi() throws IOException{
        if(iskodiInstalled()){
            openApp();
        }else{
            // Method to prompt the user to confirm the installation
            promptUser();
            if(allows){
                downloadKodi();
            }
        }
    }
    // Method to check if the Kodi app exists as an application
    public static boolean iskodiInstalled(){
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                Process process = Runtime.getRuntime().exec("cmd /c where Kodi");
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
                Runtime.getRuntime().exec("cmd /c start kodi");
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
    
    // Method to prompt the user to confirm the installation via a dialog box
    public static void promptUser(){
        JDialog dialog = new JDialog();
        dialog.setTitle("Do you want to install Kodi?");
        dialog.setSize(300, 200);
        JButton yes = new JButton("Yes");
        JButton no = new JButton("No");
        yes.addActionListener(e->{
            allows = true;
            dialog.dispose();
        });
        no.addActionListener(e->{
            allows = false;
            dialog.dispose();
        });
        dialog.add(yes);
        dialog.add(no);
        dialog.setVisible(true);
    }
    // Method to download the Kodi app by running the bash script (Only works on Linux)
    public static void downloadKodi() throws IOException{
        String path = "kodi.sh";
        String os = System.getProperty("os.name").toLowerCase();
        ProcessBuilder pb;
        if(os.contains("win")){
            pb = new ProcessBuilder("cmd", "/c", "winget install -e --id XBMCFoundation.Kodi");    
        }else if(os.contains("mac")){
            pb = new ProcessBuilder("brew","install","--cask","kodi");
        }else if(os.contains("nix") || os.contains("nux")){
            pb = new ProcessBuilder("bash", path);
        }else{
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
