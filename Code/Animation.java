import java.awt.image.BufferedImage;

public class Animation {

    private BufferedImage[] frames;
    private int currentFrame;
    private int counter;
    private int speed;
    private boolean loop;

    public Animation(int speed, BufferedImage[] frames) {
        this(speed, frames, true);
    }

    public Animation(int speed, BufferedImage[] frames, boolean loop) {
        this.frames = frames;
        this.speed = speed;
        this.loop = loop;
        currentFrame = 0;
        counter = 0;
    }

    public void update() {
        counter++;

        if (counter >= speed) {
            counter = 0;
            if (loop) {
                currentFrame++;
                currentFrame %= frames.length;
            } else if (currentFrame < frames.length - 1) {
                currentFrame++;
            }
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
        return currentFrame >= frames.length - 1;
    }

    public int getCurrentFrameIndex() {
        return currentFrame;
    }

    public int getFrameCount() {
        return frames.length;
    }
}
