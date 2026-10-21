import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Global font & emoji fixes
        Font defaultFont = new Font("Segoe UI Emoji", Font.PLAIN, 16);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("Button.font", new Font("Segoe UI Emoji", Font.BOLD, 16));
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("PasswordField.font", defaultFont);
        UIManager.put("TextArea.font", defaultFont);
        UIManager.put("Table.font", defaultFont);
        UIManager.put("TableHeader.font", new Font("Segoe UI Emoji", Font.BOLD, 16));

        new SplashScreen(() -> {
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("🔐 Inventory Pro");
                frame.setUndecorated(true);
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                AnimatedPanel container = new AnimatedPanel(null);
                LoginPanel login = new LoginPanel(frame, container);
                container.setInitialPanel(login);
                frame.setContentPane(container);
                frame.setVisible(true);
            });
        });
    }
}