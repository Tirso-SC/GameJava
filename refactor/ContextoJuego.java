public class ContextoJuego {
    private final int screenWidth;
    private final int screenHeight;
    private final KeyHandler keyHandler;

    public ContextoJuego(int screenWidth, int screenHeight, KeyHandler keyHandler) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.keyHandler = keyHandler;
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
}