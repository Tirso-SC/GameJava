import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements Runnable {
    private static final int ORIGINAL_TILE_SIZE = 32;
    private static final int SCALE = 3;
    private static final int MAX_SCREEN_COL = 12;
    private static final int MAX_SCREEN_ROW = 12;
    private static final int PLAYER_SCALE = 2;
    private static final int FPS = 60;

    private final int tileSize = ORIGINAL_TILE_SIZE * SCALE;
    private final int screenWidth = tileSize * MAX_SCREEN_COL;
    private final int screenHeight = tileSize * MAX_SCREEN_ROW;
    private final int playerSize = tileSize * PLAYER_SCALE;
    private final int cameraDeadZone = screenWidth / 3;

    private final KeyHandler keyHandler;
    private final Jugador player;
    private final Timer timer;
    private final ContextoJuego context;
    private final BackgroundManager fondo;
    private final Tilemanager suelo;
    private final Staticobjectmanajer objetos;
    
    private int cameraX = 0;
    

    private Thread gameThread;

    public GamePanel() {
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        setBackground(Color.WHITE);
        setDoubleBuffered(true);
        setFocusable(true);

        keyHandler = new KeyHandler();
        addKeyListener(keyHandler);

        player = new Jugador(playerSize);
        context = new ContextoJuego(screenWidth, screenHeight, keyHandler);
        fondo = new BackgroundManager(screenWidth,screenHeight);
        suelo= new Tilemanager(tileSize);
        objetos = new Staticobjectmanajer(tileSize);
        timer = new Timer(1000 / FPS, e -> {
            updateGame();
            repaint();
        });
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        timer.start();
    }

    private void updateGame() {
        player.update(context);
        updateCamera();
        player.updateScreenPosition(cameraX, screenWidth);
        fondo.setCameraX(cameraX);

    }

    private void updateCamera() {
        int playerLeft = player.getWorldX();
        int playerRight = playerLeft + player.getWidth();

        int deadZoneLeft = cameraX + cameraDeadZone;
        int deadZoneRight = cameraX + (screenWidth - cameraDeadZone);

        if (playerLeft < deadZoneLeft) {
            cameraX = playerLeft - cameraDeadZone;
        }

        if (playerRight > deadZoneRight) {
            cameraX = playerRight - (screenWidth - cameraDeadZone);
        }

        if (cameraX < 0) {
            cameraX = 0;
        }

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        fondo.draw(g);
        objetos.draw(g, cameraX);
        suelo.draw(g);
        player.draw(g);
    }
}
