import javax.swing.*;
import java.awt.*;

public class AdminDashboardPanel extends JPanel {
    private JFrame parentFrame;
    private AnimatedPanel container;

    public AdminDashboardPanel(JFrame frame, AnimatedPanel cont) {
        this.parentFrame = frame; this.container = cont;
        setLayout(new BorderLayout());
        setBackground(new Color(43,43,43));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(50,50,50));
        JLabel title = new JLabel("👑 Admin Panel  |  " + Session.fullName);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(10,20,10,0));
        topBar.add(title, BorderLayout.WEST);

        RoundedButton logoutBtn = new RoundedButton("🚪 Logout");
        logoutBtn.addActionListener(e -> {
            Session.clear();
            container.switchTo(new LoginPanel(parentFrame, container));
        });
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnP.setOpaque(false);
        btnP.add(logoutBtn);
        topBar.add(btnP, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        AnimatedPanel contentPanel = new AnimatedPanel(null);
        SidebarPanel sidebar = new SidebarPanel(contentPanel, true);
        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        contentPanel.setInitialPanel(new DashboardHomePanel());
    }
}