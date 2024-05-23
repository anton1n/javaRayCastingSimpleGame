import org.w3c.dom.css.Rect;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

class Camera extends JPanel {
    public static int[][] map;

    private Textures textureManager;

    private BufferedImage image;
    private int[] texturePixels; // to do: make it an array of texturePixels
    private int textureWidth, textureHeight;
    private BufferedImage offscreenImage;

    public BufferedImage img1 = null; //this is temporary, I swear

    private Player player;
    private KeyboardComponent keyboard;

    private List<SpriteComponent> sprites = new ArrayList<SpriteComponent>();

    private List<Enemy> enemies = new ArrayList<>();

    private CollisionManager collisionManager = new CollisionManager(sprites, enemies);

    private Weapon weapon;

    private SoundManager soundManager;

    private List<Item> items = new ArrayList<Item>();

    private Inventory inventory;

    private List<Door> doors = new ArrayList<Door>();
    private int level = 1;

    private SaveManager saveManager;

    public boolean gameFinished = false;
    public boolean playerDead = false;

    private boolean jumpScare = false;
    private long jumpScareEndTime = 0;

    public Camera() throws FileNotFoundException {

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        this.textureManager= new Textures();
        textureManager.loadTiles(System.getProperty("user.dir")+"/assets/textures/allTiles.png");
        //textureManager.loadTile("enemy01.png");

        //player = new Player(22, 12, -1, 0, 0, 0.66);
        player = new Player(20, 8, -1, 0, 0, 0.66);
        keyboard = new KeyboardComponent();

        //weapon = new Weapon("slash.png", 4 ,100, 125, 129 );
        weapon = new Weapon(System.getProperty("user.dir")+"/assets/textures/swordAnim.png", 4 ,100, 378
                , 283 );

        setDoubleBuffered(true);
        setFocusable(true);
        requestFocus();
        System.out.println("Constructor");

        offscreenImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);


        //meh
        try {
            img1 = ImageIO.read(new File(System.getProperty("user.dir")+"/assets/textures/forest.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //

        //sprites.add(new SpriteComponent(20, 15, false, 1));
        //sprites.add(new SpriteComponent(20, 19, false, 2));

//        EnumMap<State, Integer> frameCounts = new EnumMap<>(State.class);
//        frameCounts.put(State.attacking, 6);
//        frameCounts.put(State.idle, 5);
//        frameCounts.put(State.damaged, 1);
//        frameCounts.put(State.walking, 4);
        //frameCounts.put(State.dead, 1);

        //
        //enemies.add(new Enemy(15, 15, "skeletonSprites1.png", frameCounts, 105, 155, 100));


        //enemies.add(new Enemy(15, 15, "skeleton.png", 4, 94, 132, 100));
        //enemies.add(new Enemy(15, 15, 9));

        SoundManager soundManager = new SoundManager();
        soundManager.loadSound("background", System.getProperty("user.dir")+"/assets/sounds/backgroundMusic.wav");
        soundManager.loopSound("background");

       // items.add(new Item(22, 20, 4, textureManager));

        inventory = new Inventory();

        this.map = textureManager.readMap(System.getProperty("user.dir")+"/levels/level2.txt", enemies, sprites, items, textureManager, doors);

        this.saveManager = new SaveManager("saves.db");

        //saveManager.printDatabase();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //render(g);
        //System.out.println("paint");
        if (offscreenImage.getWidth() != getWidth() || offscreenImage.getHeight() != getHeight()) {
            offscreenImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        }

        render(offscreenImage.createGraphics());
        g.drawImage(offscreenImage, 0, 0, this);

        if (jumpScare) {
            if (System.currentTimeMillis() < jumpScareEndTime) {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            } else {
                jumpScare = false;
            }
        }

//        g.setColor(Color.WHITE);
//        g.drawString("Player life: "+Integer.toString(player.getComponent(HealthComponent.class).getHealth()), 100, getWidth()-100);
        //weapon.render(g);
    }

    private void render(Graphics g) {

        int width = getWidth();
        int height = getHeight();

        double[] zBuffer = new double[width];

        g.setColor(Color.BLACK);
        //g.setColor(Color.GREEN.darker().darker().darker().darker().darker().darker());
        g.fillRect(0, 0, width, height);

        //this is temporary, i'm too lazzy to implement horizontal scanlines now
        g.drawImage(img1, 0, 0, width, height/2 + 25,  null);
        //

//        for(Item item : items){
//            item.render(g);
//        }

        PositionComponent pos = player.getComponent(PositionComponent.class);
        double posX = pos.x;
        double posY = pos.y;

        for (int x = 0; x < width; x++) {
            double cameraX = 2 * x / (double) width - 1;
            double rayDirX = player.getDirX() + player.getPlaneX() * cameraX;
            double rayDirY = player.getDirY() + player.getPlaneY() * cameraX;

            int mapX = (int) posX;
            int mapY = (int) posY;

            double sideDistX, sideDistY;
            double deltaDistX = Math.abs(1 / rayDirX);
            double deltaDistY = Math.abs(1 / rayDirY);
            double perpWallDist;

            int stepX, stepY;
            int hit = 0;
            int side = 0;

            // ~ Initial side distance
            if (rayDirX < 0) {
                stepX = -1;
                sideDistX = (posX - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1.0 - posX) * deltaDistX;
            }

            if (rayDirY < 0) {
                stepY = -1;
                sideDistY = (posY - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1.0 - posY) * deltaDistY;
            }

            // ~ DDA
            while (hit == 0) {
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
                if (map[mapX][mapY] > 0) hit = 1;
            }

            // !Euclidean distance will give fisheye effect
            if (side == 0) perpWallDist = (mapX - posX + (1 - stepX) / 2) / rayDirX;
            else perpWallDist = (mapY - posY + (1 - stepY) / 2) / rayDirY;

            zBuffer[x] = perpWallDist;

            int lineHeight = (int) (height / perpWallDist);
            int drawStart = Math.max(-lineHeight / 2 + height / 2, 0);
            int drawEnd = Math.min(lineHeight / 2 + height / 2, height);

            double wallX = (side == 0) ? posY + perpWallDist * rayDirY : posX + perpWallDist * rayDirX;
            wallX -= Math.floor(wallX);

            int texX = (int) (wallX * textureManager.getTextureWidth());
            if (side == 0 && rayDirX > 0) texX = textureManager.getTextureWidth() - texX - 1;
            if (side == 1 && rayDirY < 0) texX = textureManager.getTextureWidth() - texX - 1;

            texX = Math.max(0, Math.min(texX, textureManager.getTextureWidth() - 1));

            int textureId = map[mapX][mapY];
            int[] texturePixels = textureManager.getTexture(textureId);
            int textureWidth = textureManager.getTextureWidth();
            int textureHeight = textureManager.getTextureHeight();

            for (int y = drawStart; y < drawEnd; y++) {
                int d = (y - drawStart) * 256;
                int texY = ((d * textureHeight) / lineHeight) / 256;
                texY = Math.max(0, Math.min(texY, textureHeight - 1));

                int color = texturePixels[texX + texY * textureWidth];
                if (side == 1) color = (color >> 1) & 8355711; // ~ darker for y

                g.setColor(new Color(color));
                g.drawLine(x, y, x, y);
            }
        }
        sortAndRenderSprites(g, zBuffer);

        for (Enemy enemy : enemies) {
            enemy.render(g, this, zBuffer);
        }

        int weaponX = getWidth() / 2 - weapon.width / 2;
        int weaponY = getHeight() - weapon.height + 20;
        weapon.render(g, weaponX, weaponY);

        g.dispose();
    }


    public void update() {

        //System.out.println("update");

        double moveSpeed = 0.1;
        double rotSpeed = 0.05;
        //System.out.println("move");

        PositionComponent pos = player.getComponent(PositionComponent.class);
        Rectangle playerBounds = player.getBounds();

        DimensionsComponent playerDims = player.getComponent(DimensionsComponent.class);
        //Rectangle playerBox = player.getBounds();

        List<Enemy> toRemove = new ArrayList<>();

        //improve this!
        if (player.getComponent(AttackComponent.class).isAttacking()) {
            for (Enemy enemy : enemies) {
                if (collisionManager.checkCollision(playerBounds, enemy.getBounds())) {
                    player.getComponent(AttackComponent.class).attack(enemy.getComponent(HealthComponent.class));
                    //System.out.println(enemy.getComponent(HealthComponent.class).getHealth());
                    if (enemy.getComponent(HealthComponent.class).getHealth() <= 0) {
                        toRemove.add(enemy);
                    }
                    //System.out.println("Enemy hit! ");
                    player.getComponent(AttackComponent.class).setAttacking(false);
                }
            }
        }
        enemies.removeAll(toRemove);

        for (Enemy enemy : enemies) {
//            Rectangle enemyBox = enemy.getBounds();
//            if (collisionManager.checkCollision(playerBox, enemyBox)) {
//                //System.out.println("Collision between " + playerBox + " and " + enemyBox);
//            }
//            else{
//
//            }
            enemy.update(player);
        }

        for (Enemy enemy : enemies) {
            if (enemy instanceof Stalker && ((Stalker) enemy).isJumpScareActive()) {
                jumpScare = true;
                jumpScareEndTime = System.currentTimeMillis() + 2000;  // 2 seconds of jumpscare
                break;
            }
        }

//        Iterator<Item> itemIterator = items.iterator();
//        while (itemIterator.hasNext()) {
//            Item item = itemIterator.next();
//            if (CollisionManager.checkCollision(player.getBounds(), item.getBounds())) {
//                inventory.addItem(item);
//                itemIterator.remove();
//                System.out.println("Item collected!");
//            }
//        }

        for(Item item : items){
            if (CollisionManager.checkCollision(playerBounds, item.getBounds())) {
                inventory.addItem(item);
                items.remove(item);
                //System.out.println("Item collected!");
                break;
            }
        }


        for (Door door : doors) {
            PositionComponent doorPos = door.getComponent(PositionComponent.class);
            if (CollisionManager.checkCollision(playerBounds, door.getBounds())) {
                door.interact(inventory, map);
                if(door.isOpen() && door.getName().compareTo("final_door") == 0){
                    if(level>=3){
                        gameFinished=true;
                        break;
                    }
                    loadNextLevel();
                    break;
                }
            }
        }


        if (keyboard.isKeyPressed(KeyEvent.VK_W)) {
            if (map[(int) (pos.x + player.getDirX() * moveSpeed)][(int) (pos.y)] == 0)
                pos.x += player.getDirX() * moveSpeed;
            else{
                pos.x -= player.getDirX();
            }
            if (map[(int) (pos.x)][(int) (pos.y + player.getDirY() * moveSpeed)] == 0)
                pos.y += player.getDirY() * moveSpeed;

        } else if (keyboard.isKeyPressed(KeyEvent.VK_S)) {
            if (map[(int) (pos.x - player.getDirX() * moveSpeed)][(int) (pos.y)] == 0)
                pos.x -= player.getDirX() * moveSpeed;
            if (map[(int) (pos.x)][(int) (pos.y + player.getDirY() * moveSpeed)] == 0)
                pos.y -= player.getDirY() * moveSpeed;

        } else if (keyboard.isKeyPressed(KeyEvent.VK_A)) {
            if (map[(int) (pos.x - player.getPlaneX() * moveSpeed)][(int) (pos.y)] == 0)
                pos.x -= player.getPlaneX() * moveSpeed;
            if (map[(int) (pos.x)][(int) (pos.y - player.getPlaneY() * moveSpeed)] == 0)

                pos.y -= player.getPlaneY() * moveSpeed;

        } else if (keyboard.isKeyPressed(KeyEvent.VK_D)) {
            if (map[(int) (pos.x + player.getPlaneX() * moveSpeed)][(int) (pos.y)] == 0)
                pos.x += player.getPlaneX() * moveSpeed;
            if (map[(int) (pos.x)][(int) (pos.y + player.getPlaneY() * moveSpeed)] == 0)
                pos.y += player.getPlaneY() * moveSpeed;

        }

        if (keyboard.isKeyPressed(KeyEvent.VK_LEFT)) {
            rotate(rotSpeed);
        } else if (keyboard.isKeyPressed(KeyEvent.VK_RIGHT)) {
            rotate(-rotSpeed);
        }

        if (keyboard.isKeyPressed(KeyEvent.VK_ENTER)){
            weapon.startAnimation();
            //System.out.println("weapon ");
        }
        weapon.update();


        //player.getComponent(AttackComponent.class).setAttacking(weapon.isAnimating());

        if(keyboard.isKeyPressed(KeyEvent.VK_E)){
            saveManager.printDatabase();
        }

        if(keyboard.isKeyPressed(KeyEvent.VK_L)){
            gameFinished=true;
        }

        if(keyboard.isKeyPressed(KeyEvent.VK_P)){
            //System.out.println(level);
            level++;
        }

        if(player.getComponent(HealthComponent.class).getHealth() <= 0){playerDead=true;}
    }

    public void keyPressed(int keyCode) {
        keyboard.pressKey(keyCode);
        if (keyboard.isKeyPressed(KeyEvent.VK_ENTER)) {
            if (!player.getComponent(AttackComponent.class).isAttacking()) {
                player.getComponent(AttackComponent.class).setAttacking(true);
            }
        }
    }


    public void keyReleased(int keyCode) {
        keyboard.releaseKey(keyCode);
    }

    public boolean isEscapePressed() {
        return keyboard.isKeyPressed(KeyEvent.VK_ESCAPE);
    }

    private void rotate(double angle) {
        double oldDirX = player.getDirX();
        double newDirX = oldDirX * Math.cos(angle) - player.getDirY() * Math.sin(angle);
        double newDirY = oldDirX * Math.sin(angle) + player.getDirY() * Math.cos(angle);
        player.updateDirX(newDirX);
        player.updateDirY(newDirY);

        double oldPlaneX = player.getPlaneX();
        double newPlaneX = oldPlaneX * Math.cos(angle) - player.getPlaneY() * Math.sin(angle);
        double newPlaneY = oldPlaneX * Math.sin(angle) + player.getPlaneY() * Math.cos(angle);
        player.updatePlaneX(newPlaneX);
        player.updatePlaneY(newPlaneY);
    }

    private void sortAndRenderSprites(Graphics g, double[] zBuffer) {
        List<SpriteComponent> allSprites = new ArrayList<>(sprites);
        for (Item item : items) {
            if(item.hasComponent(SpriteComponent.class))
                allSprites.add(item.getSprite());
        }

        for(Door door : doors){
            if(door.hasComponent(SpriteComponent.class))
                allSprites.add(door.getSprite());
        }

        Collections.sort(sprites, (s1, s2) -> {
            double d1 = Math.pow(s1.x - player.getComponent(PositionComponent.class).x, 2) + Math.pow(s1.y - player.getComponent(PositionComponent.class).y, 2);
            double d2 = Math.pow(s2.x - player.getComponent(PositionComponent.class).x, 2) + Math.pow(s2.y - player.getComponent(PositionComponent.class).y, 2);
            return Double.compare(d2, d1); // Sort in descending order to render farthest to nearest
        });

        for (SpriteComponent sprite : allSprites) {
            renderSprite(g, sprite, zBuffer);
        }
    }

    public void renderSprite(Graphics g, SpriteComponent sprite, double[] zBuffer) {
        PositionComponent camPos = player.getComponent(PositionComponent.class);
        double posX = camPos.x;
        double posY = camPos.y;
        double dirX = player.getDirX();
        double dirY = player.getDirY();
        double planeX = player.getPlaneX();
        double planeY = player.getPlaneY();

        // Translate sprite position to relative to camera
        double spriteX = sprite.x - posX;
        double spriteY = sprite.y - posY;

        // Inverse camera matrix
        double invDet = 1.0 / (planeX * dirY - dirX * planeY);

        double transformX = invDet * (dirY * spriteX - dirX * spriteY);
        double transformY = invDet * (-planeY * spriteX + planeX * spriteY); // Depth inside the screen

        int spriteScreenX = (int)((getWidth() / 2) * (1 + transformX / transformY));

        // Size of the sprite on the screen
        int spriteHeight = Math.abs((int)(getHeight() / (transformY))); // Avoiding fisheye distortion
        int drawStartY = Math.max(-spriteHeight / 2 + getHeight() / 2, 0);
        int drawEndY = Math.min(spriteHeight / 2 + getHeight() / 2, getHeight());

        int spriteWidth = Math.abs((int)(getHeight() / (transformY)));
        int drawStartX = Math.max(-spriteWidth / 2 + spriteScreenX, 0);
        int drawEndX = Math.min(spriteWidth / 2 + spriteScreenX, getWidth());

        BufferedImage texture = null;

        if(!sprite.animated){
            texture = textureManager.getTextureImage(sprite.textureId);
            if (texture == null) {
                System.out.println("Texture not found for ID: " + sprite.textureId);
                return;
            }
        }
        else{
            texture = sprite.getCurrentFrame();
        }

        for (int stripe = drawStartX; stripe < drawEndX; stripe++) {
            int texX = (256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) * texture.getWidth() / spriteWidth) / 256;
            if (transformY > 0 && transformY < zBuffer[stripe] && stripe > 0 && stripe < getWidth()) {
                for (int y = drawStartY; y < drawEndY; y++) {
                    int d = (y - drawStartY) * 256;
                    int texY = ((d * texture.getHeight()) / spriteHeight) / 256;
                    int color = texture.getRGB(texX, texY);
                    if ((color & 0x00FFFFFF) != 0) {
                        g.setColor(new Color(color, true));
                        g.drawLine(stripe, y, stripe, y);
                    }
                }
            }
        }
    }

    private void checkPlayerAttacks() {
        if (player.getComponent(AttackComponent.class).isAttacking()) {
            for (Enemy enemy : enemies) {
                if (collisionManager.checkCollision(player.getBounds(), enemy.getBounds())) {
                    player.getComponent(AttackComponent.class).attack(enemy.getComponent(HealthComponent.class));
                }
            }
        }
    }

    private void checkEnemyAttacks() {
        for (Enemy enemy : enemies) {
            if (enemy.getComponent(AttackComponent.class).isAttacking()) {
                if (collisionManager.checkCollision(enemy.getBounds(), player.getBounds())) {
                    enemy.getComponent(AttackComponent.class).attack(player.getComponent(HealthComponent.class));
                }
            }
        }
    }

    public Inventory getInventory(){return inventory;}

    private void loadNextLevel() {
        System.out.println("Loading next level...");
    try {
            level++;
            String next_level = System.getProperty("user.dir") + "/levels/level" + Integer.toString(level) + ".txt";

            enemies.clear();
            sprites.clear();
            doors.clear();


            this.map = textureManager.readMap(next_level, enemies, sprites, items, textureManager, doors);
            for (Item item : items){
                item.check();
            }
            System.out.println("Next level loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load the next level: " + e.getMessage());
        }
    }

    public void saveGame(){
        String saveName = "Save_" + System.currentTimeMillis();
        try {
            saveManager.saveGame(saveName, player.getComponent(PositionComponent.class).x,
                    player.getComponent(PositionComponent.class).y,
                    player.getComponent(HealthComponent.class).getHealth(),
                    level, enemies, items, inventory);
            System.out.println("Game saved as: " + saveName);
        }catch(GameException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(e);
        }
    }

    public void loadGame(String saveName)
    {
        GameState gameState;
        try {
            gameState = saveManager.loadGame(saveName, textureManager);

            level = gameState.level - 1;
            loadNextLevel();

            items = gameState.items;
            enemies = gameState.enemies;

            inventory.getItems().clear();
            for(Item item : gameState.inventory.getItems()){
                inventory.addItem(item);
            }

            player.getComponent(HealthComponent.class).setHealth(gameState.health);
            player.getComponent(PositionComponent.class).x = gameState.playerX;
            player.getComponent(PositionComponent.class).y = gameState.playerY;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPlayerDead() {
        //return player.getComponent(HealthComponent.class).getHealth() <= 0;
        return playerDead;
    }

    public boolean isGameFinished(){
        return gameFinished;
    }

    public static boolean isWalkable(int x, int y) {
        if (x < 0 || x >= map.length || y < 0 || y >= map[0].length) {
            return false;
        }
        return map[x][y] == 0;
    }


    public void startGameLoop() {
        Thread gameThread = new Thread(() -> {
            while (true) {

                update();
                repaint();
                try {
                    Thread.sleep(16); // ~ 60 FPS
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        gameThread.start();
    }

}

class GameState {
    double playerX, playerY;
    int health;
    int level;
    List<Enemy> enemies;
    List<Item> items;
    Inventory inventory;
}