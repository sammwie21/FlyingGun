import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public class SpikeManager {
    private ArrayList<Spike> topSpikes;
    private ArrayList<Spike> bottomSpikes;

    private final int spikeWidth = 32;
    private final int spikeHeight = 32;
    private final int spikeSpeed = 4;

    public SpikeManager() {
        topSpikes = new ArrayList<>();
        bottomSpikes = new ArrayList<>();

        int numSpikes = (GamePanel.WIDTH / spikeWidth) + 2; // +2 to cover screen fully

        for (int i = 0; i < numSpikes; i++) {
            int xPos = i * spikeWidth;
            int topY = 0;
            int bottomY = GamePanel.HEIGHT - spikeHeight;

            // Top spikes: normal orientation (facing down)
            topSpikes.add(new Spike(xPos, topY, false, Images.spikeImage));

            // Bottom spikes: flipped vertically (facing up)
            bottomSpikes.add(new Spike(xPos, bottomY, true, Images.spikeImage));
        }
    }

    public void update() {
        for (Spike s : topSpikes) {
            s.x -= spikeSpeed;
            if (s.x + spikeWidth < 0) {
                // Wrap around to the right edge
                s.x += topSpikes.size() * spikeWidth;
            }
        }
        for (Spike s : bottomSpikes) {
            s.x -= spikeSpeed;
            if (s.x + spikeWidth < 0) {
                s.x += bottomSpikes.size() * spikeWidth;
            }
        }
    }

    public void draw(Graphics g) {
        for (Spike s : topSpikes) s.draw(g);
        for (Spike s : bottomSpikes) s.draw(g);
    }

    public boolean checkCollision(Rectangle playerBounds) {
        for (Spike s : topSpikes) {
            if (playerBounds.intersects(s.getBounds())) return true;
        }
        for (Spike s : bottomSpikes) {
            if (playerBounds.intersects(s.getBounds())) return true;
        }
        return false;
    }

    public void reset() {
        int numSpikes = (GamePanel.WIDTH / spikeWidth) + 2;
        topSpikes.clear();
        bottomSpikes.clear();
        for (int i = 0; i < numSpikes; i++) {
            int xPos = i * spikeWidth;
            int topY = 0;
            int bottomY = GamePanel.HEIGHT - spikeHeight;

            topSpikes.add(new Spike(xPos, topY, false, Images.spikeImage));
            bottomSpikes.add(new Spike(xPos, bottomY, true, Images.spikeImage));
        }
    }
}
