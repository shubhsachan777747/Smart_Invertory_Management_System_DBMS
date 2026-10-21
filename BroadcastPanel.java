import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class BroadcastPanel extends JPanel {
    private JTextArea messageArea;
    private RoundedTextField subjectField;      // ✅ changed to RoundedTextField
    private RoundedButton sendBtn, refreshBtn;
    private JLabel statusLabel;

    public BroadcastPanel() {
        setBackground(new Color(43,43,43));
        setLayout(new BorderLayout(10,10));

        JPanel form = new JPanel(new BorderLayout(10,10));
        form.setBackground(new Color(50,50,50));
        form.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        subjectField = new RoundedTextField(30);    // ✅ use RoundedTextField
        subjectField.setPlaceholder("📢 Subject");  // ✅ now works

        messageArea = new JTextArea(5,30);
        messageArea.setBackground(new Color(60,63,65));
        messageArea.setForeground(Color.WHITE);
        messageArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(subjectField, BorderLayout.CENTER);
        sendBtn = new RoundedButton("📤 Send Broadcast");
        refreshBtn = new RoundedButton("🔄 Clear");
        JPanel btns = new JPanel(new FlowLayout());
        btns.setOpaque(false);
        btns.add(sendBtn);
        btns.add(refreshBtn);
        top.add(btns, BorderLayout.EAST);
        form.add(top, BorderLayout.NORTH);
        form.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        statusLabel.setForeground(Color.WHITE);
        form.add(statusLabel, BorderLayout.SOUTH);
        add(form, BorderLayout.CENTER);

        sendBtn.addActionListener(e -> sendBroadcast());
        refreshBtn.addActionListener(e -> {
            subjectField.setText("");
            messageArea.setText("");
            statusLabel.setText(" ");
        });
    }

    private void sendBroadcast() {
        String subj = subjectField.getText().trim();
        String body = messageArea.getText().trim();
        if (subj.isEmpty() || body.isEmpty()) {
            statusLabel.setText("❌ Subject and message cannot be empty.");
            return;
        }
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO Messages (sender_id, receiver_id, subject, message_body, status) VALUES (?, NULL, ?, ?, 'unread')")) {
            ps.setInt(1, Session.userId);
            ps.setString(2, "📢 " + subj);
            ps.setString(3, body);
            ps.executeUpdate();
            statusLabel.setText("✅ Broadcast sent to all shop owners!");
            subjectField.setText("");
            messageArea.setText("");
        } catch (SQLException e) {
            statusLabel.setText("❌ Error: " + e.getMessage());
        }
    }
}