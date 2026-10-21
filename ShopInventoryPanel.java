import javax.swing.*;
import java.awt.*;

public class ShopInventoryPanel extends JPanel {
    public ShopInventoryPanel() {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        tabs.addTab("🏷 Suppliers", new SupplierPanel(false, Session.userId));
        tabs.addTab("📦 Products", new ProductPanel(false, Session.userId));
        tabs.addTab("📋 Orders", new OrderPanel(false, Session.userId));
        tabs.addTab("💰 Sales", new SalePanel(false, Session.userId));
        add(tabs, BorderLayout.CENTER);
    }
}