import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Staticobjectmanajer {

    BufferedImage[] objects;
    int[][] mapa;
    int tileSize;
    double[] widthScale;
    double[] heightScale;

    public Staticobjectmanajer(int tileSize) {
        this.tileSize = tileSize;
        objects = new BufferedImage[10];
        widthScale = new double[10];
        heightScale = new double[10];

        cargarObjetos();
        cargarMapa();
    }

    private void cargarObjetos() {
        try {
            objects[1] = ImageIO.read(
                new File("../Assets/Background/GandalfHardcore FREE Platformer Assets/Tree3.png")
            );
            objects[2] = ImageIO.read(
                new File("../Assets/Background/GandalfHardcore FREE Platformer Assets/Large Pine Tree.png")
            );
            objects[3] = ImageIO.read(
                new File("../Assets/Background/GandalfHardcore FREE Platformer Assets/Angel Statue.png")
            );

            // Tamaños por tipo de objeto en unidades de tile.
            widthScale[1] = 10;
            heightScale[1] = 10;

            widthScale[2] = 14;
            heightScale[2] = 14;

            widthScale[3] = 1.2;
            heightScale[3] = 1.6;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarMapa() {
        mapa = new int[][] {
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,1,0,1,1,0,0,1,0,0,1,3,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}
        };
    }

    public void draw(Graphics g, int cameraX) {
        Rectangle clip = g.getClipBounds();

        for (int fila = 0; fila < mapa.length; fila++) {
            for (int col = 0; col < mapa[fila].length; col++) {
                int objectId = mapa[fila][col];

                if (objectId == 0 || objects[objectId] == null) {
                    continue;
                }

                int worldX = col * tileSize;
                int baseY = (fila + 1) * tileSize;
                int drawW = (int) (tileSize * widthScale[objectId]);
                int drawH = (int) (tileSize * heightScale[objectId]);

                // Centrar horizontalmente en la celda y apoyar el sprite en su base.
                int drawX = worldX - cameraX + (tileSize - drawW) / 2;
                int drawY = baseY - drawH;

                // Evita dibujar sprites completamente fuera de pantalla.
                if (clip != null) {
                    if (drawX + drawW < clip.x || drawX > clip.x + clip.width) {
                        continue;
                    }
                    if (drawY + drawH < clip.y || drawY > clip.y + clip.height) {
                        continue;
                    }
                }

                g.drawImage(objects[objectId], drawX, drawY, drawW, drawH, null);
            }
        }
    }
}