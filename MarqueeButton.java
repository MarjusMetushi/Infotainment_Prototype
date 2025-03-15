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
        this.setPreferredSize(new Dimension(160, 30)); // Set size
        this.setMaximumSize(new Dimension(160, 30));
        this.setMinimumSize(new Dimension(160, 30));
        this.displayedText = initialText; // Set initial text
        this.xPos = getWidth(); // Set initial position
        this.textWidth = getFontMetrics(getFont()).stringWidth(text); // Calculate the width of the text
        startMarquee(); // Start the marquee effect
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

    // Method to dynamically update the text
    public void updateText(String newText) {
        this.text = newText;
        this.displayedText = newText;
        this.textWidth = getFontMetrics(getFont()).stringWidth(text);
        repaint();
    }
}
