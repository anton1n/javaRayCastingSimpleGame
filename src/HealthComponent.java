public class HealthComponent implements Component {
    private int health;

    public HealthComponent(int health) {
        this.health = health;
    }

    public int getHealth(){return health;}
    public void setHealth(int hp){health=hp;}

}