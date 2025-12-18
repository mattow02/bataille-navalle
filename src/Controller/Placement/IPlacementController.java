package Controller.Placement;

import View.Model.CellState;

/** Gère les interactions de placement côté joueur. */
public interface IPlacementController {

    /** Traite un clic sur la grille de placement. */
    void handleGridClick(int row, int col);

    /** Inverse l'orientation courante. */
    void toggleOrientation();

    /** Retourne l'état d'une cellule côté joueur. */
    CellState getCellState(int row, int col);

    /** Donne le nom de l'élément en cours de placement. */
    String getCurrentItemName();

    /** Donne la taille de l'élément en cours de placement. */
    int getCurrentItemSize();

    /** Indique si l'orientation courante est horizontale. */
    boolean isHorizontal();

    /** Retourne la taille de la grille. */
    int getGridSize();
}
