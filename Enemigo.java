import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Enemigo extends Entidad {

    // Constantes
    final int    VX           = 2;
    final double GRAVEDAD     = 0.70;
    final double VY_MAX       = 14.0;
    final int    VIDA_INICIAL = 2;

    // Estado
    double  vy;
    boolean enSuelo;
    boolean miraDerecha;    
    boolean herido;
    boolean muerto;
    int     vida;

    // Animaciones
    Animacion animCaminar;
    Animacion animHerido;
    Animacion animMuerto;
    Animacion animActual;

    // Carpeta de sprites del enemigo
    static final String DIR = "Assets/Personajes/craftpix-net-407836-free-ninja-sprite-sheets-pixel-art/Ninja_Peasant/";

    public Enemigo(int mundoX, int y, int tamaño) {
        super(mundoX, y, tamaño, tamaño);
        this.vida        = VIDA_INICIAL;
        this.miraDerecha = true;
        this.vy          = 0;
        cargarAnimaciones();
    }

    private void cargarAnimaciones() {
        animCaminar = new Animacion(DIR + "Run.png",  6, true);
        animHerido  = new Animacion(DIR + "Hurt12.png", 10, false);
        animMuerto  = new Animacion(DIR + "Dead.png", 6, false);
        animActual  = animCaminar;
    }

    @Override
    public void actualizar(PanelJuego panel) {
        if (muerto) {
            animMuerto.actualizar();
            return;
        }

        if (herido) {
            animHerido.actualizar();
            if (animHerido.haTerminado()) {
                herido = false;
                animActual = animCaminar;
            }
            return;
        }

        mover(panel.getMapa());
        animActual.actualizar();
    }

    private void mover(Mapa mapa) {
        // Movimiento horizontal automático
        mundoX += miraDerecha ? VX : -VX;

        // Gravedad
        vy += GRAVEDAD;
        if (vy > VY_MAX) vy = VY_MAX;
        y += (int) vy;

        if (mapa != null) {
            // Colisión con el suelo
            int pieX1 = mundoX + 5;
            int pieX2 = mundoX + ancho - 5;
            int pieY  = y + alto;

            if (mapa.esSolido(pieX1, pieY) || mapa.esSolido(pieX2, pieY)) {
                int fila = mapa.mundoAFila(pieY);
                y       = fila * mapa.tamaño - alto;
                vy      = 0;
                enSuelo = true;
            }

            // Cambiar dirección si choca con una pared lateral
            int centroY = y + alto / 2;
            if (miraDerecha  && mapa.esSolido(mundoX + ancho, centroY)) miraDerecha = false;
            if (!miraDerecha && mapa.esSolido(mundoX, centroY))          miraDerecha = true;

            // Cambiar dirección si se acaba el suelo por delante
            if (enSuelo) {
                int frenteX = miraDerecha ? mundoX + ancho + 1 : mundoX - 1;
                if (!mapa.esSolido(frenteX, y + alto + 1)) {
                    miraDerecha = !miraDerecha;
                }
            }
        }
    }

    public void recibirDaño(int daño) {
        if (muerto) return;
        vida -= daño;
        if (vida <= 0) {
            muerto     = true;
            animActual = animMuerto;
            animMuerto.reiniciar();
        } else {
            herido = true;
            animActual = animHerido;
            animHerido.reiniciar();
        }
    }

    // Devuelve true cuando la animación de muerte ha terminado (se puede eliminar de la lista)
    public boolean estaEliminado() {
        return muerto && animMuerto.haTerminado();
    }

    // Hitbox reducida para colisiones con el jugador
    public Rectangle getHitbox() {
        return new Rectangle(mundoX + ancho / 4, y + alto / 4, ancho / 2, alto * 3 / 4);
    }

    @Override
    public void pintar(Graphics g, int camaraX) {
        Graphics2D g2    = (Graphics2D) g;
        BufferedImage frame = animActual.getFrame();
        if (frame == null) return;

        int pantallaX = mundoX - camaraX;
        if (miraDerecha) {
            g2.drawImage(frame, pantallaX,        y, ancho,  alto, null);
        } else {
            g2.drawImage(frame, pantallaX + ancho, y, -ancho, alto, null);
        }
    }
}
