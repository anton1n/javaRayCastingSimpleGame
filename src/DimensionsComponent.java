public class DimensionsComponent implements Component {
    private int width;
    private int height;

    public DimensionsComponent(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
