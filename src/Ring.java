import java.awt.*;

public class Ring {
    int x, y;
    int diameter = 60;
    int speed = 5;
    boolean passed = false;

    public Ring(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        x -= speed;
    }

    public void draw(Graphics g) {
        g.setColor(passed ? Color.BLUE : Color.ORANGE);
        g.drawOval(x, y, diameter, diameter);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, diameter, diameter);
    }
}
