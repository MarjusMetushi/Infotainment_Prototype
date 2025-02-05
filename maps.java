import java.awt.Desktop;
import java.net.URI;
import java.io.IOException;

public class maps {
    // Method to open google maps if installed as an app otherwise as a website
    public static void openMaps() {
        if (isGoogleMapsInstalled()) {
            openNativeApp();
        } else {
            openURL("https://www.google.com/maps");
        }
    }

    // Method to check if the maps app exists as an application
    private static boolean isGoogleMapsInstalled() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                Process process = Runtime.getRuntime().exec("cmd /c where chrome");
                return process.getInputStream().read() != -1; 
            } else if (os.contains("mac")) {
                Process process = Runtime.getRuntime().exec("mdfind 'kMDItemCFBundleIdentifier == \"com.google.Maps\"'");
                return process.getInputStream().read() != -1;
            } else if (os.contains("nix") || os.contains("nux")) {
                Process process = Runtime.getRuntime().exec("which google-maps");
                return process.getInputStream().read() != -1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Method to start the app
    private static void openNativeApp() {
        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("win")) {
                Runtime.getRuntime().exec("cmd /c start googlemaps://");  // Windows custom URI scheme
            } else if (os.contains("mac")) {
                Runtime.getRuntime().exec("open googlemaps://"); // macOS Google Maps custom URI
            } else {
                System.out.println("Native Google Maps app not supported on this OS.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to open the website of google maps
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
}
