import java.util.EnumMap;

public class Skelethon extends Enemy{

//    private String spritePath;
//    private EnumMap<State, Integer> frameCounts = new EnumMap<State, Integer>(State.class) {{
//        put(State.attacking, 6);
//        put(State.idle, 5);
//        put(State.damaged, 1);
//        put(State.walking, 4);
//    }};

    private static EnumMap<State, Integer> createFrameCounts() {
        EnumMap<State, Integer> frameCounts = new EnumMap<>(State.class);
        frameCounts.put(State.attacking, 6);
        frameCounts.put(State.idle, 5);
        frameCounts.put(State.damaged, 1);
        frameCounts.put(State.walking, 4);
        return frameCounts;
    }
    public Skelethon(double x, double y) {
        super(x, y, 5.0, 0.05, "skeletonSprites1.png", createFrameCounts(), 105, 155, 100, "skelethon");
    }

    @Override
    public void update(Player player) {
        PositionComponent enemyPosition = this.getComponent(PositionComponent.class);
        PositionComponent playerPosition = player.getComponent(PositionComponent.class);
        double distance = updateMovement(playerPosition, enemyPosition);

        if (distance < 1.0 && CollisionManager.checkCollision(this.getBounds(),player.getBounds())) {
            state = State.attacking;
        } else if (distance < range) {
            state = State.walking;
        } else {
            state = State.idle;
        }

        updateAnimation();
    }
}
