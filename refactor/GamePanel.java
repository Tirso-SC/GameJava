import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements Runnable {
    private static final int ORIGINAL_TILE_SIZE = 32;
    private static final int SCALE = 3;
    private static final int MAX_SCREEN_COL = 12;
    private static final int MAX_SCREEN_ROW = 12;
    private static final int PLAYER_SCALE = 2;
    private static final int FPS = 60;
    private static final int GROUND_ROW = 6;
    private static final boolean DEBUG_HITBOXES = true;

    private final int tileSize = ORIGINAL_TILE_SIZE * SCALE;
    private final int screenWidth = tileSize * MAX_SCREEN_COL;
    private final int screenHeight = tileSize * MAX_SCREEN_ROW;
    private final int playerSize = tileSize * PLAYER_SCALE;
    private final int cameraDeadZone = screenWidth / 3;

    private final KeyHandler keyHandler;
    private final Jugador player;
    private final List<Enemigo> enemies;
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
        enemies = new ArrayList<>();
        int enemySize = (int) Math.round(tileSize * 1.25);
        int enemySpeed = 2;
        int enemyY = (GROUND_ROW * tileSize) - enemySize;
        enemies.add(new Enemigo(tileSize * 5, enemyY, enemySize, enemySpeed));
        enemies.add(new Enemigo(tileSize * 10, enemyY, enemySize, enemySpeed));
        fondo = new BackgroundManager(screenWidth,screenHeight);
        suelo= new Tilemanager(tileSize);
        objetos = new Staticobjectmanajer(tileSize);
        context = new ContextoJuego(screenWidth, screenHeight, keyHandler, suelo);
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
        for (Enemigo enemy : enemies) {
            enemy.update(context);
        }
        removeDeadEnemies();
        handleAttacks();
        updateCamera();
        player.updateScreenPosition(cameraX, screenWidth);
        for (Enemigo enemy : enemies) {
            enemy.updateScreenPosition(cameraX);
        }
        fondo.setCameraX(cameraX);

    }

    private void handleAttacks() {
        if (!player.canDealAttack()) {
            return;
        }

        Rectangle attackBounds = player.getAttackBounds();
        if (attackBounds == null) {
            return;
        }

        Iterator<Enemigo> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemigo enemy = iterator.next();
            if (attackBounds.intersects(enemy.getHitboxBounds())) {
                enemy.applyDamage(1);
                player.markAttackHit();
                break;
            }
        }
    }

    private void removeDeadEnemies() {
        Iterator<Enemigo> iterator = enemies.iterator();
        while (iterator.hasNext()) {
            Enemigo enemy = iterator.next();
            if (enemy.isDeadAnimationFinished()) {
                iterator.remove();
            }
        }
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
        suelo.draw(g, cameraX);
        for (Enemigo enemy : enemies) {
            enemy.draw(g);
        }
        player.draw(g);
        if (DEBUG_HITBOXES) {
            drawDebugHitboxes(g);
        }
    }

    private void drawDebugHitboxes(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        Rectangle playerBox = player.getHitboxBounds();
        if (playerBox != null) {
            g2.setColor(Color.GREEN);
            g2.drawRect(playerBox.x - cameraX, playerBox.y, playerBox.width, playerBox.height);
        }

        Rectangle attackBox = player.getAttackBounds();
        if (attackBox != null) {
            g2.setColor(Color.ORANGE);
            g2.drawRect(attackBox.x - cameraX, attackBox.y, attackBox.width, attackBox.height);
        }

        g2.setColor(Color.RED);
        for (Enemigo enemy : enemies) {
            Rectangle enemyBox = enemy.getHitboxBounds();
            g2.drawRect(enemyBox.x - cameraX, enemyBox.y, enemyBox.width, enemyBox.height);
        }
    }
}
