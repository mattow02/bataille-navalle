package Model.Boat;

import Model.Coordinates;
import Model.GridEntity;
import Model.HitOutcome;
import Model.Map.Grid;
import Model.Orientation;

/** Décrit un bateau plaçable sur la grille. */
public interface Boat extends GridEntity {

    /** Indique si le bateau est coulé. */
    boolean isSunk();

    /** Applique un tir sur un segment du bateau. */
    HitOutcome receiveHit(int index);

    /** Donne la taille du bateau. */
    int size();

    /** Donne le nom du bateau. */
    String name();

    /** Retourne le type du bateau. */
    BoatType getType();

    default boolean placeOnGrid(Grid grid, Coordinates startCoord, Orientation orientation) {
        return grid != null && grid.placeBoat(this, startCoord, orientation);
    }

    @Override
    default boolean placeOnGridWithOrientation(Grid grid, Coordinates startCoord, Orientation orientation) {
        return placeOnGrid(grid, startCoord, orientation);
    }
}
