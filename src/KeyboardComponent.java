import java.util.HashSet;
import java.util.Set;

public class KeyboardComponent implements Component {
    private Set<Integer> activeKeys;

    public KeyboardComponent() {
        this.activeKeys = new HashSet<>();
    }

    public void pressKey(int keyCode) {
        activeKeys.add(keyCode);
    }

    public void releaseKey(int keyCode) {
        activeKeys.remove(keyCode);
    }

    public boolean isKeyPressed(int keyCode) {
        return activeKeys.contains(keyCode);
    }
}
