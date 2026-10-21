import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SalePanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField productField, totalField;
    private RoundedButton addBtn, updateBtn, deleteBtn, refreshBtn;
    private boolean isAdmin;
    private int shopId;

    public SalePanel(boolean isAdmin, int shopId) {
        this.isAdmin=isAdmin; this.shopId=shopId;
        setBackground(new Color(43,43,43)); setLayout(new BorderLayout(5,5));
        model = new DefaultTableModel(new String[]{"ID","Product ID","Total Sale","Date"},0);
        table = new JTable(model); table.setBackground(new Color(60,63,65)); table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15)); table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        table.getTableHeader().setBackground(new Color(74,134,232)); table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridBagLayout()); form.setBackground(new Color(50,50,50));
        GridBagConstraints gbc=new GridBagConstraints(); gbc.insets=new Insets(5,5,5,5); gbc.anchor=GridBagConstraints.WEST;
        addField(form,gbc,0,"Product ID:",productField=new RoundedTextField(12));
        addField(form,gbc,1,"Total Sale:",totalField=new RoundedTextField(12));
        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2; gbc.anchor=GridBagConstraints.CENTER;
        JPanel btns=new JPanel(new FlowLayout(FlowLayout.CENTER,10,0)); btns.setOpaque(false);
        addBtn=new RoundedButton("➕ Add"); updateBtn=new RoundedButton("✏ Update");
        deleteBtn=new RoundedButton("🗑 Delete"); refreshBtn=new RoundedButton("🔄 Refresh");
        btns.add(addBtn); btns.add(updateBtn); btns.add(deleteBtn); btns.add(refreshBtn);
        form.add(btns,gbc); add(form,BorderLayout.SOUTH);
        loadData();
        table.getSelectionModel().addListSelectionListener(e->{ if(!e.getValueIsAdjusting()&&table.getSelectedRow()!=-1){
            int r=table.getSelectedRow(); productField.setText(String.valueOf(model.getValueAt(r,1)));
            totalField.setText(String.valueOf(model.getValueAt(r,2)));}});
        addBtn.addActionListener(e->addSale()); updateBtn.addActionListener(e->updateSale());
        deleteBtn.addActionListener(e->deleteSale()); refreshBtn.addActionListener(e->loadData());
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
            ResultSet rs=s.executeQuery("SELECT Sale_ID,Product_ID,Total_sale,sale_date FROM Sale"+filter())){
            while(rs.next()) model.addRow(new Object[]{rs.getInt(1),rs.getInt(2),rs.getDouble(3),rs.getDate(4)});
        }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void addSale(){
        try(Connection conn=DatabaseUtil.getConnection();
            PreparedStatement ps=conn.prepareStatement("INSERT INTO Sale (Product_ID,Total_sale,sale_date,added_by) VALUES (?,?,CURDATE(),?)")){
            ps.setInt(1,Integer.parseInt(productField.getText().trim())); ps.setDouble(2,Double.parseDouble(totalField.getText().trim()));
            ps.setInt(3,Session.userId); ps.executeUpdate(); loadData(); clear();
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void updateSale(){
        int r=table.getSelectedRow(); if(r==-1)return; int id=(int)model.getValueAt(r,0);
        try(Connection conn=DatabaseUtil.getConnection();
            PreparedStatement ps=conn.prepareStatement("UPDATE Sale SET Product_ID=?,Total_sale=? WHERE Sale_ID=?")){
            ps.setInt(1,Integer.parseInt(productField.getText().trim())); ps.setDouble(2,Double.parseDouble(totalField.getText().trim()));
            ps.setInt(3,id); ps.executeUpdate(); loadData();
        }catch(Exception e){JOptionPane.showMessageDialog(this,e.getMessage());}
    }

    private void deleteSale(){
        int r=table.getSelectedRow(); if(r==-1)return; int id=(int)model.getValueAt(r,0);
        if(JOptionPane.showConfirmDialog(this,"Delete?")==JOptionPane.YES_OPTION){
            try(Connection conn=DatabaseUtil.getConnection();
                PreparedStatement ps=conn.prepareStatement("DELETE FROM Sale WHERE Sale_ID=?")){
                ps.setInt(1,id); ps.executeUpdate(); loadData(); clear();
            }catch(SQLException e){JOptionPane.showMessageDialog(this,e.getMessage());}
        }
    }

    private void clear(){ productField.setText(""); totalField.setText(""); }
}