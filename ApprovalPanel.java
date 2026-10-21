import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ApprovalPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public ApprovalPanel() {
        setBackground(new Color(43,43,43));
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"ID","Username","Full Name","Shop Name","Email","Phone","Status"},0);
        table = new JTable(model);
        table.setBackground(new Color(60,63,65)); table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(74,134,232)); table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout());
        btns.setOpaque(false);
        RoundedButton approve = new RoundedButton("✅ Approve"); approve.addActionListener(e->process("approved"));
        RoundedButton reject = new RoundedButton("❌ Reject"); reject.addActionListener(e->process("rejected"));
        btns.add(approve); btns.add(reject);
        add(btns, BorderLayout.SOUTH);
        load();
    }

    private void load(){
        model.setRowCount(0);
        try(Connection c=DatabaseUtil.getConnection();
            Statement s=c.createStatement();
            ResultSet rs=s.executeQuery("SELECT * FROM PendingRegistrations WHERE status='pending'")){
            while(rs.next()) model.addRow(new Object[]{rs.getInt("request_id"),rs.getString("username"),rs.getString("full_name"),rs.getString("shop_name"),rs.getString("email"),rs.getString("phone"),rs.getString("status")});
        }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void process(String status){
        int r=table.getSelectedRow(); if(r==-1)return;
        int id=(int)model.getValueAt(r,0);
        try(Connection c=DatabaseUtil.getConnection()){
            c.setAutoCommit(false);
            PreparedStatement up=c.prepareStatement("UPDATE PendingRegistrations SET status=?,processed_by=?,processed_at=NOW() WHERE request_id=?");
            up.setString(1,status); up.setInt(2,Session.userId); up.setInt(3,id); up.executeUpdate();
            if(status.equals("approved")){
                PreparedStatement sel=c.prepareStatement("SELECT * FROM PendingRegistrations WHERE request_id=?");
                sel.setInt(1,id); ResultSet rs=sel.executeQuery();
                if(rs.next()){
                    PreparedStatement ins=c.prepareStatement("INSERT INTO Users (username,password,full_name,email,role,shop_name,shop_address,phone) VALUES (?,?,?,?,'shop_owner',?,?,?)");
                    ins.setString(1,rs.getString("username")); ins.setString(2,rs.getString("password"));
                    ins.setString(3,rs.getString("full_name")); ins.setString(4,rs.getString("email"));
                    ins.setString(5,rs.getString("shop_name")); ins.setString(6,rs.getString("shop_address"));
                    ins.setString(7,rs.getString("phone")); ins.executeUpdate();
                }
            }
            c.commit(); load();
        }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }
}