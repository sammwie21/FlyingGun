import java.awt.*;

public class Bullet {
    public int x, y;
    public int width = 10, height = 5;
    public int speed = 10;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        x += speed;
    }

    public void draw(Graphics g) {
        if (Images.bulletImage != null) {
            g.drawImage(Images.bulletImage, x, y, 32, 16, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
