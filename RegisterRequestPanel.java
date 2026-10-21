import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterRequestPanel extends JPanel {
    private JFrame parentFrame;
    private AnimatedPanel container;
    private RoundedTextField usernameField, fullNameField, emailField, shopNameField, addressField, phoneField;
    private RoundedPasswordField passwordField;
    private JLabel messageLabel;

    public RegisterRequestPanel(JFrame frame, AnimatedPanel cont) {
        this.parentFrame = frame; this.container = cont;
        setBackground(new Color(30,30,30));
        setLayout(new GridBagLayout());

        JPanel box = new JPanel(new GridBagLayout());
        box.setBackground(new Color(50,50,50,220));
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(74,134,232),1),
                BorderFactory.createEmptyBorder(20,30,20,30)));
        box.setPreferredSize(new Dimension(480, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,0,5,0);

        JLabel title = new JLabel("📝 Register New Shop", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 28));
        title.setForeground(new Color(74,134,232));
        box.add(title, gbc);

        usernameField = new RoundedTextField(20); usernameField.setPlaceholder("👤 Username");
        box.add(usernameField, gbc);
        passwordField = new RoundedPasswordField(20); passwordField.setPlaceholder("🔒 Password");
        box.add(passwordField, gbc);
        fullNameField = new RoundedTextField(20); fullNameField.setPlaceholder("🧑 Full Name");
        box.add(fullNameField, gbc);
        emailField = new RoundedTextField(20); emailField.setPlaceholder("📧 Email");
        box.add(emailField, gbc);
        shopNameField = new RoundedTextField(20); shopNameField.setPlaceholder("🏪 Shop Name");
        box.add(shopNameField, gbc);
        addressField = new RoundedTextField(20); addressField.setPlaceholder("📍 Address");
        box.add(addressField, gbc);
        phoneField = new RoundedTextField(20); phoneField.setPlaceholder("📞 Phone");
        box.add(phoneField, gbc);

        RoundedButton submitBtn = new RoundedButton("📤 Submit Request");
        submitBtn.setPreferredSize(new Dimension(220, 45));
        box.add(submitBtn, gbc);

        RoundedButton backBtn = new RoundedButton("⬅ Back to Login");
        backBtn.setNormalColor(new Color(120,120,120));
        box.add(backBtn, gbc);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        messageLabel.setForeground(Color.RED);
        box.add(messageLabel, gbc);

        add(box);

        submitBtn.addActionListener(e -> submitRequest());
        backBtn.addActionListener(e -> container.switchTo(new LoginPanel(parentFrame, container)));
    }

    private void submitRequest() {
        String uname=usernameField.getText().trim(); String pass=new String(passwordField.getPassword()).trim();
        String name=fullNameField.getText().trim(); String email=emailField.getText().trim();
        String shop=shopNameField.getText().trim(); String addr=addressField.getText().trim(); String phone=phoneField.getText().trim();
        if(uname.isEmpty()||pass.isEmpty()||name.isEmpty()||email.isEmpty()||shop.isEmpty()){
            messageLabel.setText("❌ Fill required fields"); return;
        }
        try(Connection conn=DatabaseUtil.getConnection()) {
            PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM Users WHERE username=? OR email=?");
            check.setString(1,uname); check.setString(2,email);
            ResultSet rs=check.executeQuery(); rs.next();
            if(rs.getInt(1)>0){ messageLabel.setText("❌ Username/email exists"); return; }
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO PendingRegistrations (username,password,full_name,email,shop_name,shop_address,phone) VALUES (?,?,?,?,?,?,?)");
            ps.setString(1,uname); ps.setString(2,pass); ps.setString(3,name); ps.setString(4,email);
            ps.setString(5,shop); ps.setString(6,addr); ps.setString(7,phone);
            ps.executeUpdate();
            messageLabel.setForeground(Color.GREEN);
            messageLabel.setText("✅ Request submitted! Await approval.");
        }catch(SQLException ex){ messageLabel.setText("❌ Error: "+ex.getMessage()); }
    }
}