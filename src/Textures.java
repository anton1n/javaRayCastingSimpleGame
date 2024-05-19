import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Textures {

    private Map<Integer, int[]> textures = new HashMap<>();
    private Map<String, BufferedImage> sprites = new HashMap<>();
    public int tileWidth = 32;
    public int tileHeight = 32;

    public int[] getTexture(int id) {
        return textures.get(id);
    }

    public int getTextureWidth() {
        return tileWidth;
    }

    public int getTextureHeight() {
        return tileHeight;
    }

    public void loadTiles(String filepath) {
        try {
            BufferedImage tilemap = ImageIO.read(new File(filepath));
            int numTilesAcross = tilemap.getWidth() / tileWidth;
            int numTilesDown = tilemap.getHeight() / tileHeight;

            for (int i = 0; i < numTilesDown; i++) {
                for (int j = 0; j < numTilesAcross; j++) {
                    BufferedImage subImage = tilemap.getSubimage(j * tileWidth, i * tileHeight, tileWidth, tileHeight);
                    int[] pixelData = new int[tileWidth * tileHeight];
                    subImage.getRGB(0, 0, tileWidth, tileHeight, pixelData, 0, tileWidth);
                    //transposeTextureData(pixelData);
                    textures.put((i * numTilesAcross + j) + 1, pixelData);

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load tile map: " + e.getMessage());
        }
    }

    public void loadTile(String filepath) {
        try {
            BufferedImage tile = ImageIO.read(new File(filepath));

            int[] pixelData = new int[tileWidth * tileHeight];
            tile.getRGB(0, 0, tileWidth, tileHeight, pixelData, 0, tileWidth);

            textures.put(textures.size()+1, pixelData);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load tile map: " + e.getMessage());
        }
    }

    private void transposeTextureData(int[] texture) {
        for (int x = 0; x < tileWidth; x++) {
            for (int y = 0; y < x; y++) {
                int index1 = x * tileWidth + y;
                int index2 = y * tileWidth + x;

                int temp = texture[index1];
                texture[index1] = texture[index2];
                texture[index2] = temp;
            }
        }
    }

    public BufferedImage getTextureImage(int id) {
        int[] pixels = textures.get(id);
        if (pixels == null) {
            return null;
        }
        BufferedImage image = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, tileWidth, tileHeight, pixels, 0, tileWidth);
        return image;
    }


    public void loadSprites(String name, String filepath) {
        try {
            BufferedImage sprite = ImageIO.read(new File(filepath));
            sprites.put(name, sprite);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load sprite: " + e.getMessage());
        }
    }

    public int[][] readMap(String filePath, List<Enemy> enemies, List<SpriteComponent> sprites, List<Item> items, Textures tex, List<Door> doors) throws FileNotFoundException {
        int mapHeight = 30;
        int mapWidth = 38;
        int[][] map = new int[mapHeight][mapWidth];
        int val;

        EnumMap<State, Integer> frameCounts = new EnumMap<>(State.class);
        frameCounts.put(State.attacking, 6);
        frameCounts.put(State.idle, 5);
        frameCounts.put(State.damaged, 1);
        frameCounts.put(State.walking, 4);

        try (Scanner scanner = new Scanner(new File(filePath))) {
            for (int i = 0; i < mapHeight; i++) {
                for (int j = 0; j < mapWidth; j++) {
                    if (scanner.hasNextInt()) {
                        val = scanner.nextInt();

                        if(val == 99){
                            enemies.add(new Enemy(i, j, "skeletonSprites1.png", frameCounts, 105, 155, 100));                            map[i][j] = 0;
                        }
                        else if(val == 88){
                            items.add(new Item(i, j, 4, tex, "key"));
                            map[i][j] = 0;
                        }
                        else if(val == 87){
                            items.add(new Item(i, j, 4, tex, "final_key"));
                            map[i][j] = 0;
                        }
                        else if(val == 77){
                            doors.add(new Door(i,j,"key", map, "door"));
                        }
                        else if(val == 76){
                            doors.add(new Door(i,j,"final_key", map, "final_door"));
                        }
                        else{
                            map[i][j] = val;
                        }

                    }
                }
            }
        }

        System.out.println("map read");

        return map;
    }
}
