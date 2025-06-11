import java.awt.*;
import java.util.Random;

public class Obstacle {
    public int x;
    public int y;
    private int width = 32;
    private int blockCount;
    public boolean destroyed = false;
    private int hitCount = 0;

    public Obstacle(int xStart) {
        this.x = xStart;
        blockCount = 3 + new Random().nextInt(6);
        int totalHeight = blockCount * width;
        y = (GamePanel.HEIGHT / 2) - (totalHeight / 2);
    }


    public void update() {
        x -= 4;
    }

    public void draw(Graphics g) {
        for (int i = 0; i < blockCount; i++) {
            g.drawImage(Images.blockImage, x, y + i * width, width, width, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, blockCount * width);
    }

    public boolean isOffscreen() {
        return x + width < 0;
    }

    public void hit() {
        hitCount++;
        if (hitCount >= 1) destroyed = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
