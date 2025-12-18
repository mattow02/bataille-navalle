import Controller.Game.GameController;
import Model.Boat.SimpleBoatFactory;
import Model.Games.Game;
import Model.Games.RandomGridInitializer;
import Model.Player.RegisteredShotStrategyFactory;
import Model.Player.StandardPlayerFactory;
import Model.Trap.DefaultTrapFactory;
import View.Screen.SwingUiExecutor;
import View.Screen.SwingViewFactory;

import javax.swing.SwingUtilities;

/** Lance l'application principale. */
public class App {

    /** Démarre l'application en initialisant le contrôleur de jeu. */
    public static void main(String[] args) {
        var playerFactory = new StandardPlayerFactory();
        var gridInitializer = RandomGridInitializer.withDefaults();
        var boatFactory = new SimpleBoatFactory();
        var shotStrategyFactory = new RegisteredShotStrategyFactory();
        var trapFactory = new DefaultTrapFactory();
        var game = new Game(playerFactory, gridInitializer, boatFactory, shotStrategyFactory, trapFactory);
        var gameController = new GameController(
                game,
                new SwingViewFactory(),
                new SwingUiExecutor()
        );
        SwingUtilities.invokeLater(gameController::startApplication);
    }
}
