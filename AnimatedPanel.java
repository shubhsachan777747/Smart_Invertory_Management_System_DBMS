import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class AnimatedPanel extends JPanel {
    private CardLayout cardLayout;
    private JPanel cards;
    private Timer timer;
    private float alpha;
    private Component oldComponent;
    private Component newComponent;
    private BufferedImage oldImage;
    private BufferedImage newImage;
    private static final int FPS = 30;
    private static final int DURATION = 600;

    public AnimatedPanel(JPanel initialPanel) {
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);
        if (initialPanel != null) {
            cards.add(initialPanel, "current");
        }
        add(cards, BorderLayout.CENTER);
    }

    public void setInitialPanel(JPanel panel) {
        cards.add(panel, "current");
        cardLayout.show(cards, "current");
    }

    public void switchTo(JPanel nextPanel) {
        if (cards.getComponentCount() == 0) {
            cards.add(nextPanel, "current");
            cardLayout.show(cards, "current");
            return;
        }
        oldComponent = cards.getComponent(0);
        newComponent = nextPanel;
        cards.add(newComponent, "next");
        cardLayout.show(cards, "next");
        oldImage = captureImage(oldComponent);
        newImage = captureImage(newComponent);
        cardLayout.show(cards, "current");
        alpha = 0.0f;
        timer = new Timer(1000 / FPS, e -> {
            alpha += 1.0f / (FPS * DURATION / 1000.0f);
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                timer.stop();
                cards.remove(oldComponent);
                cards.add(newComponent, "current");
                cardLayout.show(cards, "current");
                oldComponent = null;
                newComponent = null;
                oldImage = null;
                newImage = null;
            }
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (oldImage == null || newImage == null) return;
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setComposite(AlphaComposite.SrcOver.derive(1.0f - alpha));
        g2d.drawImage(oldImage, 0, 0, null);
        g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
        g2d.drawImage(newImage, 0, 0, null);
        g2d.dispose();
    }

    private BufferedImage captureImage(Component comp) {
        BufferedImage img = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        comp.printAll(g);
        g.dispose();
        return img;
    }
}