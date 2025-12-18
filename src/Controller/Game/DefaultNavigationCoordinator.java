package Controller.Game;

import Controller.Config.ConfigurationController;
import Controller.IGameController;
import Controller.IGameControllerCoordinator;
import Controller.Placement.PlacementController;
import Model.Games.GameSetup;
import View.GameView;
import View.Model.EndGameStats;
import View.PlacementViewInterface;

import java.util.function.BiFunction;
import java.util.function.Function;

/** Coordonne la navigation entre les écrans du jeu. */
public class DefaultNavigationCoordinator implements NavigationCoordinator {

    private final IGameController controller;
    private final IGameControllerCoordinator coordinator;
    private final GameSetup game;
    private final GameViewFactory viewFactory;
    private final UiExecutor uiExecutor;
    private final BattleScreenCoordinator battleCoordinator;
    private final Function<IGameControllerCoordinator, ConfigurationController> configurationControllerFactory;
    private final BiFunction<IGameControllerCoordinator, GameSetup, PlacementController> placementControllerFactory;

    /** Initialise le coordinateur avec les dépendances nécessaires. */
    public DefaultNavigationCoordinator(IGameController controller,
                                        IGameControllerCoordinator coordinator,
                                        GameSetup game,
                                        GameViewFactory viewFactory,
                                        UiExecutor uiExecutor,
                                        BattleScreenCoordinator battleCoordinator,
                                        Function<IGameControllerCoordinator, ConfigurationController> configurationControllerFactory,
                                        BiFunction<IGameControllerCoordinator, GameSetup, PlacementController> placementControllerFactory) {
        this.controller = controller;
        this.coordinator = coordinator;
        this.game = game;
        this.viewFactory = viewFactory;
        this.uiExecutor = uiExecutor;
        this.battleCoordinator = battleCoordinator;
        this.configurationControllerFactory = configurationControllerFactory;
        this.placementControllerFactory = placementControllerFactory;
    }

    /** Affiche l'écran de configuration. */
    @Override
    public void showConfiguration() {
        uiExecutor.execute(() -> {
            var configController = configurationControllerFactory.apply(coordinator);
            var configView = viewFactory.createConfigurationView(configController);
            configView.display();
        });
    }

    /** Démarre le parcours de placement manuel. */
    @Override
    public void startManualPlacement() {
        var placementController = placementControllerFactory.apply(coordinator, game);
        placementController.initializeBoats();
        var placementView = viewFactory.createPlacementView(placementController);
        placementController.setView(placementView);
        placementController.setOnFinished(coordinator::showBattleView);
        uiExecutor.execute(placementView::display);
    }

    /** Affiche la bataille en cours. */
    @Override
    public void showBattle() {
        battleCoordinator.showBattle();
    }

    /** Affiche l'écran de fin de partie. */
    @Override
    public void showEndGame(EndGameStats stats) {
        uiExecutor.execute(() -> {
            battleCoordinator.closeBattleView();
            var endGameView = viewFactory.createEndGameView(stats, controller);
            endGameView.display();
        });
    }
}
