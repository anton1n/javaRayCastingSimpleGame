import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MenuPanel extends JPanel implements KeyListener {
    private String[] options = {"Save", "Load", "Exit", "Inventory"};
    private int currentSelection = 0;
    private BufferedImage menuImage;

    public MenuPanel() {
        setFocusable(true);
        try {
            menuImage = ImageIO.read(new File("protagonist01.png"));
        } catch (IOException e) {
            System.out.println("Unable to load image: " + e.getMessage());
            menuImage = null;
        }
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        currentSelection = (currentSelection - 1 + options.length) % options.length;
                        repaint();
                        break;
                    case KeyEvent.VK_DOWN:
                        currentSelection = (currentSelection + 1) % options.length;
                        repaint();
                        break;
                    case KeyEvent.VK_ENTER:
                        selectOption();
                        break;
                }
            }
        });
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void selectOption() {
        switch (currentSelection) {
            case 0:
                System.out.println("Save selected");
                break;
            case 1:
                System.out.println("Load selected");
                break;
            case 2:
                System.exit(0);
                break;
            case 3:
                System.out.println("Inventory selected");
                break;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        int width = getWidth();
        int height = getHeight();

        int fontSize = getWidth() / 30;
        g.setFont(new Font("Serif", Font.PLAIN, fontSize));

        for (int i = 0; i < options.length; i++) {
            if (i == currentSelection) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.WHITE);
            }
            g.drawString(options[i], 50, i * fontSize + fontSize);

            if (menuImage != null) {
                int imageWidth = menuImage.getWidth();
                int imageHeight = menuImage.getHeight();


                double scaleWidth = (double) (width / 2) / imageWidth;
                double scaleHeight = (double) height / imageHeight;
                double scale = Math.min(scaleWidth, scaleHeight);

                int scaledWidth = (int) (imageWidth * scale);
                int scaledHeight = (int) (imageHeight * scale);


                int x = width / 2 + (width / 2 - scaledWidth) / 2;
                int y = (height - scaledHeight) / 2;

                g.drawImage(menuImage, x, y, scaledWidth, scaledHeight, this);
            }
        }
    }
}
