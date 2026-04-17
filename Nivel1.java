import java.util.ArrayList;

public class Nivel1 extends Nivel {

    final int TAMAÑO_TILE = 96;

    public Nivel1() {
        super();
    }

    @Override
    public void inicializar(int tamaño) {
        int anchoPanel = tamaño * 12;
        int largoPanel = tamaño * 8;

        mapa = new Mapa(tamaño);
        fondo = new Fondo(anchoPanel, largoPanel);

        decoraciones = new ArrayList<>();
        decoraciones.add(new ObjetoEstatico(
            "Assets/Background/GandalfHardcore FREE Platformer Assets/Willow2.png",
            400, 170, 500, 500
        ));

        int tamañoEnemigo = (int)(tamaño * 1.5);
        enemigos.add(new EnemigoBasico(tamaño * 5,  384, tamañoEnemigo));
        enemigos.add(new EnemigoBasico(tamaño * 20, 384, tamañoEnemigo));
        enemigos.add(new EnemigoJefe(tamaño * 45, 384, tamañoEnemigo * 2));
    }
}
