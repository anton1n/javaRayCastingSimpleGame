import java.awt.*;

public class Player extends Entity {
    private double dirX, dirY;
    private double planeX, planeY;

    public Player(double startX, double startY, double dirX, double dirY, double planeX, double planeY) {
        this.dirX = dirX;
        this.dirY = dirY;
        this.planeX = planeX;
        this.planeY = planeY;


        this.addComponent(new PositionComponent(startX, startY));
        this.addComponent(new DimensionsComponent(2, 2));
        this.addComponent(new AttackComponent(1, 0));
        addComponent(new HealthComponent(10, this::onDeath));
    }

    public double getDirX() { return dirX; }
    public double getDirY() { return dirY; }
    public double getPlaneX() { return planeX; }
    public double getPlaneY() { return planeY; }

    public void updateDirX(double val){dirX=val; };
    public void updateDirY(double val){dirY=val; };
    public void updatePlaneX(double val){planeX=val; };
    public void updatePlaneY(double val){planeY=val; };

    public void updateDirection(double deltaDirX, double deltaDirY) {
        this.dirX += deltaDirX;
        this.dirY += deltaDirY;
    }

    public Rectangle getBounds(){
        PositionComponent pos = getComponent(PositionComponent.class);
        DimensionsComponent playerDims = getComponent(DimensionsComponent.class);
        return new Rectangle(
                (int) pos.x - playerDims.getWidth() / 2,
                (int) pos.y - playerDims.getHeight() / 2,
                playerDims.getWidth(),
                playerDims.getHeight()
        );
    }

    private void onDeath(HealthComponent healthComponent) {
        System.out.println("Player died");
    }

}

