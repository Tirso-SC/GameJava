public class Enemigo extends CharacterEntity {
    public Enemigo(int x, int y, int size, int speed) {
        super(x, y, size, size, speed);
    }

    @Override
    public void update(ContextoJuego context) {
        // Ejemplo mínimo para que veas cómo se amplía:
        // aquí NO usarías teclado, sino IA, distancia al jugador, temporizadores, etc.
        animationController.update();
    }
}