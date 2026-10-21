import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SidebarPanel extends JPanel {
    private JPanel navPanel;
    private AnimatedPanel mainContainer;

    public SidebarPanel(AnimatedPanel mainContainer, boolean isAdmin) {
        this.mainContainer = mainContainer;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 0));
        setBackground(new Color(40,40,40));

        navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(new Color(40,40,40));
        navPanel.setBorder(BorderFactory.createEmptyBorder(20,15,20,15));

        if (isAdmin) {
            addButton("📊 Dashboard", e -> switchContent(new DashboardHomePanel()));
            addButton("👥 Users", e -> switchContent(new UserManagementPanel()));
            addButton("⏳ Approvals", e -> switchContent(new ApprovalPanel()));
            addButton("💬 Messages", e -> switchContent(new MessageManagementPanel()));
            addButton("📢 Broadcast", e -> switchContent(new BroadcastPanel()));   // ✅ NEW
            addButton("📈 Activity", e -> switchContent(new ActivityLogPanel()));
            addButton("📦 Inventory", e -> switchContent(new AdminInventoryPanel()));
        } else {
            addButton("📊 Dashboard", e -> switchContent(new DashboardHomePanel()));
            addButton("📦 Inventory", e -> switchContent(new ShopInventoryPanel()));
            addButton("💬 Messages", e -> switchContent(new ShopMessagePanel()));
        }

        JScrollPane scroll = new JScrollPane(navPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);
    }

    private void addButton(String text, ActionListener listener) {
        SidebarButton btn = new SidebarButton(text, "");
        btn.addActionListener(listener);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        navPanel.add(btn);
        navPanel.add(Box.createVerticalStrut(5));
    }

    private void switchContent(JPanel panel) {
        mainContainer.switchTo(panel);
    }
}