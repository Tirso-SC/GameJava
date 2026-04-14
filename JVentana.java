import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class JVentana extends JFrame {

    public static void main(String[] args) {
        new JVentana();
    }

    PanelJuego panel;

    public JVentana() {
        super("2D Game");
        panel = new PanelJuego();
        panel.setBackground(java.awt.Color.BLACK);
        panel.setFocusable(true);
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                panel.setTeclaPresionada(e.getKeyCode(), true);
            }
            @Override
            public void keyReleased(KeyEvent e) {
                panel.setTeclaPresionada(e.getKeyCode(), false);
            }
        });

        this.add(panel, BorderLayout.CENTER);
        this.setSize(panel.ANCHO, panel.LARGO);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        panel.requestFocusInWindow();

        Thread hilo = new Thread(panel);
        hilo.start();
    }
}
