import java.awt.*;

public class Player {
    private int x = 100;
    private int y = GamePanel.HEIGHT / 2;
    private int width = 60;
    private int height = 40;
    private double velocity = 0;
    private final double gravity = 0.25;
    private final double thrust = -0.3;
    private boolean isThrusting = false;

    private final int MAX_AMMO = 15;
    private int ammo = MAX_AMMO;
    private long lastShootTime = 0;
    private final int SHOOT_COOLDOWN = 500;
    private boolean canShoot = true;

    public void update() {
        if (isThrusting) velocity += thrust;
        else velocity += gravity;

        velocity = Math.max(Math.min(velocity, 7), -7);
        y += (int) velocity;

        if (y < 0 || y + height > GamePanel.HEIGHT) {
            GamePanel.gameOver = true;
        }

        // Cooldown logic
        if (!canShoot && System.currentTimeMillis() - lastShootTime > SHOOT_COOLDOWN) {
            canShoot = true;
        }
    }

    public void draw(Graphics g) {
        if (Images.ufoImage != null) {
            g.drawImage(Images.ufoImage, x, y, width, height, null);
        } else {
            g.setColor(Color.CYAN);
            g.fillOval(x, y, width, height);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getAmmo() {
        return ammo;
    }

    public void refillAmmo() {
        ammo = MAX_AMMO;
    }

    public void setThrust(boolean thrusting) {
        isThrusting = thrusting;
    }

    public Bullet shoot() {
        if (canShoot && ammo > 0) {
            canShoot = false;
            lastShootTime = System.currentTimeMillis();
            ammo--;
            return new Bullet(x + width, y + height / 2);
        }
        return null;
    }

    public void reset() {
        y = GamePanel.HEIGHT / 2;
        velocity = 0;
        ammo = MAX_AMMO;
        isThrusting = false;
        canShoot = true;
    }
}
