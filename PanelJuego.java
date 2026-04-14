import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JPanel;

public class PanelJuego extends JPanel implements Runnable {

    // Dimensiones del panel (12x8 tiles de 96px)
    final int TAMAÑO_TILE = 96;
    final int ANCHO       = TAMAÑO_TILE * 12; // 1152
    final int LARGO       = TAMAÑO_TILE * 8;  // 768
    final int FPS         = 60;
    final int FRAME_TIME  = 1000 / FPS;

    private Jugador           jugador;
    private ArrayList<Enemigo> enemigos;
    private Mapa              mapa;
    private Fondo             fondo;
    private int               camaraX;

    private HashMap<Integer, Boolean> teclasPresionadas = new HashMap<>();

    public PanelJuego() {
        super();
        setBackground(Color.BLACK);
        setFocusable(true);
        inicializar();
    }

    private void inicializar() {
        mapa  = new Mapa(TAMAÑO_TILE);
        fondo = new Fondo(ANCHO, LARGO);

        int tamañoJugador = TAMAÑO_TILE * 2;
        jugador = new Jugador(100, LARGO - tamañoJugador * 2, tamañoJugador);

        enemigos = new ArrayList<>();
        int tamañoEnemigo = (int)(TAMAÑO_TILE * 1.5);
        enemigos.add(new Enemigo(TAMAÑO_TILE * 5,  LARGO - tamañoEnemigo * 2, tamañoEnemigo));
        enemigos.add(new Enemigo(TAMAÑO_TILE * 10, LARGO - tamañoEnemigo * 2, tamañoEnemigo));

        camaraX = 0;
    }

    @Override
    public void run() {
        while (true) {
            actualizarEstado();
            repaint();
            try {
                Thread.sleep(FRAME_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void actualizarEstado() {
        jugador.actualizar(this);

        // Actualizar enemigos y eliminar los que ya han terminado su animación de muerte
        for (int i = enemigos.size() - 1; i >= 0; i--) {
            enemigos.get(i).actualizar(this);
            if (enemigos.get(i).estaEliminado()) {
                enemigos.remove(i);
            }
        }

        checkeaAtaques();
        actualizarCamara();
    }

    private void checkeaAtaques() {
        Rectangle zonaAtaque = jugador.getZonaAtaque();
        if (zonaAtaque == null) return;

        for (Enemigo enemigo : enemigos) {
            if (zonaAtaque.intersects(enemigo.getHitbox())) {
                enemigo.recibirDaño(1);
                jugador.marcarGolpe();
                break;
            }
        }
    }

    private void actualizarCamara() {
        int zonaMuerta    = ANCHO / 3;
        int jugadorPantalla = jugador.mundoX - camaraX;

        if (jugadorPantalla < zonaMuerta) {
            camaraX = jugador.mundoX - zonaMuerta;
        }
        if (jugadorPantalla > ANCHO - zonaMuerta) {
            camaraX = jugador.mundoX - (ANCHO - zonaMuerta);
        }
        if (camaraX < 0) camaraX = 0;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        fondo.pintar(g, camaraX);
        mapa.pintar(g, camaraX);
        for (Enemigo enemigo : enemigos) {
            enemigo.pintar(g, camaraX);
        }
        jugador.pintar(g, camaraX);
    }

    // Métodos de teclado (igual que en tu juego del Breakout)
    public void setTeclaPresionada(int keyCode, boolean estado) {
        teclasPresionadas.put(keyCode, estado);
    }

    public boolean isTeclaPresionada(int keyCode) {
        return teclasPresionadas.getOrDefault(keyCode, false);
    }

    public Mapa getMapa() {
        return mapa;
    }
}
