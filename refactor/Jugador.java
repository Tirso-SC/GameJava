public class Jugador extends CharacterEntity {
    private static final int DEFAULT_X = 100;
    private static final int DEFAULT_Y = 100;
    private static final int DEFAULT_SPEED = 4;
    private static final int ANIMATION_SPEED = 10;

    private static final String SPRITES_DIR = "Assets/Personajes/SamurayPersonajes/Samurai";
    private static final String IDLE_PATH = SPRITES_DIR + "/Idle.png";
    private static final String WALK_PATH = SPRITES_DIR + "/Walk.png";
    private static final String ATTACK_PATH = SPRITES_DIR + "/Attack.png";
    private static final String JUMP_PATH = SPRITES_DIR + "/Jump.png";

    private static final double JUMP_START_VELOCITY = -14.0;
    private static final double GRAVITY = 0.70;
    private static final double MAX_FALL_SPEED = 14.0;
    private static final int JUMP_TAKEOFF_FRAME = 2;
    private static final int JUMP_LANDING_FRAME = 8;
    private static final int SPRITE_OFFSET_X = -27;

    private boolean jumping;
    private boolean jumpStarted;
    private boolean landingCompleted;
    private boolean previousJumpPressed;
    private double verticalSpeed;
    private double yFloat;
    private int groundY;

    public Jugador(int size) {
        super(DEFAULT_X, DEFAULT_Y, size, size, DEFAULT_SPEED);
        this.yFloat = y;
        this.groundY = y;
        loadAnimations();
    }

    private void loadAnimations() {
        addAnimation(EstadoAnimacion.IDLE, IDLE_PATH, true);
        addAnimation(EstadoAnimacion.WALK, WALK_PATH, true);
        addAnimation(EstadoAnimacion.ATTACK, ATTACK_PATH, true);
        addAnimation(EstadoAnimacion.JUMP, JUMP_PATH, false);
        play(EstadoAnimacion.IDLE);
    }

    private void addAnimation(EstadoAnimacion state, String path, boolean loop) {
        Animacion animation = CargadorHojasSprites.loadAnimation(path, ANIMATION_SPEED, loop);
        if (animation != null) {
            animationController.add(state, animation);
        }
    }

    @Override
    public void update(ContextoJuego context) {
        KeyHandler keys = context.getKeyHandler();

        updateHorizontalMovement(keys, context.getScreenWidth());
        updateJumpInput(keys);
        updateVerticalPhysics(context.getScreenHeight());
        updateAnimationState(keys);
        animationController.update();
    }

    private void updateHorizontalMovement(KeyHandler keys, int screenWidth) {
        if (keys.leftPressed) {
            moveLeft();
        }
        if (keys.rightPressed) {
            moveRight();
        }
        clampHorizontal(screenWidth);
    }

    private void updateJumpInput(KeyHandler keys) {
        boolean jumpPressed = keys.upPressed;

        if (jumpPressed && !previousJumpPressed && !jumping) {
            jumping = true;
            jumpStarted = false;
            landingCompleted = false;
            verticalSpeed = 0;
            yFloat = groundY;
            play(EstadoAnimacion.JUMP);
        }

        previousJumpPressed = jumpPressed;
    }

    private void updateVerticalPhysics(int screenHeight) {
        if (!jumping) {
            groundY = Math.min(groundY, screenHeight - height);
            y = groundY;
            yFloat = y;
            return;
        }

        if (!jumpStarted && !landingCompleted && getAnimationFrameIndex() >= JUMP_TAKEOFF_FRAME) {
            jumpStarted = true;
            verticalSpeed = JUMP_START_VELOCITY;
        }

        if (!jumpStarted) {
            y = groundY;
            yFloat = y;
            return;
        }

        yFloat += verticalSpeed;
        verticalSpeed += GRAVITY;

        if (verticalSpeed > MAX_FALL_SPEED) {
            verticalSpeed = MAX_FALL_SPEED;
        }

        if (yFloat >= groundY) {
            yFloat = groundY;
            verticalSpeed = 0;
            jumpStarted = false;
            landingCompleted = true;
        }

        y = (int) Math.round(yFloat);
    }

    private void updateAnimationState(KeyHandler keys) {
        boolean moving = keys.leftPressed || keys.rightPressed;

        if (jumping) {
            play(EstadoAnimacion.JUMP);

            boolean onGround = y >= groundY;
            boolean landingPoseReached = getAnimationFrameIndex() >= JUMP_LANDING_FRAME;
            if (landingCompleted && onGround && landingPoseReached && isCurrentAnimationFinished()) {
                jumping = false;
                landingCompleted = false;
            }
            return;
        }

        if (moving) {
            play(EstadoAnimacion.WALK);
        } else {
            play(EstadoAnimacion.IDLE);
        }
    }

    @Override
    protected int getSpriteOffsetX(int drawWidth) {
        return super.getSpriteOffsetX(drawWidth) + SPRITE_OFFSET_X;
    }
}