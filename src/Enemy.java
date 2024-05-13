import java.awt.*;

public class Enemy extends Entity {
    private double range;
    private double speed;
    private SpriteComponent sprite;

    public Enemy(double x, double y, int textureId) {
        range=5.0;
        speed=0.05;
        this.addComponent(new PositionComponent(x, y));
        this.addComponent(new SpriteComponent(x, y, false, textureId));
        addComponent(new DimensionsComponent(2, 2));
    }

    public Enemy(double x, double y, String spriteSheetPath, int totalFrames, int frameWidth, int frameHeight, int frameDuration) {
        range=5.0;
        speed=0.05;
        this.addComponent(new PositionComponent(x, y));
        this.addComponent(new SpriteComponent(x, y, spriteSheetPath, totalFrames, frameWidth, frameHeight, frameDuration));
        addComponent(new DimensionsComponent(2, 2));

    }

    public void setRange(double range) {
        this.range = range;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void update(Player player) {
        PositionComponent enemyPosition = this.getComponent(PositionComponent.class);
        PositionComponent playerPosition = player.getComponent(PositionComponent.class);
        updateMovement(playerPosition, enemyPosition);
        updateAnimation();
    }

    private void updateMovement(PositionComponent playerPosition, PositionComponent enemyPosition) {
        if (enemyPosition != null && playerPosition != null) {
            double dx = playerPosition.x - enemyPosition.x;
            double dy = playerPosition.y - enemyPosition.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance > 0 && distance < range) {
                double moveX = dx / distance * speed;
                double moveY = dy / distance * speed;
                enemyPosition.x += moveX;
                enemyPosition.y += moveY;

                SpriteComponent sprite = this.getComponent(SpriteComponent.class);
                if (sprite != null) {
                    sprite.x = enemyPosition.x;
                    sprite.y = enemyPosition.y;
                }
            }
        }
    }

    private void updateAnimation() {
        SpriteComponent sprite = this.getComponent(SpriteComponent.class);
        if (sprite != null) {
            sprite.update();
        }
    }

    public void render(Graphics g, Camera camera, double[] zBuffer) {
        SpriteComponent sprite = this.getComponent(SpriteComponent.class);
        if (sprite != null) {
            camera.renderSprite(g, sprite, zBuffer);
        }
    }


    public Rectangle getBounds() {
        PositionComponent pos = getComponent(PositionComponent.class);
        //SpriteComponent sprite = getComponent(SpriteComponent.class);
        DimensionsComponent dim = getComponent(DimensionsComponent.class);

        return new Rectangle((int) pos.x - dim.getWidth() / 2, (int) pos.y - dim.getHeight() / 2, dim.getWidth(), dim.getHeight());
    }
}
