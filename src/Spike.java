import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

public class Spike {
    public int x, y;
    public int width = 32;
    public int height = 32;
    private boolean isBottom; // true if this spike is on the bottom (needs flipping)
    private Image spikeImage;

    public Spike(int x, int y, boolean isBottom, Image spikeImage) {
        this.x = x;
        this.y = y;
        this.isBottom = isBottom;
        this.spikeImage = spikeImage;
    }

    public void draw(Graphics g) {
        if (isBottom) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.drawImage(spikeImage, x, y + height, width, -height, null);
        } else {
            g.drawImage(spikeImage, x, y, width, height, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
