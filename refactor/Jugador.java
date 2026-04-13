import java.awt.Rectangle;

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
    private static final int IDLE_SPRITE_OFFSET_X_LEFT = -40;
    private static final int IDLE_SPRITE_OFFSET_X_RIGHT = 40;
    private static final int IDLE_SPRITE_OFFSET_Y = 0;

    private boolean jumping;
    private boolean attacking;
    private boolean previousJumpPressed;
    private boolean previousAttackPressed;
    private boolean attackHitApplied;
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
        Animacion idle = CargadorHojasSprites.loadAnimation(IDLE_PATH, ANIMATION_SPEED, true);
        Animacion walk = CargadorHojasSprites.loadAnimation(WALK_PATH, ANIMATION_SPEED, true);
        Animacion attack = CargadorHojasSprites.loadAnimation(ATTACK_PATH, ANIMATION_SPEED, false);
        Animacion jump = CargadorHojasSprites.loadAnimation(JUMP_PATH, ANIMATION_SPEED, false);

        if (idle != null) {
            animationController.add(EstadoAnimacion.IDLE, idle);
        }
        if (walk != null) {
            animationController.add(EstadoAnimacion.WALK, walk);
        }
        if (attack != null) {
            animationController.add(EstadoAnimacion.ATTACK, attack);
        }
        if (jump != null) {
            animationController.add(EstadoAnimacion.JUMP, jump);
        }
        play(EstadoAnimacion.IDLE);
    }

    @Override
    public void update(ContextoJuego context) {
        KeyHandler keys = context.getKeyHandler();
        Tilemanager tiles = context.getTileManager();

        ensureHitbox(tiles);

        updateHorizontalMovement(keys, tiles);

        boolean jumpPressed = keys.upPressed;
        if (jumpPressed && !previousJumpPressed && onGround) {
            onGround = false;
            jumping = true;
            verticalSpeed = JUMP_START_VELOCITY;
            yFloat = y;
            play(EstadoAnimacion.JUMP);
        }
        previousJumpPressed = jumpPressed;

        boolean attackPressed = keys.iPressed;
        if (attackPressed && !previousAttackPressed && !jumping && !attacking) {
            attacking = true;
            attackHitApplied = false;
            play(EstadoAnimacion.ATTACK);
        }
        previousAttackPressed = attackPressed;

        if (attacking && isCurrentAnimationFinished()) {
            attacking = false;
            attackHitApplied = false;
        }

        updateVerticalPhysics(context.getScreenHeight(), tiles);

        boolean moving = keys.leftPressed || keys.rightPressed;
        if (jumping) {
            play(EstadoAnimacion.JUMP);
        } else if (attacking) {
            play(EstadoAnimacion.ATTACK);
        } else if (moving) {
            play(EstadoAnimacion.WALK);
        } else {
            play(EstadoAnimacion.IDLE);
        }

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

    public int getWorldX() {
        return worldX;
    }

    public void updateScreenPosition(int cameraX, int screenWidth) {
        x = worldX - cameraX;
        clampHorizontal(screenWidth);
    }

    public boolean canDealAttack() {
        return attacking && !attackHitApplied;
    }

    public void markAttackHit() {
        attackHitApplied = true;
    }

    public Rectangle getAttackBounds() {
        if (!attacking) {
            return null;
        }

        int boxWidth = hitboxWidth > 0 ? hitboxWidth : width;
        int boxHeight = hitboxHeight > 0 ? hitboxHeight : height;
        int boxLeft = worldX + hitboxOffsetX;
        int boxRight = boxLeft + boxWidth;
        int boxTop = y + hitboxOffsetY;
        int attackWidth = Math.max(1, (int) Math.round(boxWidth * 0.6));
        int attackHeight = Math.max(1, (int) Math.round(boxHeight * 0.6));
        int attackY = boxTop + (boxHeight - attackHeight);
        int attackX = facingRight ? boxRight : (boxLeft - attackWidth);

        return new Rectangle(attackX, attackY, attackWidth, attackHeight);
    }

    public Rectangle getHitboxBounds() {
        int w = hitboxWidth > 0 ? hitboxWidth : width;
        int h = hitboxHeight > 0 ? hitboxHeight : height;
        int boxLeft = worldX + hitboxOffsetX;
        int boxTop = y + hitboxOffsetY;
        return new Rectangle(boxLeft, boxTop, w, h);
    }

    @Override
    protected int getSpriteOffsetX(int drawWidth) {
        int base = super.getSpriteOffsetX(drawWidth);
        if (animationController.getCurrentState() == EstadoAnimacion.IDLE) {
            int offset = facingRight ? IDLE_SPRITE_OFFSET_X_RIGHT : IDLE_SPRITE_OFFSET_X_LEFT;
            return base + offset;
        }
        return base;
    }

    @Override
    protected int getSpriteOffsetY(int drawHeight) {
        int base = super.getSpriteOffsetY(drawHeight);
        if (animationController.getCurrentState() == EstadoAnimacion.IDLE) {
            return base + IDLE_SPRITE_OFFSET_Y;
        }
        return base;
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