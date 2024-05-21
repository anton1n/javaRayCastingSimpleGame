import java.sql.SQLException;
import java.util.ArrayList;

public interface MenuActionListener {
    void onSave();
    void onLoad(String name);
    void onExit();
    void onInventory();

    void onExitMenu();
    public ArrayList<String> onLoadSaves() throws SQLException;
    public void onDeleteSave(String saveName) throws SQLException;
}
