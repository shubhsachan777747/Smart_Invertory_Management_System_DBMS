import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ActivityLogPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public ActivityLogPanel() {
        setBackground(new Color(43,43,43));
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"ID","User","Action","Time"},0);
        table = new JTable(model);
        table.setBackground(new Color(60,63,65)); table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(74,134,232)); table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);
        load();
    }

    private void load(){
        model.setRowCount(0);
        try(Connection c=DatabaseUtil.getConnection();
            Statement s=c.createStatement();
            ResultSet rs=s.executeQuery("SELECT a.log_id,u.username,a.activity_type,a.description,a.created_at FROM ActivityLog a JOIN Users u ON a.user_id=u.user_id ORDER BY a.created_at DESC")){
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3)+" - "+rs.getString(4),rs.getTimestamp(5)});
        }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }
}