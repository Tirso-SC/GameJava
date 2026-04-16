import java.awt.Rectangle;

public class EnemigoBasico extends Enemigo {

    final int VX                 = 2;
    final int VIDA_INICIAL       = 2;
    final int DAÑO               = 1;
    final int DISTANCIA_DETECCION = 220;
    final int DISTANCIA_ATAQUE   = 70;

    static final String DIR = "Assets/Personajes/craftpix-net-407836-free-ninja-sprite-sheets-pixel-art/Ninja_Peasant/";

    public EnemigoBasico(int mundoX, int y, int tamaño) {
        super(mundoX, y, tamaño, tamaño, 2);
    }

    @Override
    protected void cargarAnimaciones() {
        animIdle    = new Animacion(DIR + "Idle.png",      8, true);
        animCaminar = new Animacion(DIR + "Run.png",       6, true);
        animHerido  = new Animacion(DIR + "Hurt12.png",   10, false);
        animMuerto  = new Animacion(DIR + "Dead.png",      6, false);
        animAtacar  = new Animacion(DIR + "Attack_2.png",  6, false);
        animActual  = animIdle;
    }

    @Override
    protected void decidir(Mapa mapa, Jugador jugador) {
        if (jugador == null) {
            animActual = animIdle;
            return;
        }

        int cx  = mundoX + ancho / 2;
        int cy  = y + alto / 2;
        int jcx = jugador.getX() + jugador.getAncho() / 2;
        int jcy = jugador.getY() + jugador.getAlto() / 2;
        int dx  = Math.abs(jcx - cx);
        int dy  = Math.abs(jcy - cy);

        miraDerecha = jcx >= cx;

        if (dx <= DISTANCIA_ATAQUE && dy <= 60) {
            iniciarAtaque();
            return;
        }

        if (dx <= DISTANCIA_DETECCION && dy <= 80) {
            if (enSuelo && puedeAvanzar(mapa, miraDerecha, VX)) {
                mundoX += miraDerecha ? VX : -VX;
            }
            animActual = animCaminar;
            return;
        }

        animActual = animIdle;
    }

    @Override
    public Rectangle getHitboxAtaque() {
        int w = 45;
        int h = alto / 2;
        int ay = y + alto / 4;
        if (miraDerecha) {
            return new Rectangle(mundoX + ancho, ay, w, h);
        } else {
            return new Rectangle(mundoX - w, ay, w, h);
        }
    }

    @Override
    public int getDaño() {
        return DAÑO;
    }
}