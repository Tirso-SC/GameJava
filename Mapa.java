import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Mapa {

    BufferedImage[] tiles;
    int[][] mapa;
    int tamaño; // tamaño en píxeles de cada tile en pantalla

    public Mapa(int tamaño) {
        this.tamaño = tamaño;
        this.tiles  = new BufferedImage[10];
        cargarTiles();
        cargarMapa();
    }

    private void cargarTiles() {
        try {
            BufferedImage tileset = ImageIO.read(
                new File("Assets/Background/GandalfHardcore FREE Platformer Assets/Floor Tiles1.png")
            );
            if (tileset == null) {
                System.err.println("No se pudo cargar el tileset.");
                return;
            }
            int t = 32; // tamaño original de cada tile en la imagen
            tiles[1] = tileset.getSubimage(10, 32 * 6, t - 5, t - 5); // suelo
            tiles[2] = tileset.getSubimage(10, 20, t, t);              // plataforma
        } catch (Exception e) {
            System.err.println("Error cargando tiles: " + e.getMessage());
        }
    }

    private void cargarMapa() {
        // 0 = vacío | 1 = suelo | 2 = plataforma
        mapa = new int[][] {
{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,},
{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,},
{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,},
{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,},
{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,},
{0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,},
{0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,},
{1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,1,1,1,1,1,1,1,1,1,0,1,0,1,1,1,1,1,1,1,0,0,}
        };
    }

    public void pintar(Graphics g, int camaraX) {
        for (int fila = 0; fila < mapa.length; fila++) {
            for (int col = 0; col < mapa[fila].length; col++) {
                int id = mapa[fila][col];
                if (id == 0 || tiles[id] == null) continue;
                int x = col * tamaño - camaraX;
                int y = fila * tamaño;
                g.drawImage(tiles[id], x, y, tamaño, tamaño, null);
            }
        }
    }

    // Devuelve true si la coordenada de mundo cae dentro de un tile sólido
    public boolean esSolido(int mundoX, int mundoY) {
        int col  = mundoACol(mundoX);
        int fila = mundoAFila(mundoY);
        if (fila < 0 || col < 0 || fila >= mapa.length || col >= mapa[fila].length) return false;
        return mapa[fila][col] != 0;
    }

    public int mundoACol(int mundoX) {
        return mundoX / tamaño;
    }

    public int mundoAFila(int mundoY) {
        return mundoY / tamaño;
    }

    public int getAnchoPíxeles() {
        if (mapa == null || mapa.length == 0) return 0;
        return mapa[0].length * tamaño;
    }
}
