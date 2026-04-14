import java.awt.Graphics;
import java.awt.Rectangle;

// Clase base para todos los elementos del juego que tienen posición y tamaño
public abstract class Entidad {

    int mundoX; // posición X real en el mundo (no en pantalla)
    int y;
    int ancho, alto;

    public Entidad(int mundoX, int y, int ancho, int alto) {
        this.mundoX = mundoX;
        this.y      = y;
        this.ancho  = ancho;
        this.alto   = alto;
    }

    // Rectángulo en coordenadas de pantalla (descontando la cámara)
    public Rectangle getRect(int camaraX) {
        return new Rectangle(mundoX - camaraX, y, ancho, alto);
    }

    // Rectángulo en coordenadas del mundo (para colisiones globales)
    public Rectangle getRectMundo() {
        return new Rectangle(mundoX, y, ancho, alto);
    }

    public abstract void actualizar(PanelJuego panel);
    public abstract void pintar(Graphics g, int camaraX);
}
