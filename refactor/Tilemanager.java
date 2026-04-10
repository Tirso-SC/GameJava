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
        tiles = new BufferedImage[4];

        cargarTiles();
        cargarMapa();
    }

    private void cargarTiles() {
        try {
            tiles[0] = ImageIO.read(new File("..\\Assets\\Background\\forest-road-background\\PNG\\back.png"));
            tiles[1] = ImageIO.read(new File("..\\Assets\\Background\\forest-road-background\\PNG\\middle.png"));
            tiles[2] = ImageIO.read(new File("..\\Assets\\Background\\forest-road-background\\PNG\\front.png"));
            tiles[3] = ImageIO.read(new File("..\\Assets\\Background\\forest-road-background\\PNG\\middle.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarMapa() {
        mapa = new int[][] {
            {0,0,1,1,2,2,2},
            {0,3,3,1,0,0,2},
            {0,0,0,1,0,0,2},
            {1,1,0,0,0,3,3},
            {2,2,2,0,0,0,0}
        };
    }

    public void draw(Graphics g) {
        for (int fila = 0; fila < mapa.length; fila++) {
            for (int col = 0; col < mapa[fila].length; col++) {
                int tileId = mapa[fila][col];
                int x = col * tileSize;
                int y = fila * tileSize;

                g.drawImage(tiles[tileId], x, y, tileSize, tileSize, null);
            }
        }
    }
}




    

    
