import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SpriteComponent implements Component {
    public double x, y;
    public boolean animated;
    public int textureId;
    public int width;
    public int height;

    private BufferedImage[] frames;
    private int currentFrame;
    private int totalFrames;
    private long frameDuration;
    private long lastFrameTime;

    public SpriteComponent(double x, double y, boolean animated, int textureId) {
        this.x = x;
        this.y = y;
        this.animated = animated;
        this.textureId = textureId;
    }

    public SpriteComponent(double x, double y, String spriteSheetPath, int totalFrames, int frameWidth, int frameHeight, int frameDuration) {
        this.x = x;
        this.y = y;
        this.animated = true;
        //this.textureId = textureId;
        this.width = frameWidth;
        this.height = frameHeight;
        this.totalFrames = totalFrames;
        this.frameDuration = frameDuration;
        this.frames = new BufferedImage[totalFrames];
        loadFrames(spriteSheetPath, totalFrames, frameWidth, frameHeight);
        this.currentFrame = 0;
        this.lastFrameTime = System.currentTimeMillis();
    }

    private void loadFrames(String path, int totalFrames, int width, int height) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(path));
            for (int i = 0; i < totalFrames; i++) {
                frames[i] = spriteSheet.getSubimage(i * width, 0, width, height);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (!animated) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDuration) {
            currentFrame = (currentFrame + 1) % totalFrames;
            lastFrameTime = currentTime;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle((int) x - width / 2, (int) y - height / 2, width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BufferedImage getCurrentFrame() {
        return frames[currentFrame];
    }
}