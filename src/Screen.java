import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Screen extends JPanel {
    private String text;
    private BufferedImage background;

    public Screen(String text, String imagePath){
        setLayout(new BorderLayout());
        loadBackgroundImage(imagePath);
        this.text = text;
    }

    public Screen(String text ){
        setLayout(new BorderLayout());
        this.text = text;
        background = null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);
            drawString(g);
        }
    }

    public void drawString(Graphics g){
        int fontSize = getWidth() / 30;
        g.setFont(new Font("Serif", Font.PLAIN, fontSize));
        g.setColor(Color.WHITE);

        FontMetrics metrics = g.getFontMetrics();
        int x = (getWidth() - metrics.stringWidth(text)) / 2;
        int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
        g.drawString(text, x, y);
    }

    private void loadBackgroundImage(String imagePath) {
        try {
            background = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to load background image.");
        }
    }
}
