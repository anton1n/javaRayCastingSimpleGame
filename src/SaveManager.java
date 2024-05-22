import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    private Connection conn;

    public SaveManager(String dbPath) {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + dbPath;
            conn = DriverManager.getConnection(url);
            initializeDatabase();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (GameException e) {
            throw new RuntimeException(e);
        }
    }
    private void initializeDatabase() throws  GameException {
        try (Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS saves (" +
                    "id INTEGER PRIMARY KEY," +
                    "name TEXT," +
                    "playerX DOUBLE," +
                    "playerY DOUBLE," +
                    "health INTEGER," +
                    "map TEXT," +
                    "level INTEGER);";
            stmt.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS enemies (" +
                    "saveId INTEGER," +
                    "name TEXT," +
                    "health INTEGER," +
                    "positionX DOUBLE," +
                    "positionY DOUBLE," +
                    "FOREIGN KEY(saveId) REFERENCES saves(id));";
            stmt.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS inventory (" +
                    "saveId INTEGER," +
                    "itemName TEXT," +
                    "texId INTEGER," +
                    "FOREIGN KEY(saveId) REFERENCES saves(id));";
            stmt.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS items (" +
                    "saveId INTEGER," +
                    "itemName TEXT," +
                    "texId INTEGER," +
                    "positionX DOUBLE," +
                    "positionY DOUBLE," +
                    "FOREIGN KEY(saveId) REFERENCES saves(id));";
            stmt.execute(sql);
        }catch (SQLException e) {
            throw new GameException(GameException.ErrorCode.INITIALIZATION_ERROR, "Failed to initialize database", e);
        }
    }

    public void saveGame(String name, double playerX, double playerY, int health, int level, List<Enemy> enemies, List<Item> items, Inventory inventory) throws GameException {
        try {
            conn.setAutoCommit(false);
            String sql = "INSERT INTO saves (name, playerX, playerY, health,  level) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, playerX);
                pstmt.setDouble(3, playerY);
                pstmt.setInt(4, health);
                //pstmt.setString(5, map);
                pstmt.setInt(5, level);
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long saveId = generatedKeys.getLong(1);
                        saveEnemies(saveId, enemies);
                        saveInventory(saveId, inventory);
                        saveIntems(saveId, items);
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            throw new GameException(GameException.ErrorCode.SAVE_GAME_ERROR, "Failed to save game", e);        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void saveEnemies(long saveId, List<Enemy> enemies) throws SQLException {
        String sql = "INSERT INTO enemies (saveId, name, health, positionX, positionY) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Enemy enemy : enemies) {
                pstmt.setLong(1, saveId);
                //pstmt.setInt(2, enemy.getId());
                pstmt.setString(2, enemy.name);
                pstmt.setInt(3, enemy.getComponent(HealthComponent.class).getHealth());
                pstmt.setDouble(4, enemy.getComponent(PositionComponent.class).x);
                pstmt.setDouble(5, enemy.getComponent(PositionComponent.class).y);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void saveInventory(long saveId, Inventory inventory) throws SQLException {
        String sql = "INSERT INTO inventory (saveId, itemName, texId) VALUES (?, ?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Item item : inventory.getItems()) {
                pstmt.setLong(1, saveId);
                pstmt.setString(2, item.getItemName());
                pstmt.setInt(3, item.getTexId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void saveIntems(long saveId, List<Item> items) throws SQLException {
        String sql = "INSERT INTO items (saveId, itemName, texId, positionX, positionY) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Item item : items) {
                pstmt.setLong(1, saveId);
                pstmt.setString(2, item.getItemName());
                pstmt.setInt(3, item.getTexId());
                pstmt.setDouble(4, item.getComponent(PositionComponent.class).x);
                pstmt.setDouble(5, item.getComponent(PositionComponent.class).y);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public ArrayList<String> loadSaveNames() throws SQLException {
        ArrayList<String> names = new ArrayList<>();
        String sql = "SELECT name FROM saves;";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                names.add(rs.getString("name"));
            }
        }
        return names;
    }

    public void deleteSave(String name) throws SQLException {
        String sql = "DELETE FROM saves WHERE name = ?;";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
        }
    }

    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void printDatabase() {
        String sql = "SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';";

        try (
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String tableName = rs.getString("name");
                printTableData(tableName);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printTableData(String tableName) {
        System.out.println("Table: " + tableName);
        String sql = "SELECT * FROM " + tableName;

        try (
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int columnCount = rs.getMetaData().getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rs.getMetaData().getColumnName(i) + "\t");
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
            System.out.println();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public GameState loadGame(String saveName, Textures tex) throws SQLException {
        String sql = "SELECT * FROM saves WHERE name = ?";
        GameState gameState = new GameState();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, saveName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                gameState.playerX = rs.getDouble("playerX");
                gameState.playerY = rs.getDouble("playerY");
                gameState.health = rs.getInt("health");
                gameState.level = rs.getInt("level");
                gameState.enemies = loadEnemies(rs.getInt("id"));
                gameState.items = loadItems(rs.getInt("id"), tex);
                gameState.inventory = loadInventory(rs.getInt("id"), tex);
            }
        }
        return gameState;
    }

    private List<Enemy> loadEnemies(int saveId) throws SQLException {
        List<Enemy> enemies = new ArrayList<>();
        String sql = "SELECT * FROM enemies WHERE saveId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, saveId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                int health = rs.getInt("health");
                double x = rs.getDouble("positionX");
                double y = rs.getDouble("positionY");
                Enemy enemy = EnemyFactory.createEnemy(name, x, y);
                enemy.setHealth(health);
                enemies.add(enemy);
            }
        }
        return enemies;
    }

    private List<Item> loadItems(int saveId, Textures tex) throws SQLException {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE saveId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, saveId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
               items.add(new Item(
                        rs.getDouble("positionX"),
                        rs.getDouble("positionY"),
                        rs.getInt("texId"),
                        tex,
                        rs.getString("itemName")
                        ));
            }
        }
        return items;
    }

    private Inventory loadInventory(int saveId, Textures tex) throws SQLException {
        Inventory inventory = new Inventory();
        String sql = "SELECT * FROM inventory WHERE saveId = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, saveId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                inventory.addItem(new Item(
                        0,
                        0,
                        rs.getInt("texId"),
                        tex,
                        rs.getString("itemName")
                ));
            }
        }
        return inventory;
    }

}