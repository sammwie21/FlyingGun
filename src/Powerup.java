import java.awt.*;

public class Powerup {
    int x, y;
    int size = 20;
    int speed = 4;

    public Powerup(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        x -= speed;
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillOval(x, y, size, size);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }
}
