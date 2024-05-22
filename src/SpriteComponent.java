import javax.imageio.ImageIO;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class SpriteComponent implements Component {
    public double x, y;
    public boolean animated;
    public int textureId;
    public int width;
    public int height;

    private Map<State, BufferedImage[]> animations = new EnumMap<>(State.class);
    private BufferedImage[] frames;
    private int currentFrame;
    private int totalFrames;
    public long frameDuration;
    private long lastFrameTime;
    private long longFrameDuration;
    private Map<State, Integer> frameCounts = new HashMap<>();
    private State currentState;

    public SpriteComponent(double x, double y, String spriteSheetPath, Map<State, Integer> framesPerState, int frameWidth, int frameHeight, int frameDuration) {
        this.x = x;
        this.y = y;
        this.width = frameWidth;
        this.height = frameHeight;
        this.frameDuration = frameDuration;
        this.animated = true;

        loadAnimations(spriteSheetPath, framesPerState, frameWidth, frameHeight);
    }


    public SpriteComponent(double x, double y, boolean animated, int textureId) {
        this.x = x;
        this.y = y;
        this.animated = animated;
        this.textureId = textureId;
    }

    public int getTextureId(){return textureId;}
    public void setTextureId(int t){textureId=t;}

    public SpriteComponent(double x, double y, String spriteSheetPath, int totalFrames, int frameWidth, int frameHeight, int frameDuration) {
        this.x = x;
        this.y = y;
        this.animated = true;
        //this.textureId = textureId;
        this.width = frameWidth;
        this.height = frameHeight;
        this.totalFrames = totalFrames;
        this.frameDuration = frameDuration;
        longFrameDuration = frameDuration + 100;
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

    private void loadAnimations(String path, Map<State, Integer> framesPerState, int width, int height) {
        try {
            BufferedImage spriteSheet = ImageIO.read(new File(path));
            int spriteSheetWidth = spriteSheet.getWidth();
            int spriteSheetHeight = spriteSheet.getHeight();

            int numTilesAcross = spriteSheetWidth / width;
            int numTilesDown = spriteSheetHeight / height;
            //System.out.println(spriteSheetHeight + " " + height);

           // System.out.println("Tiles Across: " + numTilesAcross + ", Tiles Down: " + numTilesDown);

            int stateRow = 0;
            int col = 0;
            for (State state : State.values()) {
                if(!framesPerState.containsKey(state)){
                    continue;
                }
                int frameCount = framesPerState.get(state);
                System.out.println("Loading " + frameCount + " frames for state: " + state);

                if (frameCount > numTilesAcross * numTilesDown) {
                    throw new IllegalArgumentException("Not enough space on sprite sheet for " + frameCount + " frames of state " + state);
                }

                BufferedImage[] frames = new BufferedImage[frameCount];
                for (int i = 0; i < frameCount; i++) {
                    if (col >= numTilesAcross) {
                        col = 0;
                        stateRow++;
                    }

                    if (stateRow >= numTilesDown) {
                        //System.out.println(col * width +" "+ stateRow * height);
                        throw new IllegalArgumentException("Exceeded sprite sheet dimensions while loading state " + state);
                    }

                    frames[i] = spriteSheet.getSubimage(col * width, stateRow * height, width, height);
                    //System.out.println("Loaded frame " + i + " for state " + state + " at row " + stateRow + ", col " + col);
                    col++;
                }

                animations.put(state, frames);
                frameCounts.put(state, frameCount);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load sprite sheet: " + e.getMessage());
        }
    }


//    public void update(State currentState) {
//        if (!animated || !animations.containsKey(currentState)) return;
//
//        BufferedImage[] frames = animations.get(currentState);
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastFrameTime > frameDuration) {
//            currentFrame = (currentFrame + 1) % frames.length;
//            lastFrameTime = currentTime;
//        }
//    }

    public void update(State newState) {
        if (!animated || !animations.containsKey(newState)) return;
        if (currentState != newState) {
            currentState = newState;
            currentFrame = 0;
        }

        if(frameDuration==0)
            return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDuration) {
            currentFrame = (currentFrame + 1) % animations.get(currentState).length;
            lastFrameTime = currentTime;
        }
    }

    public BufferedImage getCurrentFrame() {
        if(!animated){
            return frames[currentFrame];
        }
        else if (currentState == null || animations.get(currentState) == null || animations.get(currentState).length == 0) {
            System.err.println("No animation frames available for state: " + currentState);
            return null;
        }
        return animations.get(currentState)[currentFrame];
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

    public void setLongFrameDuration(boolean set){
        if(set){
            if(frameDuration < longFrameDuration){
                long x = frameDuration;
                frameDuration = longFrameDuration;
                longFrameDuration = x;
            }
        }
    }


}