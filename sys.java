
import javax.swing.SwingUtilities;
public class sys {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserInterface ui = new UserInterface();
            ui.setVisible(true);
        });
    }
}