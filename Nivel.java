import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

public abstract class Nivel {

    protected Mapa mapa;
    protected Fondo fondo;
    protected ArrayList<ObjetoEstatico> decoraciones;
    protected ArrayList<Enemigo> enemigos;
    protected int camaraX;
    protected boolean completado;

    public Nivel() {
        this.camaraX = 0;
        this.completado = false;
        this.enemigos = new ArrayList<>();
        this.decoraciones = new ArrayList<>();
    }

    public abstract void inicializar(int tamaño);

    public void actualizar(PanelJuego panel) {
        Jugador jugador = panel.getJugador();
        
        for (int i = enemigos.size() - 1; i >= 0; i--) {
            enemigos.get(i).actualizar(panel);
            if (enemigos.get(i).estaEliminado()) {
                enemigos.remove(i);
            }
        }

        checkeaAtaques(jugador);
        actualizarCamara(panel.getJugador(), panel.getAncho());
    }

    public void pintar(Graphics g, int camaraX) {
        fondo.pintar(g, camaraX);
        
        for (ObjetoEstatico decoracion : decoraciones) {
            decoracion.pintar(g, camaraX);
        }
        
        mapa.pintar(g, camaraX);
        for (Enemigo enemigo : enemigos) {
            enemigo.pintar(g, camaraX);
        }
    }

    private void checkeaAtaques(Jugador jugador) {
        Rectangle zonaAtaque = jugador.getZonaAtaque();
        if (zonaAtaque == null) return;

        for (Enemigo enemigo : enemigos) {
            if (zonaAtaque.intersects(enemigo.getHitbox())) {
                enemigo.recibirDaño(jugador.getDañoAtaque());
                jugador.marcarGolpe();
                break;
            }
        }
    }

    private void actualizarCamara(Jugador jugador, int anchoPanel) {
        int zonaMuerta    = anchoPanel / 3;
        int jugadorPantalla = jugador.mundoX - camaraX;
        int camaraMaxX = mapa.getAnchoPíxeles() - anchoPanel;

        if (jugadorPantalla < zonaMuerta) {
            camaraX = jugador.mundoX - zonaMuerta;
        }
        if (jugadorPantalla > anchoPanel - zonaMuerta) {
            camaraX = jugador.mundoX - (anchoPanel - zonaMuerta);
        }
        if (camaraX < 0) camaraX = 0;
        if (camaraMaxX < 0) camaraMaxX = 0;
        if (camaraX > camaraMaxX) camaraX = camaraMaxX;
    }

    public boolean comprobarVictoria() {
        return enemigos.isEmpty();
    }

    public boolean estaCompletado() {
        return completado;
    }

    public Mapa getMapa() {
        return mapa;
    }

    public int getCamaraX() {
        return camaraX;
    }

    public ArrayList<Enemigo> getEnemigos() {
        return enemigos;
    }
}
