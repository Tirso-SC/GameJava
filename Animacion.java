import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Animacion {

    final int VELOCIDAD;
    BufferedImage[] frames;
    int frameActual;
    int contador;
    boolean bucle;

    // Constructor: ruta al sprite sheet, velocidad (ticks por frame), si repite en bucle
    public Animacion(String ruta, int velocidad, boolean bucle) {
        this.VELOCIDAD  = velocidad;
        this.bucle      = bucle;
        this.frameActual = 0;
        this.contador    = 0;
        this.frames      = cargarFrames(ruta);
    }

    // Carga los frames de un sprite sheet horizontal (frames cuadrados: alto = ancho de cada frame)
    private BufferedImage[] cargarFrames(String ruta) {
        try {
            BufferedImage hoja = ImageIO.read(new File(ruta));
            if (hoja == null) {
                System.err.println("No se pudo leer la imagen: " + ruta);
                return new BufferedImage[0];
            }
            int altoFrame  = hoja.getHeight();
            int anchoFrame = altoFrame;
            int numFrames  = hoja.getWidth() / anchoFrame;

            BufferedImage[] resultado = new BufferedImage[numFrames];
            for (int i = 0; i < numFrames; i++) {
                resultado[i] = hoja.getSubimage(i * anchoFrame, 0, anchoFrame, altoFrame);
            }
            return resultado;

        } catch (Exception e) {
            System.err.println("Error al cargar animación: " + ruta);
            return new BufferedImage[0];
        }
    }

    public void actualizar() {
        contador++;
        if (contador >= VELOCIDAD) {
            contador = 0;
            if (bucle) {
                frameActual = (frameActual + 1) % frames.length;
            } else if (frameActual < frames.length - 1) {
                frameActual++;
            }
        }
    }

    public BufferedImage getFrame() {
        if (frames.length == 0) return null;
        return frames[frameActual];
    }

    public void reiniciar() {
        frameActual = 0;
        contador    = 0;
    }

    public boolean haTerminado() {
        return !bucle && frameActual >= frames.length - 1;
    }

    public int getFrameActual() {
        return frameActual;
    }
}
