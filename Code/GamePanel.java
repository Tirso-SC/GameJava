
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.Timer;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    // SCREEN SETTINGS
    final int originalTileSize = 32; // 16x16 tile
    final int scale = 3;

    final int tileSize = originalTileSize * scale; // 48x48 tile
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; // 768 pixels
    final int screenHeight = tileSize * maxScreenRow; // 576 pixels
    final int playerScale = 2;
    final int playerSize = tileSize * playerScale;

    // FPS
    int FPS = 60;

    Thread gameThread;
    KeyHandler keyHandler;
    Player player;
    Timer timer;


    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.white);
        this.setDoubleBuffered(true);
        
        keyHandler = new KeyHandler();
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        player = new Player(playerSize,keyHandler);

        timer = new Timer(1000 / FPS, e -> {
            update();
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

    public void update() {
        player.update(screenWidth, screenHeight);
    }



    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        player.pintar(g);
    }
}