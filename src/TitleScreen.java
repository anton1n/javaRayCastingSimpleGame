import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

public class TitleScreen extends Screen {
    public TitleScreen() {
        super("Press enter to start!", "/assets/textures/titleScreen.png");
    }
}