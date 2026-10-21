import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    private Color normalColor = new Color(74, 134, 232);
    private Color hoverColor = new Color(100, 160, 255);
    private Color currentColor = normalColor;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { currentColor = hoverColor; repaint(); }
            public void mouseExited(MouseEvent e)  { currentColor = normalColor; repaint(); }
        });
    }

    public void setNormalColor(Color c) { normalColor = c; currentColor = c; }
    public void setHoverColor(Color c) { hoverColor = c; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
        super.paintComponent(g);
        g2.dispose();
    }
}