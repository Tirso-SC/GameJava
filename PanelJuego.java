import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import javax.swing.JPanel;

public class PanelJuego extends JPanel implements Runnable {

    // Dimensiones del panel (12x8 tiles de 96px)
    final int TAMAÑO_TILE = 96;
    final int ANCHO       = TAMAÑO_TILE * 12; // 1152
    final int LARGO       = TAMAÑO_TILE * 8;  // 768
    final int FPS         = 60;
    final int FRAME_TIME  = 1000 / FPS;
    private Jugador jugador;
    private Nivel   nivelActual;

    private HashMap<Integer, Boolean> teclasPresionadas = new HashMap<>();

    public PanelJuego() {
        super();
        setBackground(Color.BLACK);
        setFocusable(true);
        inicializar();
    }

    private void inicializar() {
        nivelActual = new Nivel1();
        nivelActual.inicializar(TAMAÑO_TILE);
        
        int tamañoJugador = TAMAÑO_TILE * 2;
        jugador = new Jugador(100, LARGO - tamañoJugador * 2, tamañoJugador);
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
        if (nivelActual instanceof Nivel1 && nivelActual.comprobarVictoria()) {
            if (jugador.mundoX < TAMAÑO_TILE * 45) {
                jugador.mundoX += 2; // velocidad de la transicion
                jugador.miraDerecha = true;
                jugador.animActual = jugador.animCaminar;
                jugador.animActual.actualizar();
                nivelActual.actualizar(this);
                return;
            }
            if (jugador.y <200) {
                jugador.y += 2; // velocidad de la transicion
                jugador.animActual = jugador.animEntrarNivel;
                jugador.animActual.actualizar();
                nivelActual.actualizar(this);
                return;
            }

            jugador.animActual = jugador.animIdle;
            cambiarNivel(new Nivel2());
            jugador.mundoX = 100;
            jugador.y = LARGO - TAMAÑO_TILE * 4;
            return;
        }

        jugador.actualizar(this);
        nivelActual.actualizar(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        nivelActual.pintar(g, nivelActual.getCamaraX());
        jugador.pintar(g, nivelActual.getCamaraX());
    }

    // Métodos de teclado (igual que en tu juego del Breakout)
    public void setTeclaPresionada(int keyCode, boolean estado) {
        teclasPresionadas.put(keyCode, estado);
    }

    public boolean isTeclaPresionada(int keyCode) {
        return teclasPresionadas.getOrDefault(keyCode, false);
    }

    public Mapa getMapa() {
        return nivelActual.getMapa();
    }

    public Jugador getJugador() {
        return jugador;
    }

    public int getAncho() {
        return ANCHO;
    }

    public void cambiarNivel(Nivel nuevoNivel) {
        nivelActual = nuevoNivel;
        nivelActual.inicializar(TAMAÑO_TILE);
    }
}
