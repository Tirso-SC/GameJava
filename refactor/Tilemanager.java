import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Tilemanager {

    BufferedImage[] tiles;
    int[][] mapa;
    int tileSize;

    public Tilemanager(int tileSize) {
        this.tileSize = tileSize;
        tiles = new BufferedImage[10];

        cargarTiles();
        cargarMapa();
    }
    private void cargarTiles() {
        try {
            BufferedImage tileset = ImageIO.read(
                new File("../Assets/Background/GandalfHardcore FREE Platformer Assets/Floor Tiles1.png")
            );
            
            if (tileset == null) {
                System.err.println("No se pudo cargar el tileset del suelo.");
                return;
            }

            int tileOriginal = 32; // tamaño de cada pieza en la imagen original

            tiles[0] = null; // vacío
            tiles[1] = tileset.getSubimage(10, 32*6, tileOriginal-5, tileOriginal-5);
            tiles[2] = tileset.getSubimage(10, 20, tileOriginal, tileOriginal);
            tiles[4] = tileset.getSubimage(0, 32, tileOriginal, tileOriginal);

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
            {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},

            
        };
    }

    public void draw(Graphics g) {
        for (int fila = 0; fila < mapa.length; fila++) {
            for (int col = 0; col < mapa[fila].length; col++) {
                int tileId = mapa[fila][col];

                if (tileId == 0 || tiles[tileId] == null) {
                    continue;
                }

                int x = col * (tileSize-3);
                int y = fila * tileSize;
                System.out.println(x);
                g.drawImage(tiles[tileId], x, y, tileSize, tileSize, null);
            }
        }
    }
}