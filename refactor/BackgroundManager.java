import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class BackgroundManager {

    private BufferedImage sky;
    private BufferedImage mountains;
    private BufferedImage castle;
    private BufferedImage treesFar;
    private BufferedImage treesMid;
    private BufferedImage treesNear;

    private final int screenWidth;
    private final int screenHeight;

    private int cameraX = 0;

    public BackgroundManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        cargarCapas();
    }

    private void cargarCapas() {
        try {
            String basePath =
                "..\\Assets\\Background\\GandalfHardcore FREE Platformer Assets\\"
              + "GandalfHardcore Background layers\\Autumn BG\\";

            sky = ImageIO.read(new File(basePath + "GandalfHardcore Background layers_layer 1.png"));
            mountains = ImageIO.read(new File(basePath + "GandalfHardcore Background layers_layer 2.png"));
            castle = ImageIO.read(new File(basePath + "Background Castle Autumn.png"));
            treesFar = ImageIO.read(new File(basePath + "GandalfHardcore Background layers_layer 3.png"));
            treesMid = ImageIO.read(new File(basePath + "GandalfHardcore Background layers_layer 4.png"));
            treesNear = ImageIO.read(new File(basePath + "GandalfHardcore Background layers_layer 5.png"));

        } catch (Exception e) {
            System.err.println("Error cargando capas del fondo");
            e.printStackTrace();
        }
    }

    public void setCameraX(int cameraX) {
        this.cameraX = Math.max(0, cameraX);
    }

    public void draw(Graphics g) {
        drawLayer(g, sky, 0.1);
        drawLayer(g, mountains, 0.2);
        drawLayer(g, castle, 0.3);
        drawLayer(g, treesFar, 0.45);
        drawLayer(g, treesMid, 0.65);
        drawLayer(g, treesNear, 0.85);
    }

    private void drawLayer(Graphics g, BufferedImage layer, double speedFactor) {
        if (layer == null) return;

        int layerWidth = layer.getWidth();
        int layerHeight = layer.getHeight();

        double rawOffset = cameraX * speedFactor;
        int sourceX = (int) rawOffset;

        int maxSourceX = Math.max(0, layerWidth - screenWidth);
        if (sourceX > maxSourceX) {
            sourceX = maxSourceX;
        }

        g.drawImage(
            layer,
            0, 0, screenWidth, screenHeight,
            sourceX, 0, sourceX + screenWidth, layerHeight,
            null
        );
    }
}