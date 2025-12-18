package Model.Games;

import Model.Boat.Boat;
import Model.Boat.BoatFactory;
import Model.Coordinates;
import Model.GameObserver;
import Model.Map.Grid;
import Model.Orientation;
import Model.Player.ComputerPlayer;
import Model.Player.HumanPlayer;
import Model.Player.PlayerFactory;
import Model.Player.ShotStrategyFactory;
import Model.Trap.TrapFactory;
import Model.Weapons.WeaponType;
import Model.Games.TurnService.TurnContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/** Gère l'état et la logique globale d'une partie. */
public class Game implements IGame {
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final PlayerFactory playerFactory;
    private final GridInitializer gridInitializer;
    private final PlacementManager placementManager;
    private final List<GameObserver> observers;
    private final ShotStrategyFactory shotStrategyFactory;

    private Grid playerGrid;
    private Grid enemyGrid;
    private HumanPlayer humanPlayer;
    private ComputerPlayer computerPlayer;

    private final TurnService turnService;
    private GameConfiguration config;

    /** Initialise le jeu avec ses dépendances. */
    public Game(PlayerFactory playerFactory,
                GridInitializer gridInitializer,
                BoatFactory boatFactory,
                ShotStrategyFactory shotStrategyFactory,
                TrapFactory trapFactory) {
        this.playerFactory = playerFactory;
        this.gridInitializer = gridInitializer;
        this.shotStrategyFactory = shotStrategyFactory;
        this.placementManager = new PlacementManager(boatFactory, trapFactory);
        this.playerGrid = new Grid(10);
        this.enemyGrid = new Grid(10);
        this.observers = new ArrayList<>();
        this.turnService = new TurnService(new GameTurnContext(), rwLock);
    }

    /** Prépare une nouvelle partie avec la configuration fournie. */
    public void init(GameConfiguration config) {
        rwLock.writeLock().lock();
        try {
            this.config = config;

            this.playerGrid = new Grid(config.gridSize());
            this.enemyGrid = new Grid(config.gridSize());

            this.humanPlayer = playerFactory.createHumanPlayer("Joueur", playerGrid, enemyGrid);
            this.computerPlayer = playerFactory.createComputerPlayer(enemyGrid, playerGrid);

            this.turnService.reset();

            this.computerPlayer.setShotStrategy(
                    shotStrategyFactory.createForLevel(config.computerShotLevel())
            );

            if (!config.islandMode()) {
                humanPlayer.addBomb();
                humanPlayer.addSonar();
            }

            gridInitializer.setupGrids(config, playerGrid, enemyGrid);
        } finally {
            rwLock.writeLock().unlock();
        }
        notifyObservers(GameObserver::onGameInitialized);
    }

    /** Traite l'attaque du joueur avec l'arme donnée. */
    public void handleWeaponAttack(Model.Weapons.Weapon weapon, Coordinates target) {
        turnService.handlePlayerAttack(weapon, target);
    }

    /** Déclenche le tour de l'ordinateur. */
    public void resolveComputerTurn() {
        turnService.resolveComputerTurn();
    }

    /** Taille de la grille. */
    public int getGridSize() {
        rwLock.readLock().lock();
        try {
            return playerGrid != null ? playerGrid.getSize() : 10;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** Nombre de bateaux joueurs encore en vie. */
    public int getPlayerAliveBoats() {
        rwLock.readLock().lock();
        try {
            return humanPlayer != null ? humanPlayer.getAliveBoatsCount() : 0;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** Nombre de bateaux ennemis encore en vie. */
    public int getComputerAliveBoats() {
        rwLock.readLock().lock();
        try {
            return computerPlayer != null ? computerPlayer.getAliveBoatsCount() : 0;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** Nombre de bombes du joueur. */
    public int getPlayerBombCount() {
        rwLock.readLock().lock();
        try {
            return humanPlayer != null ? humanPlayer.getBombCount() : 0;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** Nombre de sonars du joueur. */
    public int getPlayerSonarCount() {
        rwLock.readLock().lock();
        try {
            return humanPlayer != null ? humanPlayer.getSonarCount() : 0;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** Indique si le joueur dispose d'au moins une bombe. */
    public boolean hasPlayerBomb() {
        rwLock.readLock().lock();
        try {
            return humanPlayer != null && humanPlayer.hasAmmo(WeaponType.BOMB);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** Indique si le joueur dispose d'au moins un sonar. */
    public boolean hasPlayerSonar() {
        rwLock.readLock().lock();
        try {
            return humanPlayer != null && humanPlayer.hasAmmo(WeaponType.SONAR);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** Indique si un sous-marin joueur est encore en vie. */
    public boolean isPlayerSubmarineAlive() {
        rwLock.readLock().lock();
        try {
            return humanPlayer != null && humanPlayer.isSubmarineAlive();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** Liste les bateaux à placer. */
    public List<Boat> getBoatsToPlace() {
        rwLock.readLock().lock();
        try {
            return List.copyOf(placementManager.createBoats(config));
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** Indique si le mode île est actif. */
    public boolean isIslandMode() {
        rwLock.readLock().lock();
        try {
            return config != null && config.islandMode();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** Liste les pièges à placer. */
    public List<Model.GridEntity> getTrapsToPlace() {
        rwLock.readLock().lock();
        try {
            return List.copyOf(placementManager.createTraps(isIslandMode()));
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** Place un bateau sur la grille joueur. */
    public boolean placeBoatOnPlayerGrid(Boat boat, Coordinates startCoord, Orientation orientation) {
        rwLock.writeLock().lock();
        try {
            return playerGrid != null && playerGrid.placeBoat(boat, startCoord, orientation);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /** Place une entité sur la grille joueur. */
    public boolean placeEntityOnPlayerGrid(Model.GridEntity entity, Coordinates coord) {
        rwLock.writeLock().lock();
        try {
            return playerGrid != null && playerGrid.placeEntity(entity, coord);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /** Place une entité en tenant compte de son orientation. */
    public boolean placeEntityOnPlayerGridByType(Model.GridEntity entity, Coordinates coord, Orientation orientation) {
        rwLock.writeLock().lock();
        try {
            return placementManager.placeEntityOnGridByType(playerGrid, entity, coord, orientation);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /** État d'une cellule côté joueur. */
    public Model.Map.CellStateInfo getPlayerCellStateInfo(Coordinates coord) {
        rwLock.readLock().lock();
        try {
            if (playerGrid == null) {
                return new Model.Map.CellStateInfo(false, false, false, false, false);
            }
            return playerGrid.getCellStateInfo(coord);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** État d'une cellule côté ennemi. */
    public Model.Map.CellStateInfo getEnemyCellStateInfo(Coordinates coord) {
        rwLock.readLock().lock();
        try {
            if (enemyGrid == null) {
                return new Model.Map.CellStateInfo(false, false, false, false, false);
            }
            return enemyGrid.getCellStateInfo(coord);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** Nombre de tours déjà joués. */
    public int getTurnCount() {
        rwLock.readLock().lock();
        try {
            return turnService != null ? turnService.getTurnCount() : 0;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /** Indique si c'est le tour du joueur. */
    public boolean isPlayerTurn() {
        rwLock.readLock().lock();
        try {
            return turnService != null && turnService.isPlayerTurn();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public void addObserver(GameObserver observer) {
        if (observer == null) return;
        rwLock.writeLock().lock();
        try {
            if (!observers.contains(observer)) {
                observers.add(observer);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public void removeObserver(GameObserver observer) {
        if (observer == null) return;
        rwLock.writeLock().lock();
        try {
            observers.remove(observer);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    private void notifyObservers(Consumer<GameObserver> notification) {
        if (notification == null) return;
        List<GameObserver> snapshot;
        rwLock.readLock().lock();
        try {
            snapshot = List.copyOf(observers);
        } finally {
            rwLock.readLock().unlock();
        }
        for (GameObserver observer : snapshot) {
            notification.accept(observer);
        }
    }

    private class GameTurnContext implements TurnContext {

        @Override
        public Grid playerGrid() {
            return Game.this.playerGrid;
        }

        @Override
        public Grid enemyGrid() {
            return Game.this.enemyGrid;
        }

        @Override
        public HumanPlayer humanPlayer() {
            return Game.this.humanPlayer;
        }

        @Override
        public ComputerPlayer computerPlayer() {
            return Game.this.computerPlayer;
        }

        @Override
        public int getPlayerAliveBoats() {
            return Game.this.getPlayerAliveBoats();
        }

        @Override
        public int getComputerAliveBoats() {
            return Game.this.getComputerAliveBoats();
        }

        @Override
        public void notifyObservers(Consumer<GameObserver> notification) {
            Game.this.notifyObservers(notification);
        }
    }
}
