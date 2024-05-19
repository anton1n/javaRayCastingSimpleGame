public class PositionComponent implements Component {
    public double x, y;

    public PositionComponent(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean isAdjacent(PositionComponent other) {
        //System.out.println("Checking adjancenty ");
        return (Math.abs(this.x - other.x) <= 1 && this.y == other.y) ||
                (Math.abs(this.y - other.y) <= 1 && this.x == other.x);
    }
}