package Model.Games;

import Model.Boat.Boat;
import Model.Coordinates;
import Model.GridEntity;
import Model.Orientation;

import java.util.List;

/** Prépare les éléments nécessaires au démarrage du jeu. */
public interface GameSetup {

    /** Initialise la configuration du jeu. */
    void init(GameConfiguration config);

    /** Liste les bateaux à placer. */
    List<Boat> getBoatsToPlace();

    /** Liste les pièges à placer. */
    List<GridEntity> getTrapsToPlace();

    /** Place une entité avec l'orientation appropriée. */
    boolean placeEntityOnPlayerGridByType(GridEntity entity, Coordinates coord, Orientation orientation);

    /** Taille de la grille. */
    int getGridSize();

    /** Indique si le mode île est activé. */
    boolean isIslandMode();
}
