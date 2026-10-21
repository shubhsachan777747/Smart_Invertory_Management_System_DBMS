import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ShopMessagePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextArea messageArea;
    private RoundedTextField subjectField;
    private RoundedButton sendBtn, refreshBtn;

    public ShopMessagePanel() {
        setBackground(new Color(43,43,43)); setLayout(new BorderLayout(5,5));
        model = new DefaultTableModel(new String[]{"ID","Subject","Message","Status","Admin Reply"},0);
        table = new JTable(model); table.setBackground(new Color(60,63,65)); table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15)); table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(74,134,232)); table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel compose = new JPanel(new BorderLayout()); compose.setBackground(new Color(50,50,50));
        subjectField = new RoundedTextField(30);
        subjectField.setPlaceholder("📝 Subject");
        messageArea = new JTextArea(3,30); messageArea.setBackground(new Color(60,63,65));
        messageArea.setForeground(Color.WHITE); messageArea.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false); top.add(subjectField, BorderLayout.CENTER);
        sendBtn = new RoundedButton("📩 Send"); refreshBtn = new RoundedButton("🔄 Refresh");
        JPanel btns = new JPanel(new FlowLayout()); btns.setOpaque(false); btns.add(sendBtn); btns.add(refreshBtn);
        top.add(btns, BorderLayout.EAST); compose.add(top, BorderLayout.NORTH);
        compose.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        add(compose, BorderLayout.SOUTH);

        sendBtn.addActionListener(e->sendMessage()); refreshBtn.addActionListener(e->loadMessages());
        loadMessages();
    }

    private void loadMessages(){
        model.setRowCount(0);
        try(Connection c=DatabaseUtil.getConnection();
            PreparedStatement ps=c.prepareStatement(
                    "SELECT message_id,subject,message_body,status,admin_reply FROM Messages " +
                            "WHERE sender_id=? OR receiver_id IS NULL " +
                            "ORDER BY created_at DESC")){
            ps.setInt(1, Session.userId);
            ResultSet rs=ps.executeQuery();
            while(rs.next())
                model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5)});
        }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void sendMessage(){
        String subj=subjectField.getText().trim(); String body=messageArea.getText().trim();
        if(subj.isEmpty()||body.isEmpty()){ JOptionPane.showMessageDialog(this,"❌ Fill subject and message"); return; }
        try(Connection c=DatabaseUtil.getConnection();
            PreparedStatement ps=c.prepareStatement("INSERT INTO Messages (sender_id,receiver_id,subject,message_body) VALUES (?,1,?,?)")){
            ps.setInt(1,Session.userId); ps.setString(2,subj); ps.setString(3,body);
            ps.executeUpdate(); loadMessages(); subjectField.setText(""); messageArea.setText("");
            JOptionPane.showMessageDialog(this,"✅ Message sent to admin!");
        }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }
}