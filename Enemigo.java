import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public abstract class Enemigo extends Entidad {

    // Estado
    double  vy;
    boolean enSuelo;
    boolean miraDerecha;
    boolean herido;
    boolean muerto;
    boolean atacando;
    boolean golpeAplicado;
    int     vida;

    Animacion animIdle;
    Animacion animCaminar;
    Animacion animHerido;
    Animacion animMuerto;
    Animacion animAtacar;
    Animacion animActual;

    // Física
    final double GRAVEDAD = 0.70;
    final double VY_MAX   = 14.0;

    public Enemigo(int mundoX, int y, int ancho, int alto, int vidaInicial) {
        super(mundoX, y, ancho, alto);
        this.vida        = vidaInicial;
        this.miraDerecha = true;
        this.vy          = 0;
        this.enSuelo     = false;
        this.herido      = false;
        this.muerto      = false;
        this.atacando    = false;
        this.golpeAplicado = false;
        cargarAnimaciones();
    }

    // Cada enemigo carga sus propios sprites
    protected abstract void cargarAnimaciones();

    // Cada enemigo decide cómo moverse y atacar
    protected abstract void decidir(Mapa mapa, Jugador jugador);

    // Cada enemigo define su hitbox de ataque
    public abstract Rectangle getHitboxAtaque();

    // Cada enemigo define cuánto daño hace
    public abstract int getDaño();

    @Override
    public void actualizar(PanelJuego panel) {
        actualizar(panel, panel.getJugador());
    }

    public void actualizar(PanelJuego panel, Jugador jugador) {
        if (muerto) {
            animMuerto.actualizar();
            return;
        }

        if (herido) {
            animHerido.actualizar();
            if (animHerido.haTerminado()) {
                herido    = false;
                animActual = animIdle;
            }
            return;
        }

        if (atacando) {
            actualizarAtaque(jugador);
            aplicarFisica(panel.getMapa());
            return;
        }

        decidir(panel.getMapa(), jugador);
        aplicarFisica(panel.getMapa());

        if (!atacando) {
            animActual.actualizar();
        }
    }

    private void actualizarAtaque(Jugador jugador) {
        animAtacar.actualizar();

        if (!golpeAplicado && animAtacar.getFrameActual() >= getFrameGolpe()) {
            golpeAplicado = true;
            if (jugador != null && getHitboxAtaque().intersects(jugador.getHitbox())) {
                jugador.recibirDaño(getDaño());
            }
        }

        if (animAtacar.haTerminado()) {
            atacando   = false;
            animActual = animIdle;
        }
    }

    // Por defecto frame 3, cada subclase puede sobreescribirlo
    protected int getFrameGolpe() {
        return 3;
    }

    protected void iniciarAtaque() {
        atacando      = true;
        golpeAplicado = false;
        animActual    = animAtacar;
        animAtacar.reiniciar();
    }

    protected void aplicarFisica(Mapa mapa) {
        enSuelo = false;

        vy += GRAVEDAD;
        if (vy > VY_MAX) vy = VY_MAX;
        y += (int) vy;

        if (mapa == null) return;

        int pieX1 = mundoX + 5;
        int pieX2 = mundoX + ancho - 5;
        int pieY  = y + alto;

        if (mapa.esSolido(pieX1, pieY) || mapa.esSolido(pieX2, pieY)) {
            int fila = mapa.mundoAFila(pieY);
            y       = fila * mapa.tamaño - alto;
            vy      = 0;
            enSuelo = true;
        }
    }

    protected boolean puedeAvanzar(Mapa mapa, boolean haciaDerecha, int vx) {
        if (mapa == null) return true;

        int siguienteX = haciaDerecha ? mundoX + vx : mundoX - vx;
        int ladoX      = haciaDerecha ? siguienteX + ancho : siguienteX;

        boolean pared = mapa.esSolido(ladoX, y + 8) || mapa.esSolido(ladoX, y + alto - 8);
        if (pared) return false;

        int frenteY = y + alto + 1;
        return mapa.esSolido(ladoX, frenteY);
    }

    public void recibirDaño(int daño) {
        if (muerto) return;

        vida -= daño;

        if (vida <= 0) {
            muerto     = true;
            atacando   = false;
            animActual = animMuerto;
            animMuerto.reiniciar();
        } else {
            herido     = true;
            atacando   = false;
            animActual = animHerido;
            animHerido.reiniciar();
        }
    }

    public boolean estaEliminado() {
        return muerto && animMuerto.haTerminado();
    }

    public Rectangle getHitbox() {
        return new Rectangle(mundoX + ancho / 4, y + alto / 4, ancho / 2, alto * 3 / 4);
    }

    @Override
    public void pintar(Graphics g, int camaraX) {
        BufferedImage frame = animActual.getFrame();
        if (frame == null) return;

        Graphics2D g2 = (Graphics2D) g;
        int pantallaX = mundoX - camaraX;

        if (miraDerecha) {
            g2.drawImage(frame, pantallaX,        y,  ancho, alto, null);
        } else {
            g2.drawImage(frame, pantallaX + ancho, y, -ancho, alto, null);
        }
    }
}