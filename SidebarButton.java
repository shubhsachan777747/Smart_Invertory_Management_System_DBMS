import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarButton extends JButton {
    private Color defaultBg = new Color(50,50,50);
    private Color hoverBg = new Color(74,134,232);
    private Color currentBg = defaultBg;

    public SidebarButton(String text, String icon) {
        super(icon + "  " + text);
        setHorizontalAlignment(SwingConstants.LEFT);
        setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        setForeground(Color.WHITE);
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { currentBg = hoverBg; repaint(); }
            public void mouseExited(MouseEvent e)  { currentBg = defaultBg; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(currentBg);
        g2.fillRoundRect(0,5,getWidth(),getHeight()-10,20,20);
        super.paintComponent(g);
        g2.dispose();
    }
}