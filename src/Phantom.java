import java.util.EnumMap;

public class Phantom extends Enemy {
    //private String spritePath;

    private static EnumMap<State, Integer> createFrameCounts() {
        EnumMap<State, Integer> frameCounts = new EnumMap<>(State.class);
        frameCounts.put(State.walking, 3);
        frameCounts.put(State.idle, 1);
        frameCounts.put(State.damaged, 1);
        return frameCounts;
    }

    public Phantom(double x, double y) {
        super(x, y, 5.0, 0.05, "phantomSprites.png", createFrameCounts(), 105, 155, 100, "phantom");
        name = "Chaser";
    }

    @Override
    public void update(Player player) {
        PositionComponent enemyPosition = this.getComponent(PositionComponent.class);
        PositionComponent playerPosition = player.getComponent(PositionComponent.class);

        double distance = Math.sqrt(Math.pow(playerPosition.x - enemyPosition.x, 2) + Math.pow(playerPosition.y - enemyPosition.y, 2));


        if(CollisionManager.checkCollision(this.getBounds(),player.getBounds())){
            state = State.idle;
        }else if(distance < range) {
            state = State.walking;
            updateMovement(playerPosition, enemyPosition);
        } else{
            state = State.idle;
        }

        updateAnimation();
    }
}