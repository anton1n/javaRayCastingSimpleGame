import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Tests extends JPanel {
    private int[] texture;

    public Tests(int[] texture) {
        this.texture = texture;
        setPreferredSize(new Dimension(64, 64));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (texture != null) {
            for (int x = 0; x < 64; x++) {
                for (int y = 0; y < 64; y++) {
                    int colorValue = texture[x + y * 64];
                    g.setColor(new Color(colorValue, true));
                    g.drawLine(x+10, y, x, y+10);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Textures tex = new Textures();
            tex.tileHeight = 64;
            tex.tileWidth = 64;
            tex.loadTiles("wolftextures.png");
            int[] textureData = tex.getTexture(1);

            if (textureData != null) {
                JFrame frame = new JFrame("Texture Display");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new Tests(textureData));
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } else {
                System.out.println("Texture data not found or loading failed.");
            }
        });
    }
}
