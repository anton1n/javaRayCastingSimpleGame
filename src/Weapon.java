import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Weapon {
    private List<BufferedImage> frames;
    private int currentFrame = 0;
    private long lastFrameTime;
    private long frameDuration;
    private boolean isAnimating = false;
    public int width;
    public int height;

    public Weapon(String path, int totalFrames, long frameDuration, int width, int height) {
        this.frameDuration = frameDuration;
        this.height = height;
        this.width = width;
        frames = new ArrayList<>();
        loadFrames(totalFrames, path);
    }

    private void loadFrames(int totalFrames, String path) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(path));

            int numTilesAcross = spriteSheet.getWidth() / width;
            int numTilesDown = spriteSheet.getHeight() / height;
            for (int i = 0; i < numTilesDown; i++) {
                for (int j = 0; j < numTilesAcross; j++) {
                    frames.add(spriteSheet.getSubimage(j * width, i * height, width, height));

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update() {
        if (isAnimating) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFrameTime > frameDuration) {
                currentFrame++;
                if (currentFrame >= frames.size()) {
                    currentFrame = 0;
                    isAnimating = false;
                }
                lastFrameTime = currentTime;
            }
        }
    }

    public void startAnimation() {
        if (!isAnimating) {
            isAnimating = true;
            currentFrame = 0;
            lastFrameTime = System.currentTimeMillis();
        }
    }

    public void render(Graphics g, int x, int y) {
        if (!frames.isEmpty()) {
            BufferedImage currentImage = frames.get(currentFrame);
            g.drawImage(currentImage, x, y, null);
        }
    }

    public boolean isAnimating(){return isAnimating;}
}
