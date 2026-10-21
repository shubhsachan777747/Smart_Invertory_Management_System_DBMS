import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ProductPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField nameField, priceField, supplierField, quantityField;
    private RoundedButton addBtn, updateBtn, deleteBtn, refreshBtn;
    private boolean isAdmin;
    private int shopId;

    public ProductPanel(boolean isAdmin, int shopId) {
        this.isAdmin=isAdmin; this.shopId=shopId;
        setBackground(new Color(43,43,43)); setLayout(new BorderLayout(5,5));
        model = new DefaultTableModel(new String[]{"ID","Name","Price","Supplier ID","Quantity"},0);
        table = new JTable(model); table.setBackground(new Color(60,63,65)); table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15)); table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(74,134,232)); table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout()); form.setBackground(new Color(50,50,50));
        GridBagConstraints gbc=new GridBagConstraints(); gbc.insets=new Insets(5,5,5,5); gbc.anchor=GridBagConstraints.WEST;
        addField(form,gbc,0,"Name:",nameField=new RoundedTextField(12));
        addField(form,gbc,1,"Price:",priceField=new RoundedTextField(12));
        addField(form,gbc,2,"Supplier ID:",supplierField=new RoundedTextField(12));
        addField(form,gbc,3,"Quantity:",quantityField=new RoundedTextField(12));
        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=2; gbc.anchor=GridBagConstraints.CENTER;
        JPanel btns=new JPanel(new FlowLayout(FlowLayout.CENTER,10,0)); btns.setOpaque(false);
        addBtn=new RoundedButton("➕ Add"); updateBtn=new RoundedButton("✏ Update");
        deleteBtn=new RoundedButton("🗑 Delete"); refreshBtn=new RoundedButton("🔄 Refresh");
        btns.add(addBtn); btns.add(updateBtn); btns.add(deleteBtn); btns.add(refreshBtn);
        form.add(btns,gbc); add(form,BorderLayout.SOUTH);
        loadData();
        table.getSelectionModel().addListSelectionListener(e->{ if(!e.getValueIsAdjusting()&&table.getSelectedRow()!=-1){
            int r=table.getSelectedRow(); nameField.setText((String)model.getValueAt(r,1));
            priceField.setText(String.valueOf(model.getValueAt(r,2))); supplierField.setText(String.valueOf(model.getValueAt(r,3)));
            quantityField.setText(String.valueOf(model.getValueAt(r,4)));}});
        addBtn.addActionListener(e->addProduct()); updateBtn.addActionListener(e->updateProduct());
        deleteBtn.addActionListener(e->deleteProduct()); refreshBtn.addActionListener(e->loadData());
    }

    private void addField(JPanel p, GridBagConstraints gbc, int y, String label, JTextField f){
        gbc.gridx=0; gbc.gridy=y; gbc.gridwidth=1;
        JLabel lbl = new JLabel(label); lbl.setForeground(Color.WHITE);
        lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); p.add(lbl,gbc);
        gbc.gridx=1; p.add(f,gbc);
    }

    private String filter(){ return isAdmin?"":" WHERE added_by="+shopId; }

    private void loadData(){
        model.setRowCount(0);
        try(Connection c=DatabaseUtil.getConnection();
            Statement s=c.createStatement();
            ResultSet rs=s.executeQuery("SELECT * FROM Product"+filter())){
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1),rs.getString(2),rs.getDouble(3),rs.getInt(4),rs.getInt(5)});
        }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void addProduct(){
        try(Connection conn=DatabaseUtil.getConnection();
            PreparedStatement ps=conn.prepareStatement("INSERT INTO Product (Product_name,Price,Supplier_ID,Quantity,added_by) VALUES (?,?,?,?,?)")){
            ps.setString(1,nameField.getText().trim()); ps.setDouble(2,Double.parseDouble(priceField.getText().trim()));
            ps.setInt(3,Integer.parseInt(supplierField.getText().trim())); ps.setInt(4,Integer.parseInt(quantityField.getText().trim()));
            ps.setInt(5,Session.userId); ps.executeUpdate(); loadData(); clear();
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void updateProduct(){
        int r=table.getSelectedRow(); if(r==-1)return; int id=(int)model.getValueAt(r,0);
        try(Connection conn=DatabaseUtil.getConnection();
            PreparedStatement ps=conn.prepareStatement("UPDATE Product SET Product_name=?,Price=?,Supplier_ID=?,Quantity=? WHERE Product_ID=?")){
            ps.setString(1,nameField.getText().trim()); ps.setDouble(2,Double.parseDouble(priceField.getText().trim()));
            ps.setInt(3,Integer.parseInt(supplierField.getText().trim())); ps.setInt(4,Integer.parseInt(quantityField.getText().trim()));
            ps.setInt(5,id); ps.executeUpdate(); loadData();
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void deleteProduct(){
        int r=table.getSelectedRow(); if(r==-1)return; int id=(int)model.getValueAt(r,0);
        if(JOptionPane.showConfirmDialog(this,"Delete?")==JOptionPane.YES_OPTION){
            try(Connection conn=DatabaseUtil.getConnection();
                PreparedStatement ps=conn.prepareStatement("DELETE FROM Product WHERE Product_ID=?")){
                ps.setInt(1,id); ps.executeUpdate(); loadData(); clear();
            }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
        }
    }

    private void clear(){ nameField.setText(""); priceField.setText(""); supplierField.setText(""); quantityField.setText(""); }
}