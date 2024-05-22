import java.awt.*;
import java.awt.image.BufferedImage;

public class Item extends Entity {
    BufferedImage texture = null;
    Textures texManager = null;
    String itemName;

    int texId;

    public Item(double x, double y, int texId, Textures tex, String name) {
        addComponent(new PositionComponent(x, y));
        addComponent(new SpriteComponent(x, y, false, texId));
        addComponent(new DimensionsComponent(2, 2));
        texManager = tex;
        itemName = name;
        this.texId=texId;
    }

    public PositionComponent getPosition() {
        return getComponent(PositionComponent.class);
    }

    public SpriteComponent getSprite() {
        return getComponent(SpriteComponent.class);
    }

    public BufferedImage getImage(){
        return texManager.getTextureImage(this.getComponent(SpriteComponent.class).getTextureId());
    }

    public Rectangle getBounds() {
        PositionComponent pos = getComponent(PositionComponent.class);
        DimensionsComponent dim = getComponent(DimensionsComponent.class);

        return new Rectangle((int) pos.x - dim.getWidth() / 2, (int) pos.y - dim.getHeight() / 2, dim.getWidth(), dim.getHeight());
    }

    public String getItemName(){return itemName;}

    public void check(){
        itemName=" ";
    }

    public int getTexId(){return texId;}
}
