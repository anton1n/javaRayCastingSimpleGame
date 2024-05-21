import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Game extends JFrame implements KeyListener, MenuActionListener {
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    private boolean running = true;
    private Camera canvas;

    private CardLayout cardLayout = new CardLayout();
    private JPanel cardPanel = new JPanel(cardLayout);
    private TitleScreen titleScreen = new TitleScreen();

    private String currentCard = "Title";

    private MenuPanel menuPanel;

    private static Game gameInstance = null;

    private Inventory inventory = new Inventory();
    private InventoryPanel inventoryPanel;


    @Override
    public void onSave() {
        //System.out.println("Save the game state");
        canvas.saveGame();
    }

    @Override
    public void onLoad(String name) {

        canvas.loadGame(name);
    }

    @Override
    public void onExit() {
        System.exit(0);
    }

    @Override
    public ArrayList<String> onLoadSaves() throws SQLException {
        SaveManager saveManager = new SaveManager("saves.db");
        return saveManager.loadSaveNames();
    }


    @Override
    public void onDeleteSave(String saveName) throws SQLException {
        SaveManager saveManager = new SaveManager("saves.db");
        saveManager.deleteSave(saveName);
    }

    @Override
    public void onInventory() {
        System.out.println("Inventory selected");
        cardLayout.show(cardPanel, "Inventory");
        currentCard = "Inventory";
        inventoryPanel.setVisible(true);
        inventoryPanel.requestFocusInWindow();
    }

    @Override
    public void onExitMenu() {
        cardLayout.show(cardPanel, "Game");
        currentCard = "Game";
        canvas.requestFocusInWindow();
    }

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



        canvas = new Camera();
        canvas.addKeyListener(this);
        cardPanel.add(canvas, "Game");
//
//        menuPanel = new MenuPanel();
//        menuPanel.addKeyListener(this);
//        cardPanel.add(menuPanel, "Menu");
//
//        // Initially show the title screen
//        cardLayout.show(cardPanel, "Title");
//        titleScreen.requestFocus();
        menuPanel = new MenuPanel(this);
        menuPanel.addKeyListener(this);
        menuPanel.setFocusable(true);
        cardPanel.add(menuPanel, "Menu");

        cardLayout.show(cardPanel, "Title");
        setVisible(true);

        inventory = canvas.getInventory();
        inventoryPanel = new InventoryPanel(inventory, this);
        cardPanel.add(inventoryPanel, "Inventory");
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
                    menuPanel.keyPressed(e);  
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
