package Model.Games;

import Model.Map.Grid;

/** Initialise les grilles de jeu selon la configuration. */
public interface GridInitializer {

    /** Met en place les éléments initiaux sur les grilles. */
    void setupGrids(GameConfiguration config, Grid playerGrid, Grid enemyGrid);
}
