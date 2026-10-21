import javax.swing.*;
import java.awt.*;

public class AdminInventoryPanel extends JPanel {
    public AdminInventoryPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        tabs.addTab("🏷 Suppliers", new SupplierPanel(true, 0));
        tabs.addTab("📦 Products", new ProductPanel(true, 0));
        tabs.addTab("📋 Orders", new OrderPanel(true, 0));
        tabs.addTab("💰 Sales", new SalePanel(true, 0));
        add(tabs, BorderLayout.CENTER);
    }
}