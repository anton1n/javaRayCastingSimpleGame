import java.util.EnumMap;

public class Stalker extends Enemy{
    private boolean jumpScareActive = false;
    private static EnumMap<State, Integer> createFrameCounts() {
        EnumMap<State, Integer> frameCounts = new EnumMap<>(State.class);
        frameCounts.put(State.idle, 1);
        //frameCounts.put(State.damaged, 1);
        //frameCounts.put(State.walking, 2);
        return frameCounts;
    }
    public Stalker(double x, double y) {
        super(x, y, 5.0, 0.05, System.getProperty("user.dir")+"/assets/textures/stalkerSprite.png", createFrameCounts(), 310, 315, 0, "stalker");
    }

    @Override
    public void update(Player player) {
        PositionComponent enemyPosition = this.getComponent(PositionComponent.class);
        PositionComponent playerPosition = player.getComponent(PositionComponent.class);
        double distance = Math.hypot(playerPosition.x - enemyPosition.x, playerPosition.y - enemyPosition.y);

        if(distance < 2.0 && !jumpScareActive) {
            jumpScareActive = true;
            triggerJumpScare();
        } else if (distance >= 2.0) {
            jumpScareActive = false;
        }

        updateAnimation();
    }

    public boolean isJumpScareActive() {
        return jumpScareActive;
    }

    private void triggerJumpScare() {
        System.out.println("Jumpscare triggered!");
    }

}
