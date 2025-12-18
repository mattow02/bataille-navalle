package Controller;

import Model.Games.GameConfiguration;
import View.Model.CellState;

/** Coordonne les interactions entre contrôleurs de jeu. */
public interface IGameControllerCoordinator {

    /** Enregistre un message côté interface. */
    void log(String message);

    /** Affiche la vue de bataille. */
    void showBattleView();

    /** Fournit l'état d'une cellule du joueur. */
    CellState getPlayerCellState(int row, int col);

    /** Reçoit une configuration validée pour lancer la partie. */
    void handleConfigurationComplete(GameConfiguration config);
}
