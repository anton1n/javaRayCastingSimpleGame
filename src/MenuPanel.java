import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class MenuPanel extends JPanel implements KeyListener {
    private String[] options = {"Save", "Load", "Exit", "Inventory"};
    private int currentSelection = 0;
    private BufferedImage menuImage;

    private MenuActionListener actionListener;

    private ArrayList<String> saveFiles;
    private boolean inLoadMenu = false;

    public MenuPanel(MenuActionListener listener) {
        this.actionListener = listener;
        setFocusable(true);
        try {
            menuImage = ImageIO.read(new File(System.getProperty("user.dir")+"/assets/textures/protagonist01.png"));
        } catch (IOException e) {
            System.out.println("Unable to load image: " + e.getMessage());
            menuImage = null;
        }
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (inLoadMenu && !saveFiles.isEmpty()) {
                            currentSelection = (currentSelection - 1 + saveFiles.size()) % saveFiles.size();
                        } else {
                            currentSelection = (currentSelection - 1 + options.length) % options.length;
                        }
                        repaint();
                        break;
                    case KeyEvent.VK_DOWN:
                        if (inLoadMenu && !saveFiles.isEmpty()) {
                            currentSelection = (currentSelection + 1) % saveFiles.size();
                        } else {
                            currentSelection = (currentSelection + 1) % options.length;
                        }
                        repaint();
                        break;
                    case KeyEvent.VK_ENTER:
                        if (inLoadMenu) {
                            actionListener.onLoad(saveFiles.get(currentSelection));
                        } else {
                            selectOption();
                        }
                        break;
                    case KeyEvent.VK_ESCAPE:
                        if (inLoadMenu) {
                            inLoadMenu = false;
                            currentSelection = 0;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_D:
                        if (inLoadMenu && !saveFiles.isEmpty()) {
                            deleteSave();
                        }
                        break;
                }
            }
        });

    }

    private void deleteSave() {
        try {
            String saveToDelete = saveFiles.get(currentSelection);
            actionListener.onDeleteSave(saveToDelete);
            saveFiles.remove(currentSelection);
            currentSelection = Math.max(0, currentSelection - 1);
            repaint();
        } catch (SQLException e) {
            System.out.println("Error deleting save: " + e.getMessage());
        }
    }

    private void loadSaves() {
        try {
            saveFiles = actionListener.onLoadSaves();
            inLoadMenu = true;
            currentSelection = 0;
            repaint();
        } catch (SQLException e) {
            System.out.println("Error loading saves: " + e.getMessage());
            saveFiles = new ArrayList<>();
        }
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
                actionListener.onSave();
                break;
            case 1:
                //actionListener.onLoad();
                loadSaves();

                break;
            case 2:
                actionListener.onExit();
                break;
            case 3:
                actionListener.onInventory();
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

        if (!inLoadMenu) {
            for (int i = 0; i < options.length; i++) {
                if (i == currentSelection) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.drawString(options[i], 50, i * fontSize + fontSize);
            }
        } else {
            for (int i = 0; i < saveFiles.size(); i++) {
                if (i == currentSelection) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.drawString(saveFiles.get(i), 50, (i+1) * fontSize + fontSize);
                g.setColor(Color.WHITE);
                g.drawString("Press d key to delete save", width/2, 50 );

            }
        }

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
