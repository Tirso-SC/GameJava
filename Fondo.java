import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Fondo {

    BufferedImage imagen;
    int anchoPantalla, altoPantalla;

    // Qué tan rápido se mueve el fondo respecto al jugador (0 = estático, 1 = igual que jugador)
    final double VELOCIDAD_PARALAJE = 0.3;

    static final String RUTA = "Assets/Background/GandalfHardcore FREE Platformer Assets/"
                             + "GandalfHardcore Background layers/Autumn BG/c.png";

    public Fondo(int anchoPantalla, int altoPantalla) {
        this.anchoPantalla = anchoPantalla;
        this.altoPantalla  = altoPantalla;
        cargarImagen();
    }

    private void cargarImagen() {
        try {
            BufferedImage original = ImageIO.read(new File(RUTA));
            if (original == null) {
                System.err.println("No se pudo cargar el fondo.");
                return;
            }
            // Escalamos para que cubra toda la pantalla en alto
            double escala    = (double) altoPantalla / original.getHeight();
            int nuevoAncho   = (int)(original.getWidth() * escala);

            // Si el ancho no llega al doble de la pantalla, escalamos más para el tiling
            if (nuevoAncho < anchoPantalla * 2) {
                escala     = (anchoPantalla * 2.0) / original.getWidth();
                nuevoAncho = anchoPantalla * 2;
            }
            int nuevoAlto = (int)(original.getHeight() * escala);

            imagen = new BufferedImage(nuevoAncho, nuevoAlto, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = imagen.createGraphics();
            g2.drawImage(original, 0, 0, nuevoAncho, nuevoAlto, null);
            g2.dispose();

        } catch (Exception e) {
            System.err.println("Error cargando fondo: " + e.getMessage());
        }
    }

    public void pintar(Graphics g, int camaraX) {
        if (imagen == null) return;
        int anchoImagen = imagen.getWidth();
        int offset      = (int)(camaraX * VELOCIDAD_PARALAJE) % anchoImagen;

        g.drawImage(imagen, -offset, 0, anchoImagen, altoPantalla, null);

        // Segunda copia para tapar el hueco cuando hay desplazamiento
        if (offset > 0) {
            g.drawImage(imagen, anchoImagen - offset, 0, anchoImagen, altoPantalla, null);
        }
    }
}
