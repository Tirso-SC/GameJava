import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Player extends Objeto {

    private int animationSpeed = 10;
    private final double jumpStartVelocity = -14.0;
    private final double gravity = 0.70;
    private final double maxFallSpeed = 14.0;
    private final int jumpTakeoffFrame = 2;
    private final int jumpLandingFrame = 8;

    private Animation idleAnimation;
    private Animation walkAnimation;
    private Animation jumpAnimation;

    private Animation currentAnimation;
    private String currentAnimationName = "idle";

    private KeyHandler keyH;
    private boolean isJumping;
    private boolean hasTakenOff;
    private boolean jumpHasLanded;
    private boolean previousJumpPressed;
    private double verticalSpeed;
    private double yFloat;
    private int groundY;
    private boolean facingRight = true;
    private final int spriteOffsetX = -27;
    private final int flipOffsetX = 0;

    public Player(int tileSize, KeyHandler keyH) {
        super(100, 100, 4, tileSize, tileSize, Color.white);
        this.keyH = keyH;
        this.yFloat = y;
        this.groundY = y;
        loadSprites();
    }

    // =============================
    // CARGA DE SPRITES
    // =============================
    private void loadSprites() {
        addAnimation(
            "idle",
            "C:\\Users\\Tirso\\OneDrive - Universidad Pontificia Comillas\\Documentos\\ICAI\\Curso2.0.2\\progrmación\\GameJavaProject\\Assets\\Personajes\\SamurayPersonajes\\Samurai\\Idle.png"
        );

        addAnimation(
            "walk",
            "C:\\Users\\Tirso\\OneDrive - Universidad Pontificia Comillas\\Documentos\\ICAI\\Curso2.0.2\\progrmación\\GameJavaProject\\Assets\\Personajes\\SamurayPersonajes\\Samurai\\Walk.png"
        );
        addAnimation(
            "attack",
            "C:\\Users\\Tirso\\OneDrive - Universidad Pontificia Comillas\\Documentos\\ICAI\\Curso2.0.2\\progrmación\\GameJavaProject\\Assets\\Personajes\\SamurayPersonajes\\Samurai\\Attack.png"
        );
        addAnimation(
            "jump",
            "C:\\Users\\Tirso\\OneDrive - Universidad Pontificia Comillas\\Documentos\\ICAI\\Curso2.0.2\\progrmación\\GameJavaProject\\Assets\\Personajes\\SamurayPersonajes\\Samurai\\Jump.png",
            false
        );
    }

    public void addAnimation(String name, String spriteSheetPath) {
        addAnimation(name, spriteSheetPath, true);
    }

    public void addAnimation(String name, String spriteSheetPath, boolean loop) {
        Animation animation = buildAnimation(spriteSheetPath, loop);

        if (animation == null) {
            return;
        }

        String key = name.toLowerCase();

        if (key.equals("idle")) {
            idleAnimation = animation;
        } else if (key.equals("walk")) {
            walkAnimation = animation;
        } else if (key.equals("jump")) {
            jumpAnimation = animation;
        } else {
            return;
        }

        if (currentAnimation == null) {
            currentAnimation = animation;
            currentAnimationName = key;
        }
    }

    private Animation buildAnimation(String spriteSheetPath, boolean loop) {
        try {
            BufferedImage sheet = ImageIO.read(new File(spriteSheetPath));

            int frameHeight = sheet.getHeight();
            int frameWidth = frameHeight;
            int totalFrames = sheet.getWidth() / frameWidth;

            if (totalFrames <= 0) {
                return null;
            }

            BufferedImage[] frames = new BufferedImage[totalFrames];

            for (int i = 0; i < totalFrames; i++) {
                frames[i] = sheet.getSubimage(
                    i * frameWidth,
                    0,
                    frameWidth,
                    frameHeight
                );
            }

            return new Animation(animationSpeed, frames, loop);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setAnimation(String name) {
        String key = name.toLowerCase();

        if (key.equals(currentAnimationName)) {
            return;
        }

        Animation next = null;

        if (key.equals("idle")) {
            next = idleAnimation;
        } else if (key.equals("walk")) {
            next = walkAnimation;
        } else if (key.equals("jump")) {
            next = jumpAnimation;
        }

        if (next == null) {
            return;
        }

        currentAnimation = next;
        currentAnimationName = key;
        currentAnimation.reset();
    }

    // =============================
    // UPDATE
    // =============================
    public void update(int screenWidth, int screenHeight) {
        updateHorizontalMovement(screenWidth);
        updateJumpInput();
        updateVerticalPhysics(screenHeight);
        updateAnimationState();

        if (currentAnimation != null) {
            currentAnimation.update();
        }
    }

    private void updateHorizontalMovement(int screenWidth) {
        if (keyH.leftPressed && !keyH.rightPressed) {
            facingRight = false;
        } else if (keyH.rightPressed && !keyH.leftPressed) {
            facingRight = true;
        }

        if (keyH.leftPressed) {
            x -= speed;
        }
        if (keyH.rightPressed) {
            x += speed;
        }

        if (x < 0) {
            x = 0;
        }
        if (x + width > screenWidth) {
            x = screenWidth - width;
        }
    }

    private void updateJumpInput() {
        boolean jumpPressed = keyH.upPressed;

        if (jumpPressed && !previousJumpPressed && !isJumping) {
            isJumping = true;
            hasTakenOff = false;
            jumpHasLanded = false;
            verticalSpeed = 0;
            yFloat = groundY;
            setAnimation("jump");
        }

        previousJumpPressed = jumpPressed;
    }

    private void updateVerticalPhysics(int screenHeight) {
        if (!isJumping) {
            groundY = Math.min(groundY, screenHeight - height);
            y = groundY;
            yFloat = y;
            return;
        }

        int currentJumpFrame = jumpAnimation != null ? jumpAnimation.getCurrentFrameIndex() : 0;
        if (!hasTakenOff && !jumpHasLanded && currentJumpFrame >= jumpTakeoffFrame) {
            hasTakenOff = true;
            verticalSpeed = jumpStartVelocity;
        }

        if (!hasTakenOff) {
            y = groundY;
            yFloat = y;
            return;
        }

        yFloat += verticalSpeed;
        verticalSpeed += gravity;

        if (verticalSpeed > maxFallSpeed) {
            verticalSpeed = maxFallSpeed;
        }

        if (yFloat >= groundY) {
            yFloat = groundY;
            verticalSpeed = 0;
            hasTakenOff = false;
            jumpHasLanded = true;
        }

        y = (int) Math.round(yFloat);
    }

    private void updateAnimationState() {
        boolean moving = keyH.leftPressed || keyH.rightPressed;

        if (isJumping) {
            setAnimation("jump");

            boolean onGround = y >= groundY;
            int currentJumpFrame = jumpAnimation != null ? jumpAnimation.getCurrentFrameIndex() : 0;
            boolean landingPoseReached = currentJumpFrame >= jumpLandingFrame;
            if (jumpHasLanded && onGround && landingPoseReached && currentAnimation != null && currentAnimation.isFinished()) {
                isJumping = false;
                jumpHasLanded = false;
            }
            return;
        }

        if (moving) {
            setAnimation("walk");
        } else {
            setAnimation("idle");
        }
    }

    // =============================
    // DIBUJADO
    // =============================

@Override
public void pintar(Graphics g) {
    if (currentAnimation == null) {
        return;
    }

    BufferedImage frame = currentAnimation.getFrame();
    if (frame == null) {
        return;
    }

    Graphics2D g2 = (Graphics2D) g;

    double scale = getScale();
    int drawWidth = (int) (width * scale);
    int drawHeight = (int) (height * scale);

    int drawX = x + getOffsetX(drawWidth);
    int drawY = y + getOffsetY(drawHeight);

    if (!facingRight) {
        g2.drawImage(
            frame,
            drawX + drawWidth + flipOffsetX,
            drawY,
            -drawWidth,
            drawHeight,
            null
        );
    } else {
        g2.drawImage(
            frame,
            drawX,
            drawY,
            drawWidth,
            drawHeight,
            null
        );
    }
}

private double getScale() {
    return 1.0;
}

private int getOffsetX(int drawWidth) {
    return -((drawWidth - width) / 2) + spriteOffsetX;
}

private int getOffsetY(int drawHeight) {
    return -(drawHeight - height);
}
}