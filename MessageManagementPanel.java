import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MessageManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextArea replyArea;
    private RoundedButton replyBtn, resolveBtn;

    public MessageManagementPanel() {
        setBackground(new Color(43,43,43));
        setLayout(new BorderLayout(5,5));

        // ✅ Table model that does NOT allow editing
        model = new DefaultTableModel(new String[]{"ID","From","Subject","Message","Status","Priority"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setBackground(new Color(60,63,65));
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(74,134,232));
        table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Reply area and buttons
        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        replyArea = new JTextArea(3, 30);
        replyArea.setBackground(new Color(60,63,65));
        replyArea.setForeground(Color.WHITE);
        replyArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        replyArea.setLineWrap(true);
        replyArea.setWrapStyleWord(true);

        JPanel btns = new JPanel(new FlowLayout());
        btns.setOpaque(false);
        replyBtn = new RoundedButton("💬 Reply");
        resolveBtn = new RoundedButton("✔ Resolve");
        replyBtn.addActionListener(e -> reply());
        resolveBtn.addActionListener(e -> resolve());
        btns.add(replyBtn);
        btns.add(resolveBtn);

        south.add(new JScrollPane(replyArea), BorderLayout.CENTER);
        south.add(btns, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        loadMessages();
    }

    private void loadMessages() {
        model.setRowCount(0);
        String sql = "SELECT m.message_id, u.username, m.subject, m.message_body, m.status, m.priority " +
                "FROM Messages m " +
                "LEFT JOIN Users u ON m.sender_id = u.user_id " +
                "ORDER BY m.created_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("message_id"),
                        rs.getString("username") == null ? "Broadcast" : rs.getString("username"),
                        rs.getString("subject"),
                        rs.getString("message_body"),
                        rs.getString("status"),
                        rs.getString("priority")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error loading messages: " + e.getMessage());
        }
    }

    private void reply() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a message to reply.");
            return;
        }
        int msgId = (int) model.getValueAt(row, 0);
        String reply = replyArea.getText().trim();
        if (reply.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Reply cannot be empty.");
            return;
        }
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE Messages SET admin_reply = ?, status = 'read' WHERE message_id = ?")) {
            ps.setString(1, reply);
            ps.setInt(2, msgId);
            ps.executeUpdate();
            replyArea.setText("");
            loadMessages();
            JOptionPane.showMessageDialog(this, "✅ Reply sent.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error sending reply: " + e.getMessage());
        }
    }

    private void resolve() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a message to resolve.");
            return;
        }
        int msgId = (int) model.getValueAt(row, 0);
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "UPDATE Messages SET status = 'resolved', resolved_at = NOW() WHERE message_id = ?")) {
            ps.setInt(1, msgId);
            ps.executeUpdate();
            loadMessages();
            JOptionPane.showMessageDialog(this, "✅ Message resolved.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "❌ Error resolving message: " + e.getMessage());
        }
    }
}