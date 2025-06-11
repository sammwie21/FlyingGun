import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Images {
    public static BufferedImage ufoImage;
    public static BufferedImage bulletImage;
    public static BufferedImage blockImage;
    public static BufferedImage spikeImage;
    public static BufferedImage powerUpImage;
    public static BufferedImage portalImage;


    public static void loadImages() {
        try {
            ufoImage = ImageIO.read(new File("src\\ufo.png"));
            bulletImage = ImageIO.read(new File("src\\bullet.png"));
            blockImage = ImageIO.read(new File("src\\wall.png"));
            spikeImage = ImageIO.read(new File("src\\spike-down.png"));
            powerUpImage = ImageIO.read(new File("src\\powerup.png"));
            portalImage = ImageIO.read(new File("src\\portal.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
