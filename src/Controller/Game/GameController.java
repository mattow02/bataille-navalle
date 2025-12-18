package Controller.Game;

import Controller.Config.ConfigurationController;
import Controller.IGameController;
import Controller.IGameControllerCoordinator;
import Controller.Placement.PlacementController;
import Model.Coordinates;
import Model.GameObserver;
import Model.Games.GameCommander;
import Model.Games.GameConfiguration;
import Model.Games.GameObservable;
import Model.Games.GameReader;
import Model.Games.GameSetup;
import Model.Games.IGame;
import Model.HitOutcome;
import Model.Weapons.Weapon;
import Model.Weapons.WeaponFactory;
import Model.Weapons.WeaponResult;
import Model.Weapons.WeaponSelectionService;
import Model.Weapons.WeaponType;
import View.Model.CellState;
import View.Model.EndGameStats;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/** Pilote la logique principale du jeu et la navigation entre vues. */
public class GameController implements IGameController, IGameControllerCoordinator, GameObserver {

    private final GameReader gameReader;
    private final GameCommander gameCommander;
    private final GameSetup gameSetup;
    private final GameObservable gameObservable;
    private final GameViewFactory viewFactory;
    private final UiExecutor uiExecutor;
    private final Function<WeaponType, Weapon> weaponProvider;
    private final ApplicationLifecycle lifecycle;
    private final WeaponCommandHandler weaponHandler;
    private final BattleFlowController battleFlow;
    private final UiLogger uiLogger;
    private final NavigationCoordinator navigation;
    private final BoardViewModelMapper mapper = new BoardViewModelMapper();

    /** Construit le contrôleur avec les dépendances par défaut. */
    public GameController(IGame game, GameViewFactory viewFactory, UiExecutor uiExecutor) {
        this(
                game,
                game,
                game,
                game,
                viewFactory,
                uiExecutor,
                WeaponFactory.withDefaults()::create,
                Executors::newSingleThreadScheduledExecutor,
                ConfigurationController::new,
                PlacementController::new,
                new DefaultApplicationLifecycle()
        );
    }

    /** Construit le contrôleur avec un fournisseur d'armes custom. */
    public GameController(IGame game,
                          GameViewFactory viewFactory,
                          UiExecutor uiExecutor,
                          Function<WeaponType, Weapon> weaponProvider) {
        this(
                game,
                game,
                game,
                game,
                viewFactory,
                uiExecutor,
                weaponProvider,
                Executors::newSingleThreadScheduledExecutor,
                ConfigurationController::new,
                PlacementController::new,
                new DefaultApplicationLifecycle()
        );
    }

    /** Construit le contrôleur avec ses dépendances explicites. */
    public GameController(GameReader gameReader,
                          GameCommander gameCommander,
                          GameSetup gameSetup,
                          GameObservable gameObservable,
                          GameViewFactory viewFactory,
                          UiExecutor uiExecutor,
                          Function<WeaponType, Weapon> weaponProvider,
                          Supplier<ScheduledExecutorService> schedulerSupplier,
                          Function<IGameControllerCoordinator, ConfigurationController> configurationControllerFactory,
                          BiFunction<IGameControllerCoordinator, GameSetup, PlacementController> placementControllerFactory,
                          ApplicationLifecycle lifecycle) {
        this.gameReader = gameReader;
        this.gameCommander = gameCommander;
        this.gameSetup = gameSetup;
        this.gameObservable = gameObservable;
        this.viewFactory = viewFactory;
        this.uiExecutor = uiExecutor;
        Supplier<ScheduledExecutorService> safeSchedulerSupplier = schedulerSupplier != null
                ? schedulerSupplier
                : Executors::newSingleThreadScheduledExecutor;
        this.weaponProvider = weaponProvider != null ? weaponProvider : WeaponFactory.withDefaults()::create;
        Function<IGameControllerCoordinator, ConfigurationController> configurationControllerFactorySafe =
                configurationControllerFactory != null ? configurationControllerFactory : ConfigurationController::new;
        BiFunction<IGameControllerCoordinator, GameSetup, PlacementController> placementControllerFactorySafe =
                placementControllerFactory != null ? placementControllerFactory : PlacementController::new;
        this.lifecycle = lifecycle != null ? lifecycle : new DefaultApplicationLifecycle();
        this.weaponHandler = new WeaponCommandHandler(gameReader, new WeaponSelectionService());
        this.battleFlow = new BattleFlowController(gameReader, gameObservable, viewFactory, uiExecutor, safeSchedulerSupplier);
        this.uiLogger = new DefaultUiLogger(battleFlow::getBattleView, uiExecutor);
        var battleCoordinator = new BattleScreenCoordinator(this, battleFlow, weaponHandler);
        var navigationCoordinator = new DefaultNavigationCoordinator(
                this,
                this,
                gameSetup,
                viewFactory,
                uiExecutor,
                battleCoordinator,
                configurationControllerFactorySafe,
                placementControllerFactorySafe
        );
        this.navigation = navigationCoordinator;
    }

    /** Démarre l'application et ouvre la configuration. */
    public void startApplication() {
        navigation.showConfiguration();
    }

    /** Reçoit la configuration finalisée et lance le placement. */
    public void handleConfigurationComplete(GameConfiguration config) {
        gameSetup.init(config);
        navigation.startManualPlacement();
    }

    /** Affiche la vue de bataille. */
    public void showBattleView() {
        navigation.showBattle();
    }

    /** Programme le tour de l'IA. */
    public void scheduleComputerTurn() {
        battleFlow.scheduleComputerTurn(gameCommander::resolveComputerTurn);
    }

    /** Traite une attaque du joueur. */
    public void handlePlayerAttack(int row, int col) {
        if (!gameCommander.isPlayerTurn()) return;
        var target = new Coordinates(row, col);
        var weapon = weaponProvider.apply(weaponHandler.getCurrentWeaponType());
        gameCommander.handleWeaponAttack(weapon, target);
        if (weaponHandler.getCurrentWeaponType() != WeaponType.MISSILE) {
            weaponHandler.resetToDefault();
        }
    }

    /** Sélectionne une arme par identifiant. */
    @Override
    public void selectWeapon(String weaponId) {
        WeaponType type;
        try {
            type = WeaponType.valueOf(weaponId);
        } catch (IllegalArgumentException e) {
            log("❌ Erreur interne : Arme inconnue (" + weaponId + ")");
            return;
        }
        selectWeapon(type);
    }

    /** Sélectionne une arme par type. */
    @Override
    public void selectWeapon(WeaponType type) {
        var result = weaponHandler.select(type);
        var view = battleFlow.getBattleView();
        if (view != null) {
            view.setSelectedWeapon(result.selectedType().name());
        }
        switch (result.status()) {
            case NOT_AVAILABLE -> log("Arme " + type + " indisponible.");
            case INVALID_SUBMARINE -> log("Sous-marin requis pour " + type + ".");
            case ACTIVATED -> log("Arme " + type + " sélectionnée.");
        }
    }

    /** Journalise un message vers l'UI. */
    public void log(String message) {
        uiLogger.log(message);
    }

    /** Retourne le nombre de tours. */
    public int getTurnCount() {
        return gameReader.getTurnCount();
    }

    /** Retourne le nombre de bateaux joueurs vivants. */
    public int getPlayerAliveBoatsCount() {
        return gameReader.getPlayerAliveBoats();
    }

    /** Retourne le nombre de bateaux ennemis vivants. */
    public int getComputerAliveBoatsCount() {
        return gameReader.getComputerAliveBoats();
    }

    /** Fournit l'état d'une cellule joueur. */
    public CellState getPlayerCellState(int row, int col) {
        return mapper.toCellState(gameReader.getPlayerCellStateInfo(new Coordinates(row, col)), true);
    }

    /** Rafraîchit la vue après initialisation du jeu. */
    @Override
    public void onGameInitialized() {
        uiExecutor.execute(this::refreshView);
    }

    /** Rafraîchit la vue sur demande. */
    @Override
    public void onRefreshRequested() {
        uiExecutor.execute(this::refreshView);
    }

    /** Planifie le tour IA après le tour joueur. */
    @Override
    public void onPlayerTurnEnded() {
        uiExecutor.execute(this::scheduleComputerTurn);
    }

    /** Affiche l'écran de fin de partie. */
    @Override
    public void onGameOver(boolean playerWon, int turnCount, int playerAliveBoats, int computerAliveBoats) {
        var stats = new EndGameStats(playerWon ? "Victoire" : "Défaite", turnCount, playerAliveBoats, computerAliveBoats);
        navigation.showEndGame(stats);
    }

    /** Journalise un tir effectué. */
    @Override
    public void onWeaponFired(WeaponResult result) {
        uiExecutor.execute(() -> {
            if (result.type() == WeaponType.SONAR) {
                if (!result.success()) {
                    if (result.value() == -1) {
                        log("Sous-marin requis pour SONAR.");
                    } else {
                        log("Sonar indisponible.");
                    }
                } else {
                    log("Sonar détecte " + result.value() + " cibles potentielles.");
                }
                return;
            }
            var outcome = switch (result.primaryOutcome()) {
                case TRAP_TRIGGERED -> "Piège déclenché";
                case ACQUIRED_WEAPON -> "Bonus récupéré";
                default -> result.primaryOutcome().toString();
            };
            log("Tir " + result.type() + " : " + outcome);
        });
    }

    /** Notifie la résolution d'un tir. */
    @Override
    public void onShotResolved(Coordinates target, HitOutcome outcome, boolean isHuman) {
        uiExecutor.execute(() -> {
            log((isHuman ? "[Vous] " : "[IA] ") + target.toFormattedString() + " -> " + outcome);
            refreshView();
        });
    }

    /** Gère l'effet de déplacement de tornade. */
    @Override
    public void onTornadoEffect(Coordinates coords) {
        uiExecutor.execute(() -> {
            log("Tornade déclenchée sur " + (coords != null ? coords.toFormattedString() : "?"));
            refreshView();
        });
    }

    /** Notifie l'impact d'une tornade. */
    @Override
    public void onTornadoHit() {
        uiExecutor.execute(() -> log("Tornade : impact !"));
    }

    /** Notifie un déclenchement de trou noir. */
    @Override
    public void onBlackHoleTrigger() {
        uiExecutor.execute(() -> log("Trou noir déclenché"));
    }

    /** Notifie un retour de trou noir. */
    @Override
    public void onBlackHoleBackfire(Coordinates coords) {
        uiExecutor.execute(() -> {
            log("Retour du trou noir sur " + (coords != null ? coords.toFormattedString() : "?"));
            refreshView();
        });
    }

    /** Notifie l'absorption d'un trou noir. */
    @Override
    public void onBlackHoleAbsorbed(Coordinates coords) {
        uiExecutor.execute(() -> log("Trou noir absorbé sur " + (coords != null ? coords.toFormattedString() : "?")));
    }

    /** Notifie l'obtention d'une bombe. */
    @Override
    public void onItemBombFound() {
        uiExecutor.execute(() -> {
            log("Bonus bombe obtenu");
            refreshView();
        });
    }

    /** Notifie l'obtention d'un sonar. */
    @Override
    public void onItemSonarFound() {
        uiExecutor.execute(() -> {
            log("Bonus sonar obtenu");
            refreshView();
        });
    }

    private void refreshView() {
        var view = battleFlow.getBattleView();
        if (view == null) return;
        var size = gameReader.getGridSize();
        var playerStates = new CellState[size][size];
        var enemyStates = new CellState[size][size];
        for (var r = 0; r < size; r++) {
            for (var c = 0; c < size; c++) {
                var cord = new Coordinates(r, c);
                playerStates[r][c] = mapper.toCellState(gameReader.getPlayerCellStateInfo(cord), true);
                enemyStates[r][c] = mapper.toCellState(gameReader.getEnemyCellStateInfo(cord), false);
            }
        }
        view.updateGrids(playerStates, enemyStates);
        view.updateInfo(new View.Model.BattleInfo(
                gameReader.getTurnCount(),
                gameReader.getPlayerAliveBoats(),
                gameReader.getComputerAliveBoats(),
                gameReader.getPlayerBombCount(),
                gameReader.getPlayerSonarCount(),
                gameReader.hasPlayerBomb(),
                gameReader.hasPlayerSonar()
        ));
        view.renderWeapons(battleFlow.buildWeaponViewModels());
        view.setSelectedWeapon(weaponHandler.getCurrentWeaponType().name());
    }

    /** Redémarre l'application. */
    @Override
    public void restartApplication() {
        uiExecutor.execute(() -> {
            var view = battleFlow.getBattleView();
            if (view != null) {
                view.close();
            }
        });
        battleFlow.restartScheduler();
        startApplication();
    }

    /** Ferme l'application. */
    @Override
    public void quitApplication() {
        battleFlow.shutdownScheduler();
        lifecycle.quit();
    }

    /** Affiche la vue de fin de partie. */
    @Override
    public void showEndGame(EndGameStats stats) {
        uiExecutor.execute(() -> {
            var view = battleFlow.getBattleView();
            if (view != null) {
                view.close();
            }
            var endGameView = viewFactory.createEndGameView(stats, this);
            endGameView.display();
        });
    }
}
