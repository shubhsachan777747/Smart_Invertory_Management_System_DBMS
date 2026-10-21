import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class LoginPanel extends JPanel {
    private JFrame parentFrame;
    private AnimatedPanel container;
    private RoundedTextField userField;
    private RoundedPasswordField passField;
    private JLabel messageLabel;
    private JRadioButton shopOwnerRadio, adminRadio;
    private ButtonGroup roleGroup;
    private Timer particleTimer;
    private ArrayList<Particle> particles = new ArrayList<>();
    private Random rand = new Random();

    public LoginPanel(JFrame frame, AnimatedPanel cont) {
        this.parentFrame = frame;
        this.container = cont;
        setBackground(new Color(30,30,30));
        setLayout(new GridBagLayout());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;

        for (int i = 0; i < 40; i++) {
            particles.add(new Particle(rand.nextInt(screenWidth), rand.nextInt(screenHeight)));
        }
        particleTimer = new Timer(30, e -> {
            for (Particle p : particles) {
                p.y -= p.speed;
                if (p.y < -20) {
                    p.y = screenHeight + 20;
                    p.x = rand.nextInt(screenWidth);
                }
            }
            repaint();
        });
        particleTimer.start();

        JPanel loginBox = new JPanel(new GridBagLayout());
        loginBox.setBackground(new Color(50,50,50, 200));
        loginBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(74,134,232), 1),
                BorderFactory.createEmptyBorder(20,30,20,30)));
        loginBox.setPreferredSize(new Dimension(440, 520));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10,0,10,0);

        JLabel title = new JLabel("🔐 INVENTORY PRO", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI Emoji", Font.BOLD, 36));
        title.setForeground(new Color(74,134,232));
        loginBox.add(title, gbc);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        rolePanel.setOpaque(false);
        adminRadio = new JRadioButton("👑 Admin");
        shopOwnerRadio = new JRadioButton("🏪 USER");
        adminRadio.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        shopOwnerRadio.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        adminRadio.setForeground(Color.WHITE); shopOwnerRadio.setForeground(Color.WHITE);
        adminRadio.setOpaque(false); shopOwnerRadio.setOpaque(false);
        roleGroup = new ButtonGroup();
        roleGroup.add(adminRadio); roleGroup.add(shopOwnerRadio);
        shopOwnerRadio.setSelected(true);
        rolePanel.add(adminRadio); rolePanel.add(shopOwnerRadio);
        loginBox.add(rolePanel, gbc);

        userField = new RoundedTextField(20);
        userField.setPlaceholder("👤 Username");
        loginBox.add(userField, gbc);

        passField = new RoundedPasswordField(20);
        passField.setPlaceholder("🔒 Password");
        loginBox.add(passField, gbc);

        RoundedButton loginBtn = new RoundedButton("🚀 LOGIN");
        loginBtn.setPreferredSize(new Dimension(220, 45));
        loginBox.add(loginBtn, gbc);

        RoundedButton registerBtn = new RoundedButton("📝 Register New Shop");
        registerBtn.setPreferredSize(new Dimension(220, 45));
        registerBtn.setNormalColor(new Color(120,120,120));
        loginBox.add(registerBtn, gbc);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        messageLabel.setForeground(Color.RED);
        loginBox.add(messageLabel, gbc);

        add(loginBox);

        loginBtn.addActionListener(e -> handleLogin());
        registerBtn.addActionListener(e -> {
            particleTimer.stop();
            container.switchTo(new RegisterRequestPanel(parentFrame, container));
        });
        userField.addActionListener(e -> passField.requestFocus());
        passField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword()).trim();
        if(user.isEmpty()||pass.isEmpty()){ messageLabel.setText("❌ Fill all fields"); return; }
        String role = adminRadio.isSelected() ? "admin" : "shop_owner";
        try(Connection conn = DatabaseUtil.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT user_id, full_name, shop_name FROM Users WHERE username=? AND password=? AND role=? AND is_active=true")){
            ps.setString(1,user); ps.setString(2,pass); ps.setString(3,role);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Session.userId = rs.getInt("user_id");
                Session.username = user;
                Session.fullName = rs.getString("full_name");
                Session.role = role;
                Session.shopName = rs.getString("shop_name");
                try(PreparedStatement logPs = conn.prepareStatement(
                        "INSERT INTO ActivityLog (user_id, activity_type, description) VALUES (?,'LOGIN',?)")){
                    logPs.setInt(1,Session.userId);
                    logPs.setString(2,Session.fullName+" logged in");
                    logPs.executeUpdate();
                }
                particleTimer.stop();
                if(role.equals("admin")) container.switchTo(new AdminDashboardPanel(parentFrame, container));
                else container.switchTo(new ShopOwnerDashboardPanel(parentFrame, container));
            } else {
                messageLabel.setText("❌ Invalid credentials or inactive");
            }
        }catch(SQLException ex){ messageLabel.setText("❌ DB error: "+ex.getMessage()); }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0,0,new Color(20,20,25),0,getHeight(),new Color(40,40,45));
        g2.setPaint(gp);
        g2.fillRect(0,0,getWidth(),getHeight());
        g2.setColor(new Color(74,134,232,100));
        for (Particle p : particles) {
            g2.fillOval(p.x, p.y, p.size, p.size);
        }
    }

    class Particle {
        int x, y, size, speed;
        Particle(int x, int y) {
            this.x = x; this.y = y;
            size = rand.nextInt(6)+3;
            speed = rand.nextInt(3)+1;
        }
    }
}