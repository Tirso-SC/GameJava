import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public final class CargadorHojasSprites {
    private CargadorHojasSprites() {
    }

    public static Animacion loadAnimation(String spriteSheetPath, int animationSpeed, boolean loop) {
        try {
            File spriteFile = resolveSpriteFile(spriteSheetPath);
            BufferedImage sheet = ImageIO.read(spriteFile);
            if (sheet == null) {
                return null;
            }

            int frameHeight = sheet.getHeight();
            int frameWidth = frameHeight;
            int totalFrames = sheet.getWidth() / frameWidth;

            if (totalFrames <= 0) {
                return null;
            }

            BufferedImage[] frames = new BufferedImage[totalFrames];
            for (int i = 0; i < totalFrames; i++) {
                frames[i] = sheet.getSubimage(i * frameWidth, 0, frameWidth, frameHeight);
            }

            return new Animacion(animationSpeed, frames, loop);
        } catch (IOException e) {
            System.err.println("No se pudo cargar el sprite: " + spriteSheetPath);
            return null;
        }
    }

    private static File resolveSpriteFile(String spriteSheetPath) {
        File directFile = new File(spriteSheetPath);
        if (directFile.isAbsolute()) {
            return directFile;
        }

        Path current = Paths.get("").toAbsolutePath();
        for (int i = 0; i < 8 && current != null; i++) {
            Path candidate = current.resolve(spriteSheetPath).normalize();
            if (Files.exists(candidate)) {
                return candidate.toFile();
            }
            current = current.getParent();
        }

        return directFile;
    }
}