import javax.swing.*;
import java.awt.*;

public class DashboardHomePanel extends JPanel {
    public DashboardHomePanel() {
        setBackground(new Color(43,43,43));
        setLayout(new GridBagLayout());
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(500,250));
        card.setBackground(new Color(60,63,65));
        card.setBorder(BorderFactory.createLineBorder(new Color(74,134,232),1));
        card.setLayout(new GridBagLayout());
        JLabel welcome = new JLabel("Welcome, " + Session.fullName + "!");
        welcome.setFont(new Font("Segoe UI Emoji", Font.BOLD, 26));
        welcome.setForeground(Color.WHITE);
        card.add(welcome);
        add(card);
    }
}