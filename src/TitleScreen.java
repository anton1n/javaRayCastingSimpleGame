import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class TitleScreen extends JPanel {
    private BufferedImage background;

    public TitleScreen() {
        setLayout(new BorderLayout());
        loadBackgroundImage();
    }

    private void loadBackgroundImage() {
        try {
            background = ImageIO.read(new File("titleScreen.png")); // Make sure the path is correct
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to load background image.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);


            int fontSize = getWidth() / 30;
            g.setFont(new Font("Serif", Font.PLAIN, fontSize));
            g.setColor(Color.WHITE);

            // center
            String text = "Press enter to continue";
            FontMetrics metrics = g.getFontMetrics();
            int x = (getWidth() - metrics.stringWidth(text)) / 2;
            int y = (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent();
            g.drawString(text, x, y);
        }
    }
}
