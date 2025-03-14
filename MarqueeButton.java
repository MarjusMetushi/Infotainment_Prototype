import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MarqueeButton extends JButton {
    private String text;
    private String displayedText;
    private int xPos;
    private Timer timer;
    private int textWidth;

    public MarqueeButton(String initialText) {
        super(initialText);
        this.text = initialText;
        this.setPreferredSize(new Dimension(160, 30));
        this.setMaximumSize(new Dimension(160, 30));
        this.setMinimumSize(new Dimension(160, 30));
        this.displayedText = initialText;
        this.xPos = getWidth();
        this.textWidth = getFontMetrics(getFont()).stringWidth(text);
        startMarquee();
    }

    private void startMarquee() {
        timer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Move text leftward
                xPos--;
                // Reset the position if the text is completely off-screen
                if (xPos < -textWidth) {
                    xPos = getWidth();
                }
                repaint();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getForeground());
        // Draw the text at the current position (marquee effect)
        g.drawString(displayedText, xPos, ((getHeight() / 2)-2) + getFont().getSize() / 2);
    }

    // Method to dynamically update the text and reset the scroll position
    public void updateText(String newText) {
        this.text = newText;
        this.displayedText = newText;
        this.textWidth = getFontMetrics(getFont()).stringWidth(text);
        this.xPos = getWidth(); // Reset position when the text changes
        repaint();
    }
}
