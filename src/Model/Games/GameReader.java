package Model.Games;

import Model.Coordinates;
import Model.Map.CellStateInfo;

/** Expose en lecture l'état du jeu pour les vues. */
public interface GameReader {

    /** Taille de la grille. */
    int getGridSize();

    /** État d'une cellule côté joueur. */
    CellStateInfo getPlayerCellStateInfo(Coordinates coord);

    /** État d'une cellule côté ennemi. */
    CellStateInfo getEnemyCellStateInfo(Coordinates coord);

    /** Nombre de tours joués. */
    int getTurnCount();

    /** Nombre de bateaux joueurs vivants. */
    int getPlayerAliveBoats();

    /** Nombre de bateaux ennemis vivants. */
    int getComputerAliveBoats();

    /** Nombre de bombes du joueur. */
    int getPlayerBombCount();

    /** Nombre de sonars du joueur. */
    int getPlayerSonarCount();

    /** Indique si le joueur possède une bombe. */
    boolean hasPlayerBomb();

    /** Indique si le joueur possède un sonar. */
    boolean hasPlayerSonar();

    /** Indique si un sous-marin joueur est encore en vie. */
    boolean isPlayerSubmarineAlive();
}
