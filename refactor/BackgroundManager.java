import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class BackgroundManager {

    private static final String PATH =
        "Assets/Background/GandalfHardcore FREE Platformer Assets/GandalfHardcore Background layers/Autumn BG/Autom.png";

    // Qué tan rápido se mueve el fondo respecto al jugador (0=estático, 1=igual)
    private static final double PARALLAX_SPEED = 0.3;
    private static final double DRAW_SCALE_Y = 0.82;

    private final BufferedImage bg;   // imagen pre-escalada a pantalla completa
    private final int screenWidth;
    private final int screenHeight;
    private int cameraX = 0;

    public BackgroundManager(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        bg = loadAndScale(screenWidth, screenHeight);
    }

    private BufferedImage loadAndScale(int w, int h) {
        try {
            File f = resolve(PATH);
            BufferedImage raw = ImageIO.read(f);
            if (raw == null) { System.err.println("No se pudo leer: " + PATH); return null; }

            // Pre-escala UNA vez: altura = pantalla, ancho proporcional (≥ pantalla*2 para tiling)
            double ratio = (double) h / raw.getHeight();
            int scaledW  = (int)(raw.getWidth() * ratio);
            int scaledH  = h;

            // Si el ancho escalado no llega al doble de pantalla, ampliar para tiling suave
            if (scaledW < w * 2) {
                ratio   = (w * 2.0) / raw.getWidth();
                scaledW = w * 2;
                scaledH = (int)(raw.getHeight() * ratio);
            }

            BufferedImage dst = new BufferedImage(scaledW, scaledH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = dst.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g2.drawImage(raw, 0, 0, scaledW, scaledH, null);
            g2.dispose();

            System.out.println("Fondo cargado: " + raw.getWidth() + "x" + raw.getHeight()
                + " → " + scaledW + "x" + scaledH);
            return dst;

        } catch (Exception e) {
            System.err.println("Error cargando fondo: " + e.getMessage());
            return null;
        }
    }

    public void setCameraX(int cx) {
        this.cameraX = Math.max(0, cx);
    }

    public void draw(Graphics g) {
        if (bg == null) return;

        int imgW   = bg.getWidth();
        int offset = (int)(cameraX * PARALLAX_SPEED) % imgW;
        int drawH = (int) (bg.getHeight() * DRAW_SCALE_Y);
        int drawY = 0;

        g.drawImage(bg, -offset, drawY, imgW, drawH, null);

        // Segunda copia para tapar el hueco cuando hay desplazamiento
        if (offset > 0) {
            g.drawImage(bg, imgW - offset, drawY, imgW, drawH, null);
        }
    }

    private File resolve(String path) {
        File f = new File(path);
        if (f.exists()) return f;
        Path cur = Paths.get("").toAbsolutePath();
        for (int i = 0; i < 8 && cur != null; i++) {
            Path c = cur.resolve(path).normalize();
            if (Files.exists(c)) return c.toFile();
            cur = cur.getParent();
        }
        return f;
    }
}