import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Obstacle {
    public int x, y;
    public int blockCount; // how many blocks tall (3 to 6)
    public static final int BLOCK_SIZE = 32; // adjust to your image height
    public boolean destroyed = false;
    public int hitCount = 0;

    private static BufferedImage blockImage;

    static {
        try {
            blockImage = ImageIO.read(new File("src\\wall.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Obstacle(int x, int blockCount) {
        this.x = x;
        this.blockCount = blockCount;
        this.y = 0; // top-aligned (adjust later for bottom-aligned if needed)
    }

    public void update(int scrollSpeed) {
        x -= scrollSpeed;
    }

    public void draw(Graphics g) {
        for (int i = 0; i < blockCount; i++) {
            g.drawImage(blockImage, x, y + i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, BLOCK_SIZE, blockCount * BLOCK_SIZE);
    }

    public void takeHit() {
        hitCount++;
        if (hitCount >= 3) {
            destroyed = true;
        }
    }
}
