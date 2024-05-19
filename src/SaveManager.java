import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    private Connection conn;

    public SaveManager(String dbPath) {
        try {
            String url = "jdbc:sqlite:" + dbPath;
            conn = DriverManager.getConnection(url);
            initializeDatabase();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void initializeDatabase() throws SQLException {
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
                    "enemyId INTEGER," +
                    "health INTEGER," +
                    "positionX DOUBLE," +
                    "positionY DOUBLE," +
                    "FOREIGN KEY(saveId) REFERENCES saves(id));";
            stmt.execute(sql);
            sql = "CREATE TABLE IF NOT EXISTS items (" +
                    "saveId INTEGER," +
                    "itemId INTEGER," +
                    "collected BOOLEAN," +
                    "FOREIGN KEY(saveId) REFERENCES saves(id));";
            stmt.execute(sql);
        }
    }

    public void saveGame(String name, double playerX, double playerY, int health, String map, int level, List<Enemy> enemies, List<Item> items) {
        try {
            conn.setAutoCommit(false);
            String sql = "INSERT INTO saves (name, playerX, playerY, health, map, level) VALUES (?, ?, ?, ?, ?, ?);";
            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, name);
                pstmt.setDouble(2, playerX);
                pstmt.setDouble(3, playerY);
                pstmt.setInt(4, health);
                pstmt.setString(5, map);
                pstmt.setInt(6, level);
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long saveId = generatedKeys.getLong(1);
                        saveEnemies(saveId, enemies);
                        saveItems(saveId, items);
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
            System.out.println(e.getMessage());
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void saveEnemies(long saveId, List<Enemy> enemies) throws SQLException {
        String sql = "INSERT INTO enemies (saveId, enemyId, health, positionX, positionY) VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Enemy enemy : enemies) {
                pstmt.setLong(1, saveId);
//                pstmt.setInt(2, enemy.getId());
//                pstmt.setInt(3, enemy.getHealth());
//                pstmt.setDouble(4, enemy.getX());
//                pstmt.setDouble(5, enemy.getY());
//                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    private void saveItems(long saveId, List<Item> items) throws SQLException {
        String sql = "INSERT INTO items (saveId, itemId, collected) VALUES (?, ?, ?);";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Item item : items) {
                pstmt.setLong(1, saveId);
                //pstmt.setInt(2, item.getId());
                //pstmt.setBoolean(3, item.isCollected());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }

    public List<String> loadSaveNames() throws SQLException {
        List<String> names = new ArrayList<>();
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
}
