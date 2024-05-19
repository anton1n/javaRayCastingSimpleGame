import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InventoryPanel extends JPanel {
    private Inventory inventory;
    private MenuActionListener actionListener;

    public InventoryPanel(Inventory inventory, MenuActionListener listener) {
        this.actionListener = listener;
        this.inventory = inventory;
        setPreferredSize(new Dimension(400, 300)); // Set size as needed
        setBackground(Color.DARK_GRAY);
        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    actionListener.onExitMenu();
                }

            }
        });
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            requestFocusInWindow();
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        int x = 10;
        int y = 30;
        int yOffset = 50;

        for (Item item : inventory.getItems()) {
            Textures tex = new Textures();
            BufferedImage img = item.getImage();

            if (img != null) {
                g.drawImage(img, x, y, null);
                y += yOffset;
                System.out.println(y);
            }
        }
    }
}
