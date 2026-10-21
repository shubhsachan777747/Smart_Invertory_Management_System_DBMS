import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SupplierPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField fNameField, lNameField, contactField;
    private RoundedButton addBtn, updateBtn, deleteBtn, refreshBtn;
    private boolean isAdmin;
    private int shopId;

    public SupplierPanel(boolean isAdmin, int shopId) {
        this.isAdmin = isAdmin; this.shopId = shopId;
        setBackground(new Color(43,43,43));
        setLayout(new BorderLayout(5,5));
        model = new DefaultTableModel(new String[]{"ID","First Name","Last Name","Contact","Shop ID"},0);
        table = new JTable(model);
        table.setBackground(new Color(60,63,65)); table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15)); table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(74,134,232)); table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout()); form.setBackground(new Color(50,50,50));
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets=new Insets(5,5,5,5); gbc.anchor=GridBagConstraints.WEST;
        addField(form,gbc,0,"First Name:",fNameField=new RoundedTextField(12));
        addField(form,gbc,1,"Last Name:",lNameField=new RoundedTextField(12));
        addField(form,gbc,2,"Contact:",contactField=new RoundedTextField(12));
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; gbc.anchor=GridBagConstraints.CENTER;
        JPanel btns=new JPanel(new FlowLayout(FlowLayout.CENTER,10,0)); btns.setOpaque(false);
        addBtn=new RoundedButton("➕ Add"); updateBtn=new RoundedButton("✏ Update");
        deleteBtn=new RoundedButton("🗑 Delete"); refreshBtn=new RoundedButton("🔄 Refresh");
        btns.add(addBtn); btns.add(updateBtn); btns.add(deleteBtn); btns.add(refreshBtn);
        form.add(btns,gbc);
        add(form,BorderLayout.SOUTH);
        loadData();
        table.getSelectionModel().addListSelectionListener(e->{ if(!e.getValueIsAdjusting()&&table.getSelectedRow()!=-1){
            int r=table.getSelectedRow(); fNameField.setText((String)model.getValueAt(r,1));
            lNameField.setText((String)model.getValueAt(r,2)); contactField.setText((String)model.getValueAt(r,3));}});
        addBtn.addActionListener(e->addSupplier()); updateBtn.addActionListener(e->updateSupplier());
        deleteBtn.addActionListener(e->deleteSupplier()); refreshBtn.addActionListener(e->loadData());
    }

    private void addField(JPanel p, GridBagConstraints gbc, int y, String label, JTextField f){
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=1;
        JLabel lbl = new JLabel(label); lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); p.add(lbl,gbc);
        gbc.gridx=1; p.add(f,gbc);
    }

    private String filter(){ return isAdmin?"":" WHERE shop_id="+shopId; }

    private void loadData(){
        model.setRowCount(0);
        try(Connection c=DatabaseUtil.getConnection();
            Statement s=c.createStatement();
            ResultSet rs=s.executeQuery("SELECT * FROM Supplier"+filter())){
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getObject("shop_id")});
        }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void addSupplier(){
        String f=fNameField.getText().trim(), l=lNameField.getText().trim(), c=contactField.getText().trim();
        try(Connection conn=DatabaseUtil.getConnection();
            PreparedStatement ps=conn.prepareStatement("INSERT INTO Supplier (First_name,Last_name,Contact_info,shop_id,added_by) VALUES (?,?,?,?,?)")){
            ps.setString(1,f); ps.setString(2,l); ps.setString(3,c);
            ps.setObject(4,isAdmin?null:shopId); ps.setInt(5,Session.userId);
            ps.executeUpdate(); loadData(); clear();
        }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void updateSupplier(){
        int r=table.getSelectedRow(); if(r==-1)return; int id=(int)model.getValueAt(r,0);
        String f=fNameField.getText().trim(), l=lNameField.getText().trim(), c=contactField.getText().trim();
        try(Connection conn=DatabaseUtil.getConnection();
            PreparedStatement ps=conn.prepareStatement("UPDATE Supplier SET First_name=?,Last_name=?,Contact_info=? WHERE Supplier_ID=?")){
            ps.setString(1,f); ps.setString(2,l); ps.setString(3,c); ps.setInt(4,id);
            ps.executeUpdate(); loadData();
        }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void deleteSupplier(){
        int r=table.getSelectedRow(); if(r==-1)return; int id=(int)model.getValueAt(r,0);
        if(JOptionPane.showConfirmDialog(this,"Delete?")==JOptionPane.YES_OPTION){
            try(Connection conn=DatabaseUtil.getConnection();
                PreparedStatement ps=conn.prepareStatement("DELETE FROM Supplier WHERE Supplier_ID=?")){
                ps.setInt(1,id); ps.executeUpdate(); loadData(); clear();
            }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
        }
    }

    private void clear(){ fNameField.setText(""); lNameField.setText(""); contactField.setText(""); }
}