import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SplashScreen {
    private JWindow window;
    private Timer timer;
    private float alpha = 0f;
    private int progress = 0;

    public SplashScreen(Runnable onComplete) {
        window = new JWindow();
        window.setSize(400, 300);
        window.setLocationRelativeTo(null);
        window.setBackground(new Color(0,0,0,0));

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30,30,30));
                g2.fillRoundRect(10,10,getWidth()-20,getHeight()-20,30,30);
                g2.setFont(new Font("Segoe UI Emoji", Font.BOLD, 32));
                g2.setColor(new Color(74,134,232));
                String logo = "INVENTORY PRO";
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(logo))/2;
                g2.drawString(logo, x, getHeight()/2-20);
                // progress bar
                g2.setColor(Color.DARK_GRAY);
                g2.fillRoundRect(80, getHeight()-80, getWidth()-160, 18, 10, 10);
                g2.setColor(new Color(74,134,232));
                int progressWidth = (int)((getWidth()-160)*progress/100.0);
                g2.fillRoundRect(80, getHeight()-80, progressWidth, 18, 10, 10);
            }
        };
        panel.setOpaque(false);
        window.add(panel);
        window.setOpacity(0f);
        window.setVisible(true);

        Timer fadeIn = new Timer(20, e -> {
            alpha += 0.05f;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                ((Timer)e.getSource()).stop();
                startLoading(onComplete);
            }
            window.setOpacity(alpha);
        });
        fadeIn.start();
    }

    private void startLoading(Runnable onComplete) {
        timer = new Timer(30, e -> {
            progress += 2;
            if (progress >= 100) {
                progress = 100;
                timer.stop();
                window.repaint();
                Timer fadeOut = new Timer(20, ev -> {
                    float a = window.getOpacity() - 0.05f;
                    if (a <= 0.0f) {
                        a = 0.0f;
                        ((Timer)ev.getSource()).stop();
                        window.dispose();
                        onComplete.run();
                    }
                    window.setOpacity(a);
                });
                fadeOut.start();
            }
            window.repaint();
        });
        timer.start();
    }
}