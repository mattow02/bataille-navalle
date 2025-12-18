package View.Screen;

import Controller.Config.ConfigurationController;
import Controller.Game.GameViewFactory;
import Controller.IGameController;
import Controller.Placement.PlacementController;
import View.BattleViewInterface;
import View.GameView;
import View.Model.EndGameStats;
import View.PlacementViewInterface;

/** Fabrique Swing pour les différentes vues du jeu. */
public class SwingViewFactory implements GameViewFactory {

    @Override
    public GameView createConfigurationView(ConfigurationController controller) {
        return new ConfigurationView(controller);
    }

    @Override
    public PlacementViewInterface createPlacementView(PlacementController controller) {
        return new PlacementView(controller);
    }

    @Override
    public BattleViewInterface createBattleView(IGameController controller, int gridSize) {
        return new BattleView(controller, gridSize);
    }

    @Override
    public GameView createEndGameView(EndGameStats stats, IGameController controller) {
        return new EndGameView(stats, controller);
    }
}
