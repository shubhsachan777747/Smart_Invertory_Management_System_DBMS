import javax.swing.*;
import java.awt.*;

public class GradientPanel extends JPanel {
    private Color color1 = new Color(25,25,25);
    private Color color2 = new Color(43,43,43);

    public GradientPanel() {
        setOpaque(false);
    }
    public GradientPanel(Color c1, Color c2) {
        this.color1 = c1; this.color2 = c2;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0,0,color1,0,getHeight(),color2);
        g2.setPaint(gp);
        g2.fillRect(0,0,getWidth(),getHeight());
    }
}