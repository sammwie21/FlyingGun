import java.awt.*;

public class Checkpoint {
    int x, y, radius;
    boolean passed = false;

    public Checkpoint(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public void draw(Graphics g) {
        if (Images.portalImage != null) {
            g.drawImage(Images.portalImage, x, y, 50, 50, null);
        }
    }

    public boolean intersects(Rectangle playerBounds) {
        // Check distance to center
        int cx = x;
        int cy = y;
        int closestX = Math.max(playerBounds.x, Math.min(cx, playerBounds.x + playerBounds.width));
        int closestY = Math.max(playerBounds.y, Math.min(cy, playerBounds.y + playerBounds.height));

        int dx = cx - closestX;
        int dy = cy - closestY;

        return dx * dx + dy * dy <= radius * radius;
    }

    public Rectangle getBounds() {
        return new Rectangle(x - radius, y - radius, radius * 2, radius * 2);
    }

    public void update() {
        x -= 4;
    }

    public boolean isOffscreen() {
        return x + radius < 0;
    }

}
