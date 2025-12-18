package Controller.Game;

import View.Model.EndGameStats;

/** Coordonne les transitions entre les écrans du jeu. */
public interface NavigationCoordinator {

    /** Ouvre l'écran de configuration. */
    void showConfiguration();

    /** Démarre le flux de placement manuel. */
    void startManualPlacement();

    /** Affiche l'écran de bataille. */
    void showBattle();

    /** Affiche l'écran de fin de partie. */
    void showEndGame(EndGameStats stats);
}
