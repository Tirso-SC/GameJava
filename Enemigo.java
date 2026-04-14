import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Enemigo extends Entidad {

    final int VX = 2;
    final double GRAVEDAD = 0.70;
    final double VY_MAX = 14.0;
    final int VIDA_INICIAL = 2;

    final int DISTANCIA_DETECCION = 220;
    final int DISTANCIA_ATAQUE = 70;
    final int FRAME_GOLPE = 3;
    final int DAÑO_ATAQUE = 1;

    double vy;
    boolean enSuelo;
    boolean miraDerecha;
    boolean herido;
    boolean muerto;
    boolean atacando;
    boolean golpeAplicado;
    int vida;

    Animacion animCaminar;
    Animacion animIdle;
    Animacion animHerido;
    Animacion animMuerto;
    Animacion animAtacar;
    Animacion animActual;

    static final String DIR = "Assets/Personajes/craftpix-net-407836-free-ninja-sprite-sheets-pixel-art/Ninja_Peasant/";

    public Enemigo(int mundoX, int y, int tamaño) {
        super(mundoX, y, tamaño, tamaño);
        vida = VIDA_INICIAL;
        miraDerecha = true;
        vy = 0;
        enSuelo = false;
        herido = false;
        muerto = false;
        atacando = false;
        golpeAplicado = false;
        cargarAnimaciones();
    }

    private void cargarAnimaciones() {
        animIdle = new Animacion(DIR + "Idle.png", 8, true);
        animCaminar = new Animacion(DIR + "Run.png", 6, true);
        animHerido = new Animacion(DIR + "Hurt12.png", 10, false);
        animMuerto = new Animacion(DIR + "Dead.png", 6, false);
        animAtacar = new Animacion(DIR + "Attack_2.png", 6, false);
        animActual = animIdle;
    }

    @Override
    public void actualizar(PanelJuego panel) {
        Jugador jugador = panel != null ? panel.getJugador() : null;
        actualizar(panel, jugador);
    }

    public void actualizar(PanelJuego panel, Jugador jugador) {
        if (muerto) {
            animMuerto.actualizar();
            return;
        }

        if (herido) {
            animHerido.actualizar();
            if (animHerido.haTerminado()) {
                herido = false;
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

    private void decidir(Mapa mapa, Jugador jugador) {
        if (jugador == null) {
            quedarseIdle();
            return;
        }

        int centroJugadorX = jugador.getX() + jugador.getAncho() / 2;
        int centroJugadorY = jugador.getY() + jugador.getAlto() / 2;
        int centroEnemigoX = mundoX + ancho / 2;
        int centroEnemigoY = y + alto / 2;

        int dx = Math.abs(centroJugadorX - centroEnemigoX);
        int dy = Math.abs(centroJugadorY - centroEnemigoY);

        if (dx <= DISTANCIA_ATAQUE && dy <= 60) {
            miraDerecha = centroJugadorX >= centroEnemigoX;
            iniciarAtaque();
            return;
        }

        if (dx <= DISTANCIA_DETECCION && dy <= 80) {
            miraDerecha = centroJugadorX >= centroEnemigoX;

            if (enSuelo && puedeAvanzar(mapa, miraDerecha)) {
                if (miraDerecha) {
                    mundoX += VX;
                } else {
                    mundoX -= VX;
                }
            }

            animActual = animCaminar;
            return;
        }

        quedarseIdle();
    }

    private void quedarseIdle() {
        animActual = animIdle;
    }

    private void iniciarAtaque() {
        atacando = true;
        golpeAplicado = false;
        animActual = animAtacar;
        animAtacar.reiniciar();
    }

    private void actualizarAtaque(Jugador jugador) {
        animAtacar.actualizar();

        if (!golpeAplicado && animAtacar.getFrameActual() >= FRAME_GOLPE) {
            golpeAplicado = true;

            if (jugador != null && getHitboxAtaque().intersects(jugador.getHitbox())) {
                jugador.recibirDaño(DAÑO_ATAQUE);
            }
        }

        if (animAtacar.haTerminado()) {
            atacando = false;
            animActual = animIdle;
        }
    }

    private Rectangle getHitboxAtaque() {
        int anchoAtaque = 45;
        int altoAtaque = alto / 2;
        int ataqueY = y + alto / 4;

        if (miraDerecha) {
            return new Rectangle(mundoX + ancho, ataqueY, anchoAtaque, altoAtaque);
        } else {
            return new Rectangle(mundoX - anchoAtaque, ataqueY, anchoAtaque, altoAtaque);
        }
    }

    private void aplicarFisica(Mapa mapa) {
        enSuelo = false;

        vy += GRAVEDAD;
        if (vy > VY_MAX) {
            vy = VY_MAX;
        }

        y += (int) vy;

        if (mapa == null) {
            return;
        }

        int pieX1 = mundoX + 5;
        int pieX2 = mundoX + ancho - 5;
        int pieY = y + alto;

        if (mapa.esSolido(pieX1, pieY) || mapa.esSolido(pieX2, pieY)) {
            int fila = mapa.mundoAFila(pieY);
            y = fila * mapa.tamaño - alto;
            vy = 0;
            enSuelo = true;
        }
    }

    private boolean puedeAvanzar(Mapa mapa, boolean haciaDerecha) {
        if (mapa == null) {
            return true;
        }

        int siguienteX;
        if (haciaDerecha) {
            siguienteX = mundoX + VX;
        } else {
            siguienteX = mundoX - VX;
        }

        int ladoX;
        if (haciaDerecha) {
            ladoX = siguienteX + ancho;
        } else {
            ladoX = siguienteX;
        }

        int cuerpoY1 = y + 8;
        int cuerpoY2 = y + alto - 8;

        boolean pared = mapa.esSolido(ladoX, cuerpoY1) || mapa.esSolido(ladoX, cuerpoY2);
        if (pared) {
            return false;
        }

        int frenteX;
        if (haciaDerecha) {
            frenteX = siguienteX + ancho;
        } else {
            frenteX = siguienteX;
        }

        int frenteY = y + alto + 1;

        return mapa.esSolido(frenteX, frenteY);
    }

    public void recibirDaño(int daño) {
        if (muerto) {
            return;
        }

        vida -= daño;

        if (vida <= 0) {
            muerto = true;
            atacando = false;
            animActual = animMuerto;
            animMuerto.reiniciar();
        } else {
            herido = true;
            atacando = false;
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
        Graphics2D g2 = (Graphics2D) g;
        BufferedImage frame = animActual.getFrame();

        if (frame == null) {
            return;
        }

        int pantallaX = mundoX - camaraX;

        if (miraDerecha) {
            g2.drawImage(frame, pantallaX, y, ancho, alto, null);
        } else {
            g2.drawImage(frame, pantallaX + ancho, y, -ancho, alto, null);
        }
    }
}