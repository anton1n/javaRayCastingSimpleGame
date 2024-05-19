import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items;

    public Inventory() {
        items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean hasItem(String itemName){
        for(Item item : items){
            if(itemName.equals(item.getItemName()))
                return true;
        }
        return false;
    }

    public void renderInventory(Graphics g, int startX, int startY) {
        for (Item item : items) {
            BufferedImage img = item.getImage();
            if (img != null) {
                g.drawImage(img, startX, startY, null);
                startX += img.getWidth() + 5;
            }
        }
    }
}
