import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {
    public boolean upPressed;
    public boolean downPressed;
    public boolean leftPressed;
    public boolean rightPressed;
    public boolean iPressed;

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        updateKeyState(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        updateKeyState(e.getKeyCode(), false);
    }

    private void updateKeyState(int code, boolean pressed) {
        if (code == KeyEvent.VK_W) {
            upPressed = pressed;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = pressed;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = pressed;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = pressed;
        }
        if (code == KeyEvent.VK_I) {
            iPressed = pressed;
        }
    }
}
