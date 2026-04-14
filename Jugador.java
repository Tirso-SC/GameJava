import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Jugador extends Entidad {

    // Constantes de movimiento
    final int    VX               = 7;
    final double VELOCIDAD_SALTO  = -14.0;
    final double GRAVEDAD         = 0.70;
    final double VY_MAX           = 14.0;

    // Estado
    double  vy;
    boolean enSuelo;
    boolean miraDerecha;
    boolean atacando;
    boolean golpeAplicado;

    // Animaciones
    Animacion animIdle;
    Animacion animCaminar;
    Animacion animSalto;
    Animacion animAtaque;
    Animacion animActual;

    // Carpeta de sprites del jugador
    static final String DIR = "Assets/Personajes/SamurayPersonajes/Samurai/";

    public Jugador(int mundoX, int y, int tamaño) {
        super(mundoX, y, tamaño, tamaño);
        this.vy          = 0;
        this.enSuelo     = false;
        this.miraDerecha = true;
        cargarAnimaciones();
    }

    private void cargarAnimaciones() {
        animIdle    = new Animacion(DIR + "Idle.png",     5, true);
        animCaminar = new Animacion(DIR + "Walk.png",     5, true);
        animSalto   = new Animacion(DIR + "Jump.png",     5, false);
        animAtaque  = new Animacion(DIR + "Attack_1.png", 5, false);
        animActual  = animIdle;
    }

    @Override
    public void actualizar(PanelJuego panel) {
        mover(panel);
        animActual.actualizar();
    }

    private void mover(PanelJuego panel) {
        boolean izquierda = panel.isTeclaPresionada(KeyEvent.VK_A);
        boolean derecha   = panel.isTeclaPresionada(KeyEvent.VK_D);
        boolean salto     = panel.isTeclaPresionada(KeyEvent.VK_W);
        boolean ataque    = panel.isTeclaPresionada(KeyEvent.VK_I);

        int dx = 0;

        // Movimiento horizontal
        if (derecha) {
            dx = VX;
            miraDerecha = true;
        } else if (izquierda) {
            dx = -VX;
            miraDerecha = false;
        }
        mundoX += dx;

        // Salto
        if (salto && enSuelo) {
            vy       = VELOCIDAD_SALTO;
            enSuelo  = false;
            animSalto.reiniciar();
        }

        // Ataque
        if (ataque && !atacando) {
            atacando      = true;
            golpeAplicado = false;
            animAtaque.reiniciar();
        }
        if (atacando && animAtaque.haTerminado()) {
            atacando = false;
        }

        // Gravedad
        if (!enSuelo) {
            vy += GRAVEDAD;
            if (vy > VY_MAX) vy = VY_MAX;
        }
        y += (int) vy;

        // Colisiones con el mapa
        checkeaColisionSuelo(panel.getMapa());
        checkeaColisionTecho(panel.getMapa());
        checkeaColisionParedes(panel.getMapa(), dx);

        // No salir del mundo por la izquierda
        if (mundoX < 0) mundoX = 0;

        // Actualizar qué animación toca
        if (atacando) {
            animActual = animAtaque;
        } else if (!enSuelo) {
            animActual = animSalto;
        } else if (izquierda || derecha) {
            animActual = animCaminar;
        } else {
            animActual = animIdle;
        }
    }

    private int hitboxOffsetX() {
        return ancho / 4;
    }

    private int hitboxOffsetY() {
        return alto / 4;
    }

    private int hitboxAncho() {
        return ancho / 2;
    }

    private int hitboxAlto() {
        return alto * 3 / 4;
    }

    private void checkeaColisionSuelo(Mapa mapa) {
        if (mapa == null) return;

        int hbX = mundoX + hitboxOffsetX();
        int hbY = y + hitboxOffsetY();
        int hbW = hitboxAncho();
        int hbH = hitboxAlto();

        int pieX1 = hbX + 2;
        int pieX2 = hbX + hbW - 2;
        int pieY  = hbY + hbH;

        if (vy >= 0 && (mapa.esSolido(pieX1, pieY) || mapa.esSolido(pieX2, pieY))) {
            int fila = mapa.mundoAFila(pieY);
            y       = fila * mapa.tamaño - alto;
            vy      = 0;
            enSuelo = true;
        } else if (!mapa.esSolido(pieX1, pieY + 1) && !mapa.esSolido(pieX2, pieY + 1)) {
            enSuelo = false;
        }
    }

    private void checkeaColisionTecho(Mapa mapa) {
        if (mapa == null) return;

        int hbX = mundoX + hitboxOffsetX();
        int hbY = y + hitboxOffsetY();
        int hbW = hitboxAncho();

        int cabezaX1 = hbX + 2;
        int cabezaX2 = hbX + hbW - 2;
        int cabezaY  = hbY;

        if (vy < 0 && (mapa.esSolido(cabezaX1, cabezaY) || mapa.esSolido(cabezaX2, cabezaY))) {
            int fila = mapa.mundoAFila(cabezaY);
            y  = (fila + 1) * mapa.tamaño - hitboxOffsetY();
            vy = 0;
        }
    }

    private void checkeaColisionParedes(Mapa mapa, int dx) {
        if (mapa == null) return;

        int hbX = mundoX + hitboxOffsetX();
        int hbY = y + hitboxOffsetY();
        int hbW = hitboxAncho();
        int hbH = hitboxAlto();

        int centroY1 = hbY + 2;
        int centroY2 = hbY + hbH - 2;

        if (dx > 0 && (mapa.esSolido(hbX + hbW, centroY1) || mapa.esSolido(hbX + hbW, centroY2))) {
            int col = mapa.mundoACol(hbX + hbW);
            mundoX  = col * mapa.tamaño - hbW - hitboxOffsetX();
        }
        if (dx < 0 && (mapa.esSolido(hbX, centroY1) || mapa.esSolido(hbX, centroY2))) {
            int col = mapa.mundoACol(hbX);
            mundoX  = (col + 1) * mapa.tamaño - hitboxOffsetX();
        }
    }

    // Zona de ataque delante del jugador (para detectar golpes a enemigos)
    public Rectangle getZonaAtaque() {
        if (!atacando || golpeAplicado) return null;
        int ax = miraDerecha ? mundoX + ancho : mundoX - ancho / 2;
        return new Rectangle(ax, y + alto / 4, ancho / 2, alto / 2);
    }

    // Hitbox reducida (más ajustada que el sprite completo)
    public Rectangle getHitbox() {
        return new Rectangle(mundoX + hitboxOffsetX(), y + hitboxOffsetY(), hitboxAncho(), hitboxAlto());
    }

    public void marcarGolpe() {
        golpeAplicado = true;
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
