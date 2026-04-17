import java.awt.Rectangle;

public class EnemigoJefe extends Enemigo {

    final int VX                  = 3;
    final int DAÑO                = 3;
    final int DISTANCIA_DETECCION = 400;
    final int DISTANCIA_ATAQUE    = 150;

    static final String DIR = "Assets/Personajes/Jefe1/";

    public EnemigoJefe(int mundoX, int y, int tamaño) {
        super(mundoX, y, tamaño, tamaño, 8);
    }

    @Override
    protected void cargarAnimaciones() {
        System.out.println("Cargando animaciones del jefe desde: " + DIR);
        animIdle    = new Animacion(DIR + "Idle.png",    8, true);
        animCaminar = new Animacion(DIR + "Walk.png",    6, true);
        animHerido  = new Animacion(DIR + "Hurt.png",   10, false);
        animMuerto  = new Animacion(DIR + "Dead.png",    8, false);
        animAtacar  = new Animacion(DIR + "Attack_1.png",  8, false);
        animActual  = animIdle;
        System.out.println("Animaciones cargadas. Idle frame: " + animIdle.getFrame());
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
        int distancia = (int) Math.sqrt(dx * dx + dy * dy);

        miraDerecha = jcx >= cx;

        if (distancia <= DISTANCIA_ATAQUE) {
            iniciarAtaque();
            return;
        }

        // El jefe siempre persigue al jugador si lo detecta
        if (distancia <= DISTANCIA_DETECCION) {
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
        int w = 120;
        int h = (alto * 2) / 3;
        int ay = y + alto / 6;
        return miraDerecha
            ? new Rectangle(mundoX + ancho / 2, ay, w, h)
            : new Rectangle(mundoX - w + ancho / 2, ay, w, h);
    }

    @Override
    protected int getFrameGolpe() {
        return 4;
    }

    @Override
    public int getDaño() {
        return DAÑO;
    }
}