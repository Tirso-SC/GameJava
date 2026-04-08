import java.awt.Color;
import java.awt.Graphics;

public abstract class Objeto {
    protected int x;
    protected int y;
    protected int speed;
    protected int width;
    protected int height;
    protected Color color;

    public Objeto(int x, int y, int speed, int width, int height, Color color) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    public void mover(KeyHandler keyH, int limiteAncho, int limiteAlto) {
        if (keyH.upPressed) {
            y -= speed;
        }
        if (keyH.downPressed) {
            y += speed;
        }
        if (keyH.leftPressed) {
            x -= speed;
        }
        if (keyH.rightPressed) {
            x += speed;
        }

        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x + width > limiteAncho) {
            x = limiteAncho - width;
        }
        if (y + height > limiteAlto) {
            y = limiteAlto - height;
        }
    }

    public abstract void pintar(Graphics g);
}
