import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ObjetoEstatico {

    BufferedImage imagen;
    int x, y;
    int ancho, alto;

    public ObjetoEstatico(String rutaImagen, int x, int y, int ancho, int alto) {
        this.x = x;
        this.y = y;
        this.ancho = ancho;
        this.alto = alto;
        cargarImagen(rutaImagen);
    }

    private void cargarImagen(String ruta) {
        try {
            imagen = ImageIO.read(new File(ruta));
            if (imagen == null) {
                System.err.println("No se pudo cargar la imagen del objeto: " + ruta);
            }
        } catch (Exception e) {
            System.err.println("Error cargando objeto estático: " + e.getMessage());
        }
    }

    public void pintar(Graphics g, int camaraX) {
        if (imagen != null) {
            // Dibujamos el objeto restando la cámara a su posición X
            g.drawImage(imagen, x - camaraX, y, ancho, alto, null);
        }
    }
    
    // Getters y Setters por si más adelante necesitas interactuar con él
    public int getX() { return x; }
    public int getY() { return y; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
}
