package Model.Trap;

import Model.GridEntity;

import java.util.List;

/** Fabrique des pièges selon le mode de jeu. */
public interface TrapFactory {

    BlackHole createBlackHole();

    Tornado createTornado();

    List<GridEntity> createTraps(boolean islandMode);
}
