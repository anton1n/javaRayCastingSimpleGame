import java.awt.*;

public class Door extends Entity {
    private boolean isOpen;
    private String requiredItemKey;
    int tileId;
    private String name;

    public Door(double x, double y, String requiredItemKey, int[][] map, String name) {
        this.isOpen = false;
        this.requiredItemKey = requiredItemKey;
        addComponent(new PositionComponent(x, y));
        //addComponent(new SpriteComponent(x, y, false, 8));
        int tileId=8;
        addComponent(new DimensionsComponent(2, 2));
        map[(int)x][(int)y] = tileId;
        this.name = name;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open, int[][]map) {
        this.isOpen = open;
        //getComponent(SpriteComponent.class).setTextureId(open ? 9 : 8);
        if(isOpen){
            map[(int)getComponent(PositionComponent.class).x][(int)getComponent(PositionComponent.class).y]=0;
            tileId = 9;
            addComponent(new SpriteComponent(getComponent(PositionComponent.class).x, getComponent(PositionComponent.class).y, false, 9));
        }
    }

    public String getRequiredItemKey() {
        return requiredItemKey;
    }

    public void interact(Inventory inventory, int[][] map) {
        if (!isOpen && inventory.hasItem(requiredItemKey)) {
            setOpen(true, map);
            System.out.println("Door opened!");
        } else if (isOpen){
            System.out.println("Door is already opened! ");
        } else{
            System.out.println("The door is locked. Required item: " + requiredItemKey);
        }
    }

    public Rectangle getBounds() {
        PositionComponent pos = getComponent(PositionComponent.class);
        DimensionsComponent dim = getComponent(DimensionsComponent.class);

        return new Rectangle((int) pos.x - dim.getWidth() / 2, (int) pos.y - dim.getHeight() / 2, dim.getWidth(), dim.getHeight());
    }

    public SpriteComponent getSprite() {
        return getComponent(SpriteComponent.class);
    }
}
