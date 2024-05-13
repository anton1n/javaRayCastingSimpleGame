import java.awt.Rectangle;
import java.util.List;

public class CollisionManager {
    private List<SpriteComponent> sprites;
    private List<Enemy> enemies;

    public CollisionManager(List<SpriteComponent> sprites, List<Enemy> enemies) {
        this.sprites = sprites;
        this.enemies = enemies;
    }

    public boolean checkCollision(Rectangle box1, Rectangle box2) {
        return box1.intersects(box2);
    }

    public boolean collidesWithAnySprite(Rectangle entityBox) {
        for (SpriteComponent sprite : sprites) {
            if (checkCollision(entityBox, sprite.getBounds())) {
                return true;
            }
        }
        return false;
    }

    public boolean collidesWithAnyEnemy(Rectangle entityBox) {
        for (Enemy enemy : enemies) {
            if (checkCollision(entityBox, enemy.getBounds())) {
                return true;
            }
        }
        return false;
    }

}