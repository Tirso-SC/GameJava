import java.awt.image.BufferedImage;

public class Animacion {
    private final BufferedImage[] frames;
    private final int speed;
    private final boolean loop;

    private int currentFrame;
    private int counter;

    public Animacion(int speed, BufferedImage[] frames) {
        this(speed, frames, true);
    }

    public Animacion(int speed, BufferedImage[] frames, boolean loop) {
        if (frames == null || frames.length == 0) {
            throw new IllegalArgumentException("Animation needs at least one frame.");
        }
        this.frames = frames;
        this.speed = Math.max(1, speed);
        this.loop = loop;
        this.currentFrame = 0;
        this.counter = 0;
    }

    public void update() {
        counter++;
        if (counter < speed) {
            return;
        }

        counter = 0;

        if (loop) {
            currentFrame = (currentFrame + 1) % frames.length;
        } else if (currentFrame < frames.length - 1) {
            currentFrame++;
        }
    }

    public BufferedImage getFrame() {
        return frames[currentFrame];
    }

    public void reset() {
        currentFrame = 0;
        counter = 0;
    }

    public boolean isFinished() {
        return !loop && currentFrame >= frames.length - 1;
    }

    public int getCurrentFrameIndex() {
        return currentFrame;
    }
}