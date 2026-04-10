import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements Runnable {
    private static final int ORIGINAL_TILE_SIZE = 32;
    private static final int SCALE = 3;
    private static final int MAX_SCREEN_COL = 16;
    private static final int MAX_SCREEN_ROW = 12;
    private static final int PLAYER_SCALE = 2;
    private static final int FPS = 60;

    private final int tileSize = ORIGINAL_TILE_SIZE * SCALE;
    private final int screenWidth = tileSize * MAX_SCREEN_COL;
    private final int screenHeight = tileSize * MAX_SCREEN_ROW;
    private final int playerSize = tileSize * PLAYER_SCALE;

    private final KeyHandler keyHandler;
    private final Jugador player;
    private final Timer timer;
    private final ContextoJuego context;

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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.draw(g);
    }
}
