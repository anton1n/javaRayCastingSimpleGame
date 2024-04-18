import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Entity {
    private UUID id;
    private Map<Class<? extends Component>, Component> components;

    public Entity() {
        this.id = UUID.randomUUID();
        this.components = new HashMap<>();
    }

    public UUID getId() {
        return id;
    }

    public <T extends Component> T getComponent(Class<T> componentClass) {
        return componentClass.cast(components.get(componentClass));
    }

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        components.remove(componentClass);
    }
}
