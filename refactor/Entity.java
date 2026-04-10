import java.awt.Graphics;

public abstract class Entity {
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected Entity(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
// En Entity.java — añade este método
    public int getX() {
        return x;
}

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public abstract void update(ContextoJuego context);

    public abstract void draw(Graphics g);

    protected void clampHorizontal(int screenWidth) {
        if (x < 0) {
            x = 0;
        }
        if (x + width > screenWidth) {
            x = screenWidth - width;
        }
    }
}
