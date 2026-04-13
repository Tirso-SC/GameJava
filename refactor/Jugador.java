public class Jugador extends CharacterEntity {
    private static final int DEFAULT_X = 100;
    private static final int DEFAULT_Y = 383;
    private static final int DEFAULT_SPEED = 7;
    private static final int ANIMATION_SPEED = 5;

    private static final String SPRITES_DIR = "Assets/Personajes/SamurayPersonajes/Samurai";
    private static final String IDLE_PATH = SPRITES_DIR + "/Idle.png";
    private static final String WALK_PATH = SPRITES_DIR + "/Walk.png";
    private static final String ATTACK_PATH = SPRITES_DIR + "/Attack_1.png";
    private static final String JUMP_PATH = SPRITES_DIR + "/Jump.png";

    private static final double JUMP_START_VELOCITY = -14.0;
    private static final double GRAVITY = 0.70;
    private static final double MAX_FALL_SPEED = 14.0;
    private static final int JUMP_TAKEOFF_FRAME = 2;
    private static final int JUMP_LANDING_FRAME = 8;
    private static final int SPRITE_OFFSET_X = 0;

    private boolean jumping;
    private boolean attacking;
    private boolean previousJumpPressed;
    private boolean previousAttackPressed;
    private double verticalSpeed;
    private double yFloat;
    private boolean onGround;
    private int worldX;
    private int hitboxSize;
    private int hitboxWidth;
    private int hitboxHeight;
    private int hitboxOffsetX;
    private int hitboxOffsetY;

    public Jugador(int size) {
        super(DEFAULT_X, DEFAULT_Y, size, size, DEFAULT_SPEED);
        this.worldX = DEFAULT_X;
        this.yFloat = y;
        this.onGround = false;
        loadAnimations();
    }

    private void loadAnimations() {
        addAnimation(EstadoAnimacion.IDLE, IDLE_PATH, true);
        addAnimation(EstadoAnimacion.WALK, WALK_PATH, true);
        addAnimation(EstadoAnimacion.ATTACK, ATTACK_PATH, false);
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
        Tilemanager tiles = context.getTileManager();

        ensureHitbox(tiles);

        updateHorizontalMovement(keys, tiles);
        updateJumpInput(keys);
        updateAttackInput(keys);
        updateVerticalPhysics(context.getScreenHeight(), tiles);
        updateAnimationState(keys);
        animationController.update();
    }

    private void updateHorizontalMovement(KeyHandler keys, Tilemanager tiles) {
        if (tiles == null) {
            return;
        }

        int dx = 0;

        if (keys.leftPressed) {
            dx -= speed;
        }
        if (keys.rightPressed) {
            dx += speed;
        }

        if (dx == 0) {
            return;
        }

        facingRight = dx > 0;

        int boxLeft = worldX + hitboxOffsetX;
        int boxRight = boxLeft + hitboxWidth - 1;
        int boxTop = y + hitboxOffsetY;
        int boxBottom = boxTop + hitboxHeight - 1;
        int topRow = tiles.worldToRow(boxTop);
        int bottomRow = tiles.worldToRow(boxBottom);

        if (dx < 0) {
            int newBoxLeft = boxLeft + dx;
            int col = tiles.worldToCol(newBoxLeft);
            if (hasSolidInColumn(tiles, col, topRow, bottomRow)) {
                int tileRight = col * tiles.getTileSize() + tiles.getTileSize();
                int resolvedBoxLeft = tileRight;
                worldX = resolvedBoxLeft - hitboxOffsetX;
                if (verticalSpeed < 0) {
                    verticalSpeed = 0;
                }
            } else {
                worldX += dx;
            }
        } else {
            int newBoxRight = boxRight + dx;
            int col = tiles.worldToCol(newBoxRight);
            if (hasSolidInColumn(tiles, col, topRow, bottomRow)) {
                int tileLeft = col * tiles.getTileSize();
                int resolvedBoxLeft = tileLeft - hitboxWidth;
                worldX = resolvedBoxLeft - hitboxOffsetX;
                if (verticalSpeed < 0) {
                    verticalSpeed = 0;
                }
            } else {
                worldX += dx;
            }
        }

        clampWorldX(tiles);
    }
    private void updateAttackInput(KeyHandler keys) {
        boolean attackPressed = keys.iPressed;

        if (attackPressed && !previousAttackPressed && !jumping && !attacking) {
            attacking = true;
            play(EstadoAnimacion.ATTACK);
        }

        previousAttackPressed = attackPressed;

        if (attacking && isCurrentAnimationFinished()) {
            attacking = false;
        }
    }
    private void updateJumpInput(KeyHandler keys) {
        boolean jumpPressed = keys.upPressed;

        if (jumpPressed && !previousJumpPressed && onGround) {
            onGround = false;
            jumping = true;
            verticalSpeed = JUMP_START_VELOCITY;
            yFloat = y;
            play(EstadoAnimacion.JUMP);
        }

        previousJumpPressed = jumpPressed;
    }

    private void updateVerticalPhysics(int screenHeight, Tilemanager tiles) {
        if (tiles == null) {
            return;
        }

        if (!onGround) {
            verticalSpeed += GRAVITY;
            if (verticalSpeed > MAX_FALL_SPEED) {
                verticalSpeed = MAX_FALL_SPEED;
            }
        }

        double newY = yFloat + verticalSpeed;
        int boxLeft = worldX + hitboxOffsetX;
        int boxRight = boxLeft + hitboxWidth - 1;
        int leftCol = tiles.worldToCol(boxLeft);
        int rightCol = tiles.worldToCol(boxRight);

        if (verticalSpeed < 0) {
            int newBoxTop = (int) Math.floor(newY) + hitboxOffsetY;
            int row = tiles.worldToRow(newBoxTop);

            if (hasSolidInRow(tiles, row, leftCol, rightCol)) {
                int tileBottom = (row + 1) * tiles.getTileSize();
                int resolvedBoxTop = tileBottom;
                yFloat = resolvedBoxTop - hitboxOffsetY;
                verticalSpeed = 0;
                onGround = false;
                jumping = true;
            } else {
                yFloat = newY;
                onGround = false;
                jumping = true;
            }
        } else if (verticalSpeed > 0) {
            int newBoxBottom = (int) Math.floor(newY) + hitboxOffsetY + hitboxHeight - 1;
            int row = tiles.worldToRow(newBoxBottom);

            if (hasSolidInRow(tiles, row, leftCol, rightCol)) {
                int tileTop = row * tiles.getTileSize();
                int resolvedBoxBottom = tileTop - 1;
                int resolvedBoxTop = resolvedBoxBottom - hitboxHeight + 1;
                yFloat = resolvedBoxTop - hitboxOffsetY;
                verticalSpeed = 0;
                onGround = true;
                jumping = false;
            } else {
                yFloat = newY;
                onGround = false;
                jumping = true;
            }
        } else {
            int belowY = (int) Math.floor(yFloat) + hitboxOffsetY + hitboxHeight;
            int row = tiles.worldToRow(belowY);

            if (hasSolidInRow(tiles, row, leftCol, rightCol)) {
                onGround = true;
                jumping = false;
            } else {
                onGround = false;
                jumping = true;
            }
        }

        int maxY = screenHeight - height;
        if (yFloat > maxY) {
            yFloat = maxY;
            verticalSpeed = 0;
            onGround = true;
            jumping = false;
        }

        y = (int) Math.round(yFloat);
    }

    private void updateAnimationState(KeyHandler keys) {
        boolean moving = keys.leftPressed || keys.rightPressed;

        if (jumping) {
            play(EstadoAnimacion.JUMP);
            return;
        }

        if (attacking) {
            play(EstadoAnimacion.ATTACK);
        } else if (moving) {
            play(EstadoAnimacion.WALK);
        } else {

            play(EstadoAnimacion.IDLE);
        }
    }

    @Override
    protected int getSpriteOffsetX(int drawWidth) {
        return super.getSpriteOffsetX(drawWidth) + SPRITE_OFFSET_X;
    }

    public int getWorldX() {
        return worldX;
    }

    public void updateScreenPosition(int cameraX, int screenWidth) {
        x = worldX - cameraX;
        clampHorizontal(screenWidth);
    }

    private void clampWorldX(Tilemanager tiles) {
        if (worldX < 0) {
            worldX = 0;
        }

        if (tiles == null) {
            return;
        }

        if (hitboxWidth > 0) {
            int maxX = tiles.getMapPixelWidth() - hitboxWidth - hitboxOffsetX;
            if (maxX >= 0 && worldX > maxX) {
                worldX = maxX;
            }
        }
    }

    private boolean hasSolidInColumn(Tilemanager tiles, int col, int startRow, int endRow) {
        if (tiles == null) {
            return false;
        }
        int from = Math.min(startRow, endRow);
        int to = Math.max(startRow, endRow);
        for (int row = from; row <= to; row++) {
            if (tiles.isSolidTile(row, col)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasSolidInRow(Tilemanager tiles, int row, int startCol, int endCol) {
        if (tiles == null) {
            return false;
        }
        int from = Math.min(startCol, endCol);
        int to = Math.max(startCol, endCol);
        for (int col = from; col <= to; col++) {
            if (tiles.isSolidTile(row, col)) {
                return true;
            }
        }
        return false;
    }

    private void ensureHitbox(Tilemanager tiles) {
        if (tiles == null) {
            return;
        }
        int size = tiles.getTileSize();
        if (size <= 0) {
            return;
        }
        if (hitboxSize == size && hitboxWidth > 0) {
            return;
        }

        hitboxSize = size;
        hitboxWidth = Math.max(1, (int) Math.round(size * 0.75));
        hitboxHeight = size;
        hitboxOffsetX = (width - hitboxWidth) / 2;
        hitboxOffsetY = height - hitboxHeight;
    }
}