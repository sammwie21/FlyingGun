import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    // Screen size constants
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private Timer timer;
    private final int DELAY = 15; // ~60 FPS

    // Player variables
    private int playerX = 100;
    private int playerY = HEIGHT / 2;
    private int playerWidth = 60;
    private int playerHeight = 40;
    private double velocity = 0;
    private final double gravity = 0.3;
    private final double upwardForce = -0.6;

    private BufferedImage ufoImage;

    // Bullets
    private class Bullet {
        int x, y;
        int width = 10, height = 5;
        int speed = 10;

        public Bullet(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void update() {
            x += speed;
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private final int MAX_BULLETS = 15;
    private boolean canShoot = true;
    private long lastShootTime = 0;
    private final int SHOOT_COOLDOWN = 500; // milliseconds

    private BufferedImage bulletImage;

    // Obstacles made of blocks stacked vertically
    private class Obstacle {
        int x;
        int blockCount;
        static final int BLOCK_SIZE = 32;
        int y; // from bottom up
        int hitCount = 0;
        boolean destroyed = false;

        Obstacle(int x, int blockCount) {
            this.x = x;
            this.blockCount = blockCount;
            this.y = HEIGHT - blockCount * BLOCK_SIZE;
        }

        void update() {
            x -= scrollSpeed;
        }

        void draw(Graphics g) {
            for (int i = 0; i < blockCount; i++) {
                g.drawImage(blockImage, x, y + i * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE, null);
            }
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, BLOCK_SIZE, blockCount * BLOCK_SIZE);
        }
    }

    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private int scrollSpeed = 4;
    private BufferedImage blockImage;

    // Powerups for ammo refill
    private class Powerup {
        int x, y, size = 30;
        boolean collected = false;

        Powerup(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void update() {
            x -= scrollSpeed;
        }

        void draw(Graphics g) {
            g.setColor(Color.GREEN);
            g.fillOval(x, y, size, size);
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, size, size);
        }
    }

    private ArrayList<Powerup> powerups = new ArrayList<>();

    // Spikes as small blocks stacked vertically on top and bottom edges
    private class Spike {
        int x, y;
        static final int SIZE = 32;

        Spike(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void update() {
            x -= scrollSpeed;
        }

        void draw(Graphics g) {
            if (blockImage != null) {
                g.drawImage(blockImage, x, y, SIZE, SIZE, null);
            } else {
                g.setColor(Color.RED);
                g.fillRect(x, y, SIZE, SIZE);
            }
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, SIZE, SIZE);
        }
    }

    private ArrayList<Spike> topSpikes = new ArrayList<>();
    private ArrayList<Spike> bottomSpikes = new ArrayList<>();

    // Game state
    public static boolean gameOver = false;
    private int ammo = MAX_BULLETS;

    // Controls
    private boolean spacePressed = false;

    // Restart button
    private Rectangle restartButton = new Rectangle(WIDTH / 2 - 75, HEIGHT / 2 + 50, 150, 50);

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        try {
            ufoImage = ImageIO.read(new File("src\\ufo.png"));
            blockImage = ImageIO.read(new File("src\\wall.png"));  // Use your block or spike image here
            bulletImage = ImageIO.read(new File("src\\bullet.png"));
            BufferedImage spikeImage = ImageIO.read(new File("src\\spike-down.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize spikes covering entire top and bottom edges (a bit extra to the right)
        int spikesNeeded = WIDTH / Spike.SIZE + 3; // +3 to cover offscreen area on right
        for (int i = 0; i < spikesNeeded; i++) {
            topSpikes.add(new Spike(i * Spike.SIZE, 0));              // top edge, y=0
            bottomSpikes.add(new Spike(i * Spike.SIZE, HEIGHT - Spike.SIZE)); // bottom edge
        }

        timer = new Timer(DELAY, this);
        timer.start();

        spawnObstacle();
    }

    private void spawnObstacle() {
        int blocks = 3 + new Random().nextInt(4); // 3 to 6
        int x = WIDTH + 50;
        obstacles.add(new Obstacle(x, blocks));

        // Random chance to spawn powerup roughly aligned with obstacle gap (just example)
        if (new Random().nextDouble() < 0.3) {
            int puY = HEIGHT - blocks * Obstacle.BLOCK_SIZE - 50;
            powerups.add(new Powerup(x + 20, puY));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw player (UFO)
        if (ufoImage != null) {
            g.drawImage(ufoImage, playerX, playerY, playerWidth, playerHeight, null);
        } else {
            g.setColor(Color.CYAN);
            g.fillOval(playerX, playerY, playerWidth, playerHeight);
        }

        // Draw bullets
        for (Bullet b : bullets) {
            if (bulletImage != null) {
                g.drawImage(bulletImage, b.x, b.y, 32, 16, null);
            } else {
                g.setColor(Color.YELLOW);
                g.fillRect(b.x, b.y, b.width, b.height);
            }
        }

        // Draw obstacles (stacked blocks)
        for (Obstacle o : obstacles) {
            o.draw(g);
        }

        // Draw powerups
        for (Powerup p : powerups) {
            if (!p.collected) p.draw(g);
        }

        // Draw spikes on top and bottom edges
        for (Spike s : topSpikes) {
            s.draw(g);
        }
        for (Spike s : bottomSpikes) {
            s.draw(g);
        }

        // Ammo display
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Ammo: " + ammo, 10, 25);

        // Game over screen
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 170));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String msg = "GAME OVER";
            int msgWidth = g.getFontMetrics().stringWidth(msg);
            g.drawString(msg, WIDTH / 2 - msgWidth / 2, HEIGHT / 2);

            // Draw restart button
            g.setColor(Color.GRAY);
            g.fillRect(restartButton.x, restartButton.y, restartButton.width, restartButton.height);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String btnText = "RESTART";
            int btnWidth = g.getFontMetrics().stringWidth(btnText);
            g.drawString(btnText, restartButton.x + (restartButton.width - btnWidth) / 2,
                    restartButton.y + 32);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            updateGame();
        }
        repaint();
    }

    private void updateGame() {
        // Update player velocity and position
        if (spacePressed) {
            velocity += upwardForce;
        } else {
            velocity += gravity;
        }
        // Limit velocity so it’s smooth
        velocity = Math.max(Math.min(velocity, 7), -7);

        playerY += (int) velocity;

        // Check screen bounds (top/bottom)
        if (playerY < 0 || playerY + playerHeight > HEIGHT) {
            gameOver = true;
        }

        // Update bullets
        Iterator<Bullet> bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();
            b.update();
            if (b.x > WIDTH) {
                bulletIt.remove();
            }
        }

        // Cooldown shoot timer
        if (!canShoot && System.currentTimeMillis() - lastShootTime > SHOOT_COOLDOWN) {
            canShoot = true;
        }

        // Update obstacles
        Iterator<Obstacle> obsIt = obstacles.iterator();
        while (obsIt.hasNext()) {
            Obstacle o = obsIt.next();
            o.update();

            // Remove offscreen
            if (o.x + Obstacle.BLOCK_SIZE < 0 || o.destroyed) {
                obsIt.remove();
            }

            // Check collision with player - GAME OVER
            if (o.getBounds().intersects(new Rectangle(playerX, playerY, playerWidth, playerHeight))) {
                gameOver = true;
            }
        }

        // Update powerups
        Iterator<Powerup> puIt = powerups.iterator();
        while (puIt.hasNext()) {
            Powerup p = puIt.next();
            p.update();
            if (p.collected || p.x + p.size < 0) {
                puIt.remove();
                continue;
            }
            if (p.getBounds().intersects(new Rectangle(playerX, playerY, playerWidth, playerHeight))) {
                ammo = MAX_BULLETS;
                p.collected = true;
            }
        }

        // Update spikes
        updateSpikes();

        // Bullet hits obstacle
        bulletIt = bullets.iterator();
        while (bulletIt.hasNext()) {
            Bullet b = bulletIt.next();
            boolean bulletRemoved = false;
            for (Obstacle o : obstacles) {
                if (b.getBounds().intersects(o.getBounds())) {
                    o.hitCount++;
                    bulletRemoved = true;
                    if (o.hitCount >= 3) {
                        o.destroyed = true;
                    }
                    break;
                }
            }
            if (bulletRemoved) bulletIt.remove();
        }

        // Spawn new obstacles randomly
        if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).x < WIDTH - 200) {
            spawnObstacle();
        }
    }

    private void updateSpikes() {
        // Move spikes left
        for (Spike s : topSpikes) {
            s.update();
        }
        for (Spike s : bottomSpikes) {
            s.update();
        }

        // Remove spikes that have moved off left side and add new ones to right side to maintain coverage
        int spikesNeeded = WIDTH / Spike.SIZE + 3;

        // Top spikes
        Iterator<Spike> topIt = topSpikes.iterator();
        while (topIt.hasNext()) {
            Spike s = topIt.next();
            if (s.x + Spike.SIZE < 0) {
                topIt.remove();
            }
        }
        // Add new spikes on right if needed
        while (topSpikes.size() < spikesNeeded) {
            int maxX = topSpikes.isEmpty() ? 0 : topSpikes.get(topSpikes.size() - 1).x;
            topSpikes.add(new Spike(maxX + Spike.SIZE, 0));
        }

        // Bottom spikes
        Iterator<Spike> botIt = bottomSpikes.iterator();
        while (botIt.hasNext()) {
            Spike s = botIt.next();
            if (s.x + Spike.SIZE < 0) {
                botIt.remove();
            }
        }
        // Add new spikes on right if needed
        while (bottomSpikes.size() < spikesNeeded) {
            int maxX = bottomSpikes.isEmpty() ? 0 : bottomSpikes.get(bottomSpikes.size() - 1).x;
            bottomSpikes.add(new Spike(maxX + Spike.SIZE, HEIGHT - Spike.SIZE));
        }

        // Check collision of spikes with player — GAME OVER
        Rectangle playerRect = new Rectangle(playerX, playerY, playerWidth, playerHeight);
        for (Spike s : topSpikes) {
            if (s.getBounds().intersects(playerRect)) {
                gameOver = true;
                break;
            }
        }
        for (Spike s : bottomSpikes) {
            if (s.getBounds().intersects(playerRect)) {
                gameOver = true;
                break;
            }
        }
    }

    // KeyListener methods
    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                spacePressed = true;
            } else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                shoot();
            }
        } else {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                restartGame();
            }
        }
    }

    private void shoot() {
        if (canShoot && ammo > 0) {
            bullets.add(new Bullet(playerX + playerWidth, playerY + playerHeight / 2));
            ammo--;
            canShoot = false;
            lastShootTime = System.currentTimeMillis();
        }
    }

    private void restartGame() {
        playerY = HEIGHT / 2;
        velocity = 0;
        bullets.clear();
        obstacles.clear();
        powerups.clear();
        ammo = MAX_BULLETS;
        gameOver = false;
        spacePressed = false;
        spawnObstacle();

        // Reset spikes
        topSpikes.clear();
        bottomSpikes.clear();
        int spikesNeeded = WIDTH / Spike.SIZE + 3;
        for (int i = 0; i < spikesNeeded; i++) {
            topSpikes.add(new Spike(i * Spike.SIZE, 0));
            bottomSpikes.add(new Spike(i * Spike.SIZE, HEIGHT - Spike.SIZE));
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            spacePressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    // Mouse listener for restart button click
    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (gameOver && e.getID() == MouseEvent.MOUSE_CLICKED) {
            Point p = e.getPoint();
            if (restartButton.contains(p)) {
                restartGame();
            }
        }
        super.processMouseEvent(e);
    }

    // Enable mouse events to detect clicks
    @Override
    public void addNotify() {
        super.addNotify();
        addMouseListener(new MouseAdapter() {});
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }
}
