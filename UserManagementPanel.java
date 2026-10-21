import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserManagementPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public UserManagementPanel() {
        setBackground(new Color(43,43,43));
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"ID","Username","Full Name","Role","Shop","Active"},0);
        table = new JTable(model);
        table.setBackground(new Color(60,63,65)); table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(74,134,232)); table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout());
        btns.setOpaque(false);
        RoundedButton deactivate = new RoundedButton("❌ Deactivate");
        deactivate.addActionListener(e->toggleActive(false));
        RoundedButton activate = new RoundedButton("✅ Activate");
        activate.addActionListener(e->toggleActive(true));
        btns.add(deactivate); btns.add(activate);
        add(btns, BorderLayout.SOUTH);
        loadUsers();
    }

    private void loadUsers() {
        model.setRowCount(0);
        try(Connection c=DatabaseUtil.getConnection();
            Statement s=c.createStatement();
            ResultSet rs=s.executeQuery("SELECT user_id,username,full_name,role,shop_name,is_active FROM Users")){
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),
                    rs.getString(4),rs.getString(5),rs.getBoolean(6)});
        }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void toggleActive(boolean active){
        int r=table.getSelectedRow(); if(r==-1)return;
        int id=(int)model.getValueAt(r,0);
        try(Connection c=DatabaseUtil.getConnection();
            PreparedStatement ps=c.prepareStatement("UPDATE Users SET is_active=? WHERE user_id=?")){
            ps.setBoolean(1,active); ps.setInt(2,id); ps.executeUpdate();
            loadUsers();
        }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }
}