import java.util.function.Consumer;

public class HealthComponent implements Component {
    private int health;
    private Consumer<HealthComponent> onDeath;

    public HealthComponent(int initialHealth, Consumer<HealthComponent> onDeath) {
        this.health = initialHealth;
        this.onDeath = onDeath;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void takeDamage(int damage) {
        System.out.println("Damage taken! Actual life: " + health);
        health -= damage;
        if (health <= 0) {
            health = 0;
            if (onDeath != null) {
                onDeath.accept(this);
            }
        }
    }
}
