import javax.swing.*;
import java.awt.*;

public class NotificationDialog {
    public static int showConfirm(Component parent, String message, String title) {
        UIManager.put("OptionPane.background", new Color(50,50,50));
        UIManager.put("Panel.background", new Color(50,50,50));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        UIManager.put("OptionPane.font", new Font("Segoe UI Emoji", Font.PLAIN, 16));
        UIManager.put("Button.background", new Color(74,134,232));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI Emoji", Font.BOLD, 14));
        return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}