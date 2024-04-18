import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Game extends JFrame implements KeyListener {
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    private boolean running = true;
    private Camera canvas;
    //private List<Enemy> enemies = new ArrayList<>();

    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);
    private TitleScreen titleScreen = new TitleScreen();

    private String currentCard = "Title";

    private MenuPanel menuPanel;

    private static Game gameInstance = null;

    public Game() throws IOException {
        setTitle("Raycasting Engine");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Setting up the card layout
        cardPanel.setLayout(cardLayout);
        add(cardPanel);

        // Add title screen
        titleScreen.addKeyListener(this);
        titleScreen.setFocusable(true);
        cardPanel.add(titleScreen, "Title");

        // Initialize and add game canvas
        Textures tex = new Textures();
        tex.loadTiles("tilemap01.png");
        tex.loadTile("enemy01.png");
        int[][] map = tex.readMap("map.txt");

        canvas = new Camera(map, tex);
        canvas.addKeyListener(this);
        cardPanel.add(canvas, "Game");

        menuPanel = new MenuPanel();
        menuPanel.addKeyListener(this);
        cardPanel.add(menuPanel, "Menu");

        // Initially show the title screen
        cardLayout.show(cardPanel, "Title");
        titleScreen.requestFocus();
        setVisible(true);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (currentCard) {
            case "Title":
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    cardLayout.show(cardPanel, "Game");
                    currentCard = "Game";
                    canvas.requestFocusInWindow();
                    canvas.startGameLoop();
                }
                break;
            case "Game":
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cardLayout.show(cardPanel, "Menu");
                    currentCard = "Menu";
                    menuPanel.requestFocusInWindow();
                } else {
                    canvas.keyPressed(e.getKeyCode());
                }
                break;
            case "Menu":
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    cardLayout.show(cardPanel, "Game");
                    currentCard = "Game";
                    canvas.requestFocusInWindow();
                } else {
                    menuPanel.keyPressed(e);  // Delegate key handling to the menu panel
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if ("Game".equals(currentCard)) {
            canvas.keyReleased(e.getKeyCode());
        }
    }

    @Override
    public void keyTyped(KeyEvent e){

    }

    public static synchronized Game getInstance() throws IOException
    {
        if (gameInstance == null)
            gameInstance = new Game();

        return gameInstance;
    }
}
