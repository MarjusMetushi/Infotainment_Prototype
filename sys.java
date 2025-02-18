import javax.swing.SwingUtilities;
// Create a bash file as an installer for the programming languages and libraries (Linux support only) 
// For windows use the processbuilders

public class sys {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserInterface ui = new UserInterface();
            ui.setVisible(true);
        });
    }
}