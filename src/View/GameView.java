package View;

/** Vue affichable/fermable du jeu. */
public interface GameView {

    void display();

    default void close() {}
}
