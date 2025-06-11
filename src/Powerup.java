import java.awt.*;

public class Powerup {
    int x, y, size = 30;
    boolean collected = false;

    public Powerup(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        x -= 4;
    }

    public void draw(Graphics g) {
        if (Images.powerUpImage != null) {
            g.drawImage(Images.powerUpImage, x, y, size, size, null);
        } else {
            g.setColor(Color.GREEN);
            g.fillOval(x, y, size, size); // fallback if image not found
        }
    }


    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }


}
