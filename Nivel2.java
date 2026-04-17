import java.util.ArrayList;

public class Nivel2 extends Nivel {

    final int TAMAÑO_TILE = 96;

    public Nivel2() {
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
        enemigos.add(new EnemigoBasico(tamaño * 3,  384, tamañoEnemigo));
        enemigos.add(new EnemigoBasico(tamaño * 8,  384, tamañoEnemigo));
        enemigos.add(new EnemigoBasico(tamaño * 15, 384, tamañoEnemigo));
        enemigos.add(new EnemigoJefe(tamaño * 50, 384, tamañoEnemigo * 2));
    }
}
