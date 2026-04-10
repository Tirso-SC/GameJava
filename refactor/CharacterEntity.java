import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class CharacterEntity extends Entity {
    protected int speed;
    protected boolean facingRight;
    protected final ControladorAnimacion animationController;

    protected CharacterEntity(int x, int y, int width, int height, int speed) {
        super(x, y, width, height);
        this.speed = speed;
        this.facingRight = true;
        this.animationController = new ControladorAnimacion();
    }

    protected void moveLeft() {
        x -= speed;
        facingRight = false;
    }

    protected void moveRight() {
        x += speed;
        facingRight = true;
    }

    protected void play(EstadoAnimacion state) {
        animationController.play(state);
    }

    protected int getAnimationFrameIndex() {
        return animationController.getCurrentFrameIndex();
    }

    protected boolean isCurrentAnimationFinished() {
        return animationController.isCurrentAnimationFinished();
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage frame = animationController.getCurrentFrame();
        if (frame == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        int drawWidth = (int) (width * getSpriteScale());
        int drawHeight = (int) (height * getSpriteScale());
        int drawX = x + getSpriteOffsetX(drawWidth);
        int drawY = y + getSpriteOffsetY(drawHeight);

        if (facingRight) {
            g2.drawImage(frame, drawX, drawY, drawWidth, drawHeight, null);
        } else {
            g2.drawImage(frame, drawX + drawWidth + getFlipOffsetX(), drawY, -drawWidth, drawHeight, null);
        }
    }

    protected double getSpriteScale() {
        return 1.0;
    }

    protected int getSpriteOffsetX(int drawWidth) {
        return -((drawWidth - width) / 2);
    }

    protected int getSpriteOffsetY(int drawHeight) {
        return -(drawHeight - height);
    }

    protected int getFlipOffsetX() {
        return 0;
    }
}
