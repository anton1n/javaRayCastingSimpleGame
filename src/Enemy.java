import java.awt.*;
import java.util.Map;

enum State{
    attacking,
    idle,
    damaged,
    walking
};

public class Enemy extends Entity {
    private double range;
    private double speed;
    private SpriteComponent sprite;
    private State state;

    public Enemy(double x, double y, int textureId) {
        range=5.0;
        speed=0.05;
        this.addComponent(new PositionComponent(x, y));
        this.addComponent(new SpriteComponent(x, y, false, textureId));
        addComponent(new DimensionsComponent(2, 2));
        addComponent(new HealthComponent(10, this::onDeath));
        state = State.idle;
    }

    public Enemy(double x, double y, String spriteSheetPath, int totalFrames, int frameWidth, int frameHeight, int frameDuration) {
        range=5.0;
        speed=0.05;
        this.addComponent(new PositionComponent(x, y));
        this.addComponent(new SpriteComponent(x, y, spriteSheetPath, totalFrames, frameWidth, frameHeight, frameDuration));
        addComponent(new DimensionsComponent(2, 2));
        addComponent(new HealthComponent(3, this::onDeath));
        state = State.idle;

    }

    public Enemy(double x, double y, String spriteSheetPath, Map<State, Integer> frameCounts, int frameWidth, int frameHeight, int frameDuration) {
        range = 5.0;
        speed = 0.05;
        this.addComponent(new PositionComponent(x, y));

        this.sprite = new SpriteComponent(x, y, spriteSheetPath, frameCounts, frameWidth, frameHeight, frameDuration);
        this.addComponent(sprite);

        addComponent(new DimensionsComponent(2, 2));
        addComponent(new HealthComponent(3, this::onDeath));
        state = State.idle;
        System.out.println("Enemy created ");
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
        double distance = updateMovement(playerPosition, enemyPosition);
        //System.out.println("Current State: " + state + ", Distance: " + distance);

        //getComponent(SpriteComponent.class).setLongFrameDuration(false);
        if (distance < 1.0 && CollisionManager.checkCollision(this.getBounds(),player.getBounds())) {
            state = State.attacking;
            //System.out.println("enemy is attacking");
        } else if (distance < range) {
            state = State.walking;
            //System.out.println("enemy is walking");
        } else {
            state = State.idle;
            //getComponent(SpriteComponent.class).setLongFrameDuration(true);
            //System.out.println("enemy is idle");
        }

        //System.out.println("Enemy state updated to: " + state);
        updateAnimation();
    }

    private double updateMovement(PositionComponent playerPosition, PositionComponent enemyPosition) {
        double dx = playerPosition.x - enemyPosition.x;
        double dy = playerPosition.y - enemyPosition.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > 0 && distance < range && state != State.attacking) {
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

        return distance;
    }

    private void updateAnimation() {
        SpriteComponent sprite = this.getComponent(SpriteComponent.class);
        if (sprite != null) {
            sprite.update(state);
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

    private void onDeath(HealthComponent healthComponent) {
        System.out.println("Enemy died");
    }
}
