public class EnemyFactory {
    static Enemy createEnemy(String type, double x, double y){
        return switch (type.toLowerCase()) {
            case "skelethon" -> new Skelethon(x, y);
            case "phantom" -> new Phantom(x, y);
            case "stalker" -> new Stalker(x, y);
            default -> {
                System.out.println("Enemy type not found!");
                yield null;
            }
        };
    }
}