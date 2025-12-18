package Controller.Game;

import Controller.Config.ConfigurationController;
import Controller.IGameController;
import Controller.Placement.PlacementController;
import View.BattleViewInterface;
import View.GameView;
import View.Model.EndGameStats;
import View.PlacementViewInterface;

/** Fabrique les vues utilisées pendant le jeu. */
public interface GameViewFactory {

    /** Crée la vue de configuration. */
    GameView createConfigurationView(ConfigurationController controller);

    /** Crée la vue de placement. */
    PlacementViewInterface createPlacementView(PlacementController controller);

    /** Crée la vue de bataille. */
    BattleViewInterface createBattleView(IGameController controller, int gridSize);

    /** Crée la vue de fin de partie. */
    GameView createEndGameView(EndGameStats stats, IGameController controller);
}
