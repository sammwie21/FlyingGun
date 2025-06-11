import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private SoundPlayer checkpointSound;
    private SoundPlayer endingSound;
    private ArrayList<Checkpoint> checkpoints = new ArrayList<>();
    private int score = 0;
    private int obstacleCounter = 0;
    private Player player;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private ArrayList<Powerup> powerups = new ArrayList<>();
    private SpikeManager spikeManager;

    public static final int WIDTH = 800, HEIGHT = 600;
    public static boolean gameOver = false;

    private Timer timer;
    private final int DELAY = 3;

    private Rectangle restartButton = new Rectangle(WIDTH / 2 - 75, HEIGHT / 2 + 50, 150, 50);

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        checkpointSound = new SoundPlayer("src\\checkpoint.wav");
        endingSound = new SoundPlayer("src\\ending.wav");

        Images.loadImages();

        player = new Player();
        spikeManager = new SpikeManager();

        timer = new Timer(DELAY, this);
        timer.start();

        spawnObstacle();
    }

    private void spawnObstacle() {
        Obstacle o = new Obstacle(WIDTH + 50);
        obstacles.add(o);
        obstacleCounter++;

        // Add checkpoint less frequently
        if (obstacleCounter % 8 == 0) {
            int attempts = 0;
            while (attempts < 10) {
                int radius = 40;
                int cx = o.getX() + 100 + new Random().nextInt(100); // vary x
                int cy = HEIGHT / 3 + new Random().nextInt(HEIGHT / 3); // vary y around middle

                Checkpoint cp = new Checkpoint(cx, cy, radius);

                boolean overlaps = false;
                for (Obstacle ob : obstacles) {
                    Rectangle obBounds = ob.getBounds();
                    if (obBounds.intersects(cp.getBounds())) {
                        overlaps = true;
                        break;
                    }
                }

                if (!overlaps) {
                    checkpoints.add(cp);
                    break;
                }
                attempts++;
            }
        }

        // Optional: spawn powerup
        if (Math.random() < 0.15) {
            powerups.add(new Powerup(o.getX() + 20, o.getY() - 50));
        }
    }




    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.draw(g);
        for (Bullet b : bullets) b.draw(g);
        for (Obstacle o : obstacles) o.draw(g);
        spikeManager.draw(g);

        for (Powerup p : powerups) {
            if (!p.collected) p.draw(g);
        }

        // Draw checkpoints
        for (Checkpoint cp : checkpoints) {
            cp.draw(g);
        }

        for (Checkpoint cp : checkpoints) {
            cp.draw(g);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Score: " + score, 10, 50);



        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Ammo: " + player.getAmmo(), 10, 25);

        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 170));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String msg = "GAME OVER";
            g.drawString(msg, WIDTH / 2 - g.getFontMetrics().stringWidth(msg) / 2, HEIGHT / 2);

            g.setColor(Color.GRAY);
            g.fillRect(restartButton.x, restartButton.y, restartButton.width, restartButton.height);

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String btnText = "RESTART";
            g.drawString(btnText, restartButton.x + (restartButton.width - g.getFontMetrics().stringWidth(btnText)) / 2,
                    restartButton.y + 32);
        }


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) updateGame();
        repaint();

    }

    private void updateGame() {
        player.update();

        bullets.removeIf(b -> {
            b.update();
            return b.x > WIDTH;
        });

        for (Obstacle o : new ArrayList<>(obstacles)) {
            o.update();
            if (o.isOffscreen() || o.destroyed) obstacles.remove(o);
            if (!gameOver && player.getBounds().intersects(o.getBounds())) {
                gameOver = true;
                endingSound.play();
            }
        }

        for (Powerup p : new ArrayList<>(powerups)) {
            p.update();
            if (p.collected || p.x + p.size < 0) powerups.remove(p);
            if (p.getBounds().intersects(player.getBounds())) {
                player.refillAmmo();
                p.collected = true;
            }
        }

        Iterator<Checkpoint> it = checkpoints.iterator();
        while (it.hasNext()) {
            Checkpoint c = it.next();
            c.update();

            if (!c.passed && c.intersects(player.getBounds())) {
                c.passed = true;
                score++;
                checkpointSound.play();// increase score
                it.remove();    // remove checkpoint so it disappears
            }

            if (c.isOffscreen()) {
                if (!c.passed) {
                    endingSound.play();
                    GamePanel.gameOver = true;  // player missed checkpoint — game over
                }
                it.remove();
            }
        }
        if (!gameOver && spikeManager.checkCollision(player.getBounds())) {
            gameOver = true;
            endingSound.play();
        }





// Spawn new checkpoints regularly
        if (checkpoints.isEmpty() || checkpoints.get(checkpoints.size() - 1).x < GamePanel.WIDTH - 300) {
            int radius = 30;
            int newX = GamePanel.WIDTH + 200;
            int newY = 200 + new Random().nextInt(GamePanel.HEIGHT - 400);

            checkpoints.add(new Checkpoint(newX, newY, radius));
        }



        spikeManager.update();
        Rectangle playerBounds = player.getBounds();
        for (Checkpoint c : checkpoints) {
            if (!c.passed && c.intersects(playerBounds)) {
                c.passed = true;
                score++;
            }
        }

        if (spikeManager.checkCollision(player.getBounds())) gameOver = true;

        bullets.removeIf(bullet -> {
            for (Obstacle o : obstacles) {
                if (bullet.getBounds().intersects(o.getBounds())) {
                    o.hit();
                    return true;
                }
            }
            return false;
        });

        if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).getX() < WIDTH - 200)
            spawnObstacle();
    }

    private void restartGame() {
        player.reset();
        bullets.clear();
        obstacles.clear();
        powerups.clear();
        spikeManager.reset();
        gameOver = false;
        checkpoints.clear();
        score = 0;
        spawnObstacle();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) player.setThrust(true);
            else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                Bullet b = player.shoot();
                if (b != null) bullets.add(b);
            }
        } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            restartGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) player.setThrust(false);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (gameOver && e.getID() == MouseEvent.MOUSE_CLICKED && restartButton.contains(e.getPoint()))
            restartGame();
        super.processMouseEvent(e);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        addMouseListener(new MouseAdapter() {});
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }
}
