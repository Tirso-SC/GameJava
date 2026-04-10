import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

public class ControladorAnimacion {
    private final Map<EstadoAnimacion, Animacion> animations = new EnumMap<>(EstadoAnimacion.class);
    private EstadoAnimacion currentState;
    private Animacion currentAnimation;

    public void add(EstadoAnimacion state, Animacion animation) {
        animations.put(state, animation);

        if (currentAnimation == null) {
            currentState = state;
            currentAnimation = animation;
        }
    }

    public void play(EstadoAnimacion state) {
        if (state == null || state == currentState) {
            return;
        }

        Animacion nextAnimation = animations.get(state);
        if (nextAnimation == null) {
            return;
        }

        currentState = state;
        currentAnimation = nextAnimation;
        currentAnimation.reset();
    }

    public void update() {
        if (currentAnimation != null) {
            currentAnimation.update();
        }
    }

    public BufferedImage getCurrentFrame() {
        return currentAnimation != null ? currentAnimation.getFrame() : null;
    }

    public int getCurrentFrameIndex() {
        return currentAnimation != null ? currentAnimation.getCurrentFrameIndex() : 0;
    }

    public boolean isCurrentAnimationFinished() {
        return currentAnimation != null && currentAnimation.isFinished();
    }

    public EstadoAnimacion getCurrentState() {
        return currentState;
    }
}