package Controller.Game;

import Controller.IGameController;
import Model.GameObserver;
import Model.Games.GameObservable;
import Model.Games.GameReader;
import Model.Weapons.WeaponType;
import View.BattleViewInterface;
import View.Model.WeaponViewModel;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/** Orchestre l'affichage et la boucle de la bataille. */
public class BattleFlowController {
    private final GameReader game;
    private final GameObservable observable;
    private final GameViewFactory viewFactory;
    private final UiExecutor uiExecutor;
    private final Supplier<ScheduledExecutorService> schedulerSupplier;
    private ScheduledExecutorService scheduler;
    private BattleViewInterface battleView;

    /** Initialise le flux de bataille avec ses dépendances. */
    public BattleFlowController(GameReader game,
                                GameObservable observable,
                                GameViewFactory viewFactory,
                                UiExecutor uiExecutor,
                                Supplier<ScheduledExecutorService> schedulerSupplier) {
        this.game = game;
        this.observable = observable;
        this.viewFactory = viewFactory;
        this.uiExecutor = uiExecutor;
        this.schedulerSupplier = schedulerSupplier != null ? schedulerSupplier : java.util.concurrent.Executors::newSingleThreadScheduledExecutor;
        this.scheduler = this.schedulerSupplier.get();
    }

    /** Affiche la vue de bataille et configure les armes. */
    public void showBattleView(IGameController controller, WeaponCommandHandler weaponHandler) {
        uiExecutor.execute(() -> {
            battleView = viewFactory.createBattleView(controller, game.getGridSize());
            battleView.display();
            observable.addObserver((GameObserver) controller);
            battleView.renderWeapons(buildWeaponViewModels());
            battleView.setSelectedWeapon(weaponHandler.getCurrentWeaponType().name());
            if (controller instanceof Model.GameObserver observer) {
                observer.onRefreshRequested();
            }
        });
    }

    /** Programme le tour de l'ordinateur après le joueur. */
    public void scheduleComputerTurn(Runnable computerTurn) {
        ensureScheduler().schedule(() -> uiExecutor.execute(computerTurn), 1, TimeUnit.SECONDS);
    }

    /** Coupe immédiatement le scheduler interne. */
    public void shutdownScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    /** Relance un scheduler neuf. */
    public void restartScheduler() {
        shutdownScheduler();
        scheduler = schedulerSupplier.get();
    }

    /** Expose la vue de bataille actuelle. */
    public BattleViewInterface getBattleView() {
        return battleView;
    }

    /** Construit les modèles d'armes pour la vue. */
    public java.util.List<WeaponViewModel> buildWeaponViewModels() {
        var list = new java.util.ArrayList<WeaponViewModel>();
        for (var type : WeaponType.values()) {
            var label = switch (type) {
                case MISSILE -> "Missile";
                case BOMB -> "Bombe (" + game.getPlayerBombCount() + ")";
                case SONAR -> "Sonar (" + game.getPlayerSonarCount() + ")";
            };
            var enabled = switch (type) {
                case MISSILE -> true;
                case BOMB -> game.hasPlayerBomb();
                case SONAR -> game.hasPlayerSonar();
            };
            list.add(new WeaponViewModel(type.name(), label, enabled));
        }
        return list;
    }

    private ScheduledExecutorService ensureScheduler() {
        if (scheduler == null || scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = schedulerSupplier.get();
        }
        return scheduler;
    }
}
