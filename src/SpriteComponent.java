public class SpriteComponent implements Component {
    public double x, y;
    public boolean animated;
    public int textureId;

    public SpriteComponent(double x, double y, boolean animated, int textureId) {
        this.x = x;
        this.y = y;
        this.animated = animated;
        this.textureId = textureId;
    }
}
