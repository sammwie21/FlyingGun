import java.awt.*;
import java.awt.image.BufferedImage;

public class Spike {
    int x, y;
    int width, height;
    BufferedImage image;

    public Spike(int x, int y, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public void update() {
        x -= 4; // move with rest of world
    }

    public void draw(Graphics g) {
        g.drawImage(image, x, y, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
