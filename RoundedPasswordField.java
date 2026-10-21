import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedPasswordField extends JPasswordField {
    private String placeholder;
    private Color bg = new Color(60, 63, 65);
    private Color fg = Color.WHITE;
    private Color placeholderColor = Color.GRAY;
    private Color borderColor = new Color(74, 134, 232);
    private Shape shape;

    public RoundedPasswordField(int columns) {
        super(columns);
        setOpaque(false);
        setBackground(bg);
        setForeground(fg);
        setCaretColor(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
    }

    public void setPlaceholder(String placeholder) { this.placeholder = placeholder; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        super.paintComponent(g);
        if (getPassword().length == 0 && placeholder != null) {
            g2.setColor(placeholderColor);
            FontMetrics fm = g2.getFontMetrics();
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(placeholder, 15, y);
        }
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        g2.dispose();
    }

    @Override
    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds()))
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
        return shape.contains(x, y);
    }
}