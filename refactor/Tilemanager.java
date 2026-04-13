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
            {0,0,0,0,0,0,0,2,0,0,0,0,0,0,0},
            {0,0,0,2,0,0,0,0,0,0,0,0,0,0,0},
            {1,1,1,1,1,1,1,1,1,1,0,0,0,1,1},
            {2,2,2,2,2,2,2,2,2,2,0,0,0,2,2},

            
        };
    }

    public void draw(Graphics g, int cameraX) {
        for (int fila = 0; fila < mapa.length; fila++) {
            for (int col = 0; col < mapa[fila].length; col++) {
                int tileId = mapa[fila][col];

                if (tileId == 0 || tiles[tileId] == null) {
                    continue;
                }

                int worldX = col * tileSize;
                int x = worldX - cameraX;
                int y = fila * tileSize;
                g.drawImage(tiles[tileId], x, y, tileSize, tileSize, null);
            }
        }
    }

    public int getTileSize() {
        return tileSize;
    }

    public int getTileStepX() {
        return tileSize;
    }

    public int getMapPixelWidth() {
        if (mapa == null || mapa.length == 0) {
            return 0;
        }
        int cols = mapa[0].length;
        return cols * tileSize;
    }

    public int getMapPixelHeight() {
        if (mapa == null) {
            return 0;
        }
        return mapa.length * tileSize;
    }

    public int worldToCol(int worldX) {
        int stepX = getTileStepX();
        if (stepX <= 0) {
            return 0;
        }
        return worldX / stepX;
    }

    public int worldToRow(int worldY) {
        if (tileSize <= 0) {
            return 0;
        }
        return worldY / tileSize;
    }

    public boolean isSolidAt(int worldX, int worldY) {
        if (mapa == null) {
            return false;
        }

        int col = worldToCol(worldX);
        int row = worldToRow(worldY);

        if (row < 0 || col < 0 || row >= mapa.length || col >= mapa[row].length) {
            return false;
        }

        return mapa[row][col] != 0;
    }

    public boolean isSolidTile(int row, int col) {
        if (mapa == null) {
            return false;
        }
        if (row < 0 || col < 0 || row >= mapa.length || col >= mapa[row].length) {
            return false;
        }
        return mapa[row][col] != 0;
    }
}