import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Player {
    int x, y;
    int width = 30, height = 30;
    double velocity = 0;
    final double gravity = 1;
    final double upwardForce = -2;
    final double maxFallSpeed = 8;
    private BufferedImage ufoImage;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = 60;  // Adjust to fit your image
        this.height = 40;

        try {
            ufoImage = ImageIO.read(new File("src\\ufo.png")); // adjust path if needed
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        velocity += gravity;

        // Clamp velocity to prevent it from getting too fast
        if (velocity > maxFallSpeed) velocity = maxFallSpeed;
        if (velocity < -maxFallSpeed) velocity = -maxFallSpeed;

        y += velocity;

        if (y + height > 720 || y < 0) {
            // Player hit top or bottom → trigger game over
            GamePanel.gameOver = true;
        }

    }

    public void glideUp() {
        velocity += upwardForce;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics g) {
        if (ufoImage != null) {
            g.drawImage(ufoImage, x, y, width, height, null);
        } else {
            g.setColor(Color.CYAN);
            g.fillOval(x, y, width, height); // fallback
        }
    }

}
