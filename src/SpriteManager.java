//import java.awt.*;
//import java.util.*;
//import java.util.List;
//
//public class SpriteManager {
//    private List<SpriteComponent> sprites = new ArrayList<>();
//    private Camera camera;
//
//    public SpriteManager(Camera camera) {
//        this.camera = camera;
//    }
//
//    public void addSprite(SpriteComponent sprite) {
//        sprites.add(sprite);
//    }
//
//    public void sortSprites() {
//        PositionComponent cameraPos = camera.player.getComponent(PositionComponent.class);
//        sprites.sort((s1, s2) -> {
//            double dist1 = Math.pow(cameraPos.x - s1.x, 2) + Math.pow(cameraPos.y - s1.y, 2);
//            double dist2 = Math.pow(cameraPos.x - s2.x, 2) + Math.pow(cameraPos.y - s2.y, 2);
//            return Double.compare(dist2, dist1); // Sort in descending order
//        });
//    }
//
//    public void renderSprites(Graphics g) {
//        // Use camera to render each sprite sorted by distance
//        for (SpriteComponent sprite : sprites) {
//            camera.renderSprite(g, sprite);
//        }
//    }
//}
