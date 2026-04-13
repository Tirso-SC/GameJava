public class ContextoJuego {
    private final int screenWidth;
    private final int screenHeight;
    private final KeyHandler keyHandler;
    private final Tilemanager tileManager;

    public ContextoJuego(int screenWidth, int screenHeight, KeyHandler keyHandler, Tilemanager tileManager) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.keyHandler = keyHandler;
        this.tileManager = tileManager;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public KeyHandler getKeyHandler() {
        return keyHandler;
    }

    public Tilemanager getTileManager() {
        return tileManager;
    }
}