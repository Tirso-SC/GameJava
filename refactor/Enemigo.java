import java.awt.Rectangle;

public class Enemigo extends CharacterEntity {
    private static final int ANIMATION_SPEED = 10;
    private static final double GRAVITY = 0.70;
    private static final double MAX_FALL_SPEED = 14.0;
    private static final int MAX_HEALTH = 2;

    private static final String SPRITES_DIR =
        "Assets/Personajes/craftpix-net-407836-free-ninja-sprite-sheets-pixel-art/Ninja_Peasant";
    private static final String IDLE_PATH = SPRITES_DIR + "/Idle.png";
    private static final String RUN_PATH = SPRITES_DIR + "/Run.png";
    private static final String DEAD_PATH = SPRITES_DIR + "/Dead.png";
    private static final String HURT_PATH = SPRITES_DIR + "/Hurt.png";

    private int worldX;
    private double yFloat;
    private double verticalSpeed;
    private boolean onGround;
    private boolean movingRight = true;
    private int health = MAX_HEALTH;
    private boolean dead;
    private boolean hurting;
    private int hitboxSize;
    private int hitboxWidth;
    private int hitboxHeight;
    private int hitboxOffsetX;
    private int hitboxOffsetY;

    public Enemigo(int worldX, int y, int size, int speed) {
        super(worldX, y, size, size, speed);
        this.worldX = worldX;
        this.yFloat = y;
        loadAnimations();
    }

    private void loadAnimations() {
        Animacion idle = CargadorHojasSprites.loadAnimation(IDLE_PATH, ANIMATION_SPEED, true);
        Animacion run = CargadorHojasSprites.loadAnimation(RUN_PATH, ANIMATION_SPEED, true);
        Animacion deadAnim = CargadorHojasSprites.loadAnimation(DEAD_PATH, ANIMATION_SPEED, false);
        Animacion hurtAnim = CargadorHojasSprites.loadAnimation(HURT_PATH, ANIMATION_SPEED, false);
        if (idle != null) {
            animationController.add(EstadoAnimacion.IDLE, idle);
        }
        if (run != null) {
            animationController.add(EstadoAnimacion.WALK, run);
        }
        if (deadAnim != null) {
            animationController.add(EstadoAnimacion.DEAD, deadAnim);
        }
        if (hurtAnim != null) {
            animationController.add(EstadoAnimacion.HURT, hurtAnim);
        }
        play(EstadoAnimacion.IDLE);
    }

    @Override
    public void update(ContextoJuego context) {
        if (dead) {
            play(EstadoAnimacion.DEAD);
            animationController.update();
            return;
        }

        Tilemanager tiles = context.getTileManager();

        ensureHitbox(tiles);
        updateVerticalPhysics(context.getScreenHeight(), tiles);

        if (hurting) {
            play(EstadoAnimacion.HURT);
            animationController.update();
            if (isCurrentAnimationFinished()) {
                hurting = false;
            }
            return;
        }

        boolean moved = updateHorizontalMovement(tiles);

        if (moved) {
            play(EstadoAnimacion.WALK);
        } else {
            play(EstadoAnimacion.IDLE);
        }
        animationController.update();
    }

    private boolean updateHorizontalMovement(Tilemanager tiles) {
        if (tiles == null) {
            return false;
        }

        int dx = movingRight ? speed : -speed;
        if (dx == 0) {
            return false;
        }

        int startWorldX = worldX;

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
                movingRight = true;
            } else {
                worldX += dx;
            }
        } else {
            int newBoxRight = boxRight + dx;
            int col = tiles.worldToCol(newBoxRight);
            if (hasSolidInColumn(tiles, col, topRow, bottomRow)) {
                movingRight = false;
            } else {
                worldX += dx;
            }
        }

        if (onGround) {
            int frontX = movingRight ? boxRight + 1 : boxLeft - 1;
            int belowY = boxBottom + 1;
            if (!tiles.isSolidAt(frontX, belowY)) {
                movingRight = !movingRight;
            }
        }

        clampWorldX(tiles);
        facingRight = movingRight;
        return worldX != startWorldX;
    }

    private void updateVerticalPhysics(int screenHeight, Tilemanager tiles) {
        if (tiles == null) {
            return;
        }

        if (verticalSpeed < 0) {
            verticalSpeed = 0;
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

        if (verticalSpeed > 0) {
            int newBoxBottom = (int) Math.floor(newY) + hitboxOffsetY + hitboxHeight - 1;
            int row = tiles.worldToRow(newBoxBottom);

            if (hasSolidInRow(tiles, row, leftCol, rightCol)) {
                int tileTop = row * tiles.getTileSize();
                int resolvedBoxBottom = tileTop - 1;
                int resolvedBoxTop = resolvedBoxBottom - hitboxHeight + 1;
                yFloat = resolvedBoxTop - hitboxOffsetY;
                verticalSpeed = 0;
                onGround = true;
            } else {
                yFloat = newY;
                onGround = false;
            }
        } else {
            int belowY = (int) Math.floor(yFloat) + hitboxOffsetY + hitboxHeight;
            int row = tiles.worldToRow(belowY);

            if (hasSolidInRow(tiles, row, leftCol, rightCol)) {
                onGround = true;
            } else {
                onGround = false;
            }
        }

        int maxY = screenHeight - height;
        if (yFloat > maxY) {
            yFloat = maxY;
            verticalSpeed = 0;
            onGround = true;
        }

        y = (int) Math.round(yFloat);
    }

    public void updateScreenPosition(int cameraX) {
        x = worldX - cameraX;
    }

    public Rectangle getHitboxBounds() {
        int w = hitboxWidth > 0 ? hitboxWidth : width;
        int h = hitboxHeight > 0 ? hitboxHeight : height;
        int boxLeft = worldX + hitboxOffsetX;
        int boxTop = y + hitboxOffsetY;
        return new Rectangle(boxLeft, boxTop, w, h);
    }

    public void applyDamage(int amount) {
        if (dead || amount <= 0) {
            return;
        }

        health -= amount;
        if (health <= 0) {
            dead = true;
            play(EstadoAnimacion.DEAD);
            hurting = false;
        } else {
            hurting = true;
            play(EstadoAnimacion.HURT);
        }
    }

    public boolean isDeadAnimationFinished() {
        return dead && isCurrentAnimationFinished();
    }

    public boolean isDead() {
        return dead;
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
        hitboxWidth = Math.max(1, (int) Math.round(size * 0.7));
        hitboxHeight = size;
        hitboxOffsetX = (width - hitboxWidth) / 2;
        hitboxOffsetY = height - hitboxHeight;
    }
}