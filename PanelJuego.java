import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import javax.imageio.ImageIO;
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
    private int alphaFundido;
    private BufferedImage imagenMuerte;
    private BufferedImage imagenInicio;
    private int tiempoMostrandoPantalla = 0;
    private final int DURACION_PANTALLA_INICIO = 180; // 3 segundos a 60 FPS

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
        alphaFundido = 0;

        try {
            imagenMuerte = ImageIO.read(new File("Assets/Personajes/SamurayPersonajes/Samurai/SamuráiMuerto.png"));
            imagenInicio = ImageIO.read(new File("Assets/Personajes/SamurayPersonajes/Samurai/Samuraihalando.png"));
        } catch (Exception e) {
            imagenMuerte = null;
            imagenInicio = null;
        }
        
        tiempoMostrandoPantalla = DURACION_PANTALLA_INICIO;
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
        // Decrementar tiempo de pantalla de inicio
        if (tiempoMostrandoPantalla > 0) {
            tiempoMostrandoPantalla--;
        }
        
        if (nivelActual instanceof Nivel1 && nivelActual.comprobarVictoria()) {
            if (jugador.mundoX < (TAMAÑO_TILE * 48)+40) {
                jugador.mundoX += 2; // velocidad de la transicion
                jugador.miraDerecha = true;
                jugador.animActual = jugador.animCaminar;
                jugador.animActual.actualizar();
                nivelActual.actualizar(this);
                return;
            }
            if (jugador.y >450) {
                jugador.y -= 2; // velocidad de la transicion
                jugador.animActual = jugador.animEntrarNivel;
                jugador.animActual.actualizar();
                nivelActual.actualizar(this);
                return;
            }

            if (alphaFundido < 255) {
                alphaFundido += 5;
                if (alphaFundido > 255) alphaFundido = 255;
                nivelActual.actualizar(this);
                return;
            }

            jugador.animActual = jugador.animIdle;
            cambiarNivel(new Nivel2());
            jugador.mundoX = 100;
            jugador.y = LARGO - TAMAÑO_TILE * 4;
            alphaFundido = 0;
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

        // Mostrar pantalla de inicio del nivel si el tiempo no ha expirado
        if (tiempoMostrandoPantalla > 0) {
            mostrarPantallaInfo(g, imagenInicio, "Nivel", "¡Preparado!");
        }

        if (jugador.estaMuerto()) {
            mostrarPantallaInfo(g, imagenMuerte, "Has muerto", "Pulsa reiniciar para volver a empezar");
        }

        if (alphaFundido > 0) {
            g.setColor(new Color(0, 0, 0, alphaFundido));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void mostrarPantallaInfo(Graphics g, BufferedImage imagen, String textoTitulo, String textoSubtitulo) {
        Graphics2D g2 = (Graphics2D) g;

        // Banner semitransparente
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(0, 450, getWidth(), 140);

        // Imagen
        if (imagen != null) {
            g2.drawImage(imagen, 40, 140, 1536/2, 1024/2, null);
        }

        // Texto principal
        g2.setColor(Color.WHITE);
        g2.setFont(new java.awt.Font("Trebuchet MS", java.awt.Font.BOLD, 44));
        int ancho = g2.getFontMetrics().stringWidth(textoTitulo);
        g2.drawString(textoTitulo, getWidth() / 2 - ancho / 2, 510);

        // Texto secundario
        g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 22));
        ancho = g2.getFontMetrics().stringWidth(textoSubtitulo);
        g2.drawString(textoSubtitulo, getWidth() / 2 - ancho / 2, 540);
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
        tiempoMostrandoPantalla = DURACION_PANTALLA_INICIO;
    }
}
