public class Player extends Entity {
    private double dirX, dirY;  // Direction vector
    private double planeX, planeY;  // Camera plane

    public Player(double startX, double startY, double dirX, double dirY, double planeX, double planeY) {
        this.dirX = dirX;
        this.dirY = dirY;
        this.planeX = planeX;
        this.planeY = planeY;


        this.addComponent(new PositionComponent(startX, startY));
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

}

