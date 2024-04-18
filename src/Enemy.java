import java.awt.*;

public class Enemy extends Entity {
    private double range;
    private double speed;

    public Enemy(double x, double y, int textureId) {
        range=5.0;
        speed=0.05;
        this.addComponent(new PositionComponent(x, y));
        this.addComponent(new SpriteComponent(x, y, false, textureId));
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

        if (enemyPosition != null && playerPosition != null) {
            double dx = playerPosition.x - enemyPosition.x;
            double dy = playerPosition.y - enemyPosition.y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            System.out.println(distance);

            if (distance > 0 && distance < range) {
                dx /= distance;
                dy /= distance;
                enemyPosition.x += dx * speed;
                enemyPosition.y += dy * speed;


                SpriteComponent sprite = this.getComponent(SpriteComponent.class);
                if (sprite != null) {
                    sprite.x = enemyPosition.x;
                    sprite.y = enemyPosition.y;
                }
            }
        }
    }
    public void render(Graphics g, Camera camera) {
            SpriteComponent sprite = this.getComponent(SpriteComponent.class);
            if (sprite != null) {
                camera.renderSprite(g, sprite);
            }
        }
}
