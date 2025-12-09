package Controller;

import Model.*;
import Model.GameConfiguration;
import Model.Player.ComputerPlayer;
import Model.Player.HumanPlayer;
import Model.Player.TargetedShotStrategy;
import View.BattleView;
import Model.Map.Grid;
import Model.Boat.*;
import View.CellState;

import java.util.List;
import java.util.Map;

public class GameController implements Observer {
    // Grilles de jeu du joueur et de l'ennemi
    private Grid playerGrid;
    private Grid enemyGrid;
    private BattleController battleController;
    private GameConfiguration config;
    private Model.Player.HumanPlayer humanPlayer;
    private Model.Player.ComputerPlayer computerPlayer;
    // Gestion des tours et effets spéciaux
    private boolean isPlayerTurn;
    private HitOutcome lastComputerOutcome = null;
    private int computerTornadoTurnsLeft = 0;
    private int playerTornadoTurnsLeft = 0;
    private int turnCount = 1;
    // Liste des observateurs pour le pattern Observer
    private java.util.List<Observer> observers = new java.util.ArrayList<>();

    public GameController() {
        this.playerGrid = new Grid(10);
        this.enemyGrid = new Grid(10);

        this.humanPlayer = new Model.Player.HumanPlayer("Joueur", playerGrid, enemyGrid);
        this.computerPlayer = new Model.Player.ComputerPlayer(enemyGrid, playerGrid);

        this.battleController = new BattleController(this);
        this.isPlayerTurn = true;
    }

    // Point d'entrée principal : affiche la vue de configuration
    public void startApplication() {
        showConfigurationView();
    }

    public int getTurnCount() {
        return turnCount;
    }
    public int getComputerTornadoTurnsLeft() {
        return computerTornadoTurnsLeft;
    }

    public void setComputerTornadoTurnsLeft(int computerTornadoTurnsLeft) {
        this.computerTornadoTurnsLeft = computerTornadoTurnsLeft;
    }

    public int getPlayerTornadoTurnsLeft() {
        return playerTornadoTurnsLeft;
    }

    public void setPlayerTornadoTurnsLeft(int playerTornadoTurnsLeft) {
        this.playerTornadoTurnsLeft = playerTornadoTurnsLeft;
    }

    private void showConfigurationView() {
        ConfigurationController configController = new ConfigurationController(this);
        View.ConfigurationView configView = new View.ConfigurationView(configController);
        configView.display();
    }

    // Appelé quand la configuration est terminée, initialise le jeu
    public void handleConfigurationComplete(GameConfiguration config) {
        this.config = config;
        initializeGame();
    }

    // Envoie un message de log aux observateurs (InfoPanel)
    public void log(String message) {
        notifyObservers("LOG:" + message);
    }

    // Initialise toutes les composantes du jeu selon la configuration
    private void initializeGame() {
        this.turnCount = 1;
        this.computerTornadoTurnsLeft = 0;
        this.playerTornadoTurnsLeft = 0;
        this.lastComputerOutcome = null;
        this.isPlayerTurn = true;

        this.playerGrid = new Grid(config.getGridSize());
        this.enemyGrid = new Grid(config.getGridSize());

        this.humanPlayer = new HumanPlayer("Joueur", playerGrid, enemyGrid);
        this.computerPlayer = new ComputerPlayer(enemyGrid, playerGrid);

        this.battleController = new BattleController(this);
        this.isPlayerTurn = true;

        // Mode île : crée une zone 4x4 avec items et pièges
        if (config.isIslandMode()) {
            int islandStart = config.getGridSize() / 2 - 2;
            playerGrid.markIslandZone(islandStart, islandStart);
            enemyGrid.markIslandZone(islandStart, islandStart);
            placeIslandItemsRandomly(enemyGrid);
        }

        // Mode standard : donne des armes par défaut au joueur
        if (!config.isIslandMode()) {
            humanPlayer.addBomb();
            humanPlayer.addSonar();
            log("Mode Standard : 1 Bombe et 1 Sonar ajoutés.");
        }

        BoatFactory factory = new SimpleBoatFactory();
        placeEnemyBoats(factory);

        startManualPlacement();
    }


    private void startManualPlacement() {
        PlacementController placementController = new PlacementController(this);
        placementController.initializeBoats(config);
        View.PlacementView placementView = new View.PlacementView(placementController);
        placementController.setView(placementView);

        placementView.display();
    }


    private void placeIslandItemsRandomly(Grid grid) {
        int gridSize = grid.getSize();
        int islandStart = gridSize / 2 - 2;
        List<Coordinates> islandCoords = new java.util.ArrayList<>();

        for (int r = islandStart; r < islandStart + 4; r++) {
            for (int c = islandStart; c < islandStart + 4; c++) {
                Coordinates coord = new Coordinates(r, c);
                if (grid.getCell(coord).isIslandCell() && !grid.getCell(coord).isOccupied()) {
                    islandCoords.add(coord);
                }
            }
        }

        List<Model.GridEntity> itemsToPlace = new java.util.ArrayList<>();
        itemsToPlace.add(new Model.Island.BombItem());
        itemsToPlace.add(new Model.Island.SonarItem());
        itemsToPlace.add(new Model.Trap.BlackHole());
        itemsToPlace.add(new Model.Trap.Tornado());

        java.util.Collections.shuffle(islandCoords);

        int itemsCount = itemsToPlace.size();
        for (int i = 0; i < itemsCount; i++) {
            if (!islandCoords.isEmpty()) {
                Coordinates coord = islandCoords.remove(0);
                grid.getCell(coord).setEntity(itemsToPlace.get(i));
            }
        }

        for (Coordinates coord : islandCoords) {
            grid.getCell(coord).setEntity(new Model.Island.IslandEmpty());
        }

        log("Île initialisée. Total d'entités cachées: " + itemsCount);
    }

    private void placeBoatsFromConfiguration() {
        BoatFactory factory = new SimpleBoatFactory();
        placePlayerBoats(factory);
        placeEnemyBoats(factory);
    }

    private void placePlayerBoats(BoatFactory factory) {
        for (Map.Entry<BoatType, Integer> entry : config.getBoatCounts().entrySet()) {
            BoatType type = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                Boat boat = factory.create(type);
                placeBoatRandomly(boat, playerGrid);
            }
        }
    }

    private void placeEnemyBoats(BoatFactory factory) {
        for (Map.Entry<BoatType, Integer> entry : config.getBoatCounts().entrySet()) {
            BoatType type = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                Boat boat = factory.create(type);
                placeBoatRandomly(boat, enemyGrid);
            }
        }
    }

    boolean placeBoatRandomly(Boat boat, Grid grid) {
        int attempts = 0;
        while (attempts < 100) {
            int row = (int) (Math.random() * grid.getSize());
            int col = (int) (Math.random() * grid.getSize());
            Orientation orientation = Math.random() > 0.5 ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            if (grid.placeBoat(boat, new Coordinates(row, col), orientation)) {
                return true;
            }
            attempts++;
        }
        return false;
    }

    // Gère l'attaque du joueur et passe le tour à l'IA après un délai
    public void playerAttacked(int row, int col, HitOutcome outcome) {
        if (!isPlayerTurn) {
            return;
        }
        notifyObservers("REFRESH_ALL");

        isPlayerTurn = false;
        turnCount++;

        // Vérifie si l'IA est vaincue après un bateau coulé
        if (outcome == HitOutcome.SUNK) {
            if (computerPlayer.isDefeated()) {
                endGame(humanPlayer);
                return;
            }
        }

        // Délai de 1.5s avant le tour de l'IA pour la lisibilité
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        executeComputerTurn();
                    }
                },
                1500
        );
    }


    // Exécute le tour de l'IA avec gestion de la tornade et mise à jour de la stratégie
    public void executeComputerTurn() {
        HitOutcome outcome;
        Coordinates target;

        Coordinates chosenTarget = computerPlayer.chooseNextShot(lastComputerOutcome);
        target = chosenTarget;

        // Applique l'effet tornade si actif (dévie le tir de 5 cases)
        if (computerTornadoTurnsLeft > 0) {
            target = applyTornadoEffect(chosenTarget);
            computerTornadoTurnsLeft--;
            log("🌪️ Tornade active (Reste " + computerTornadoTurnsLeft + " tours). Tir ciblé en " + chosenTarget + " décalé en " + target);
        }

        outcome = computerPlayer.fire(target);

        String coordStr = (char)('A' + target.getRow()) + "" + (target.getColumn() + 1);
        String msg = "L'IA attaque en " + coordStr;
        if (outcome == HitOutcome.HIT) msg += " : TOUCHÉ !";
        else if (outcome == HitOutcome.SUNK) msg += " : COULÉ !";
        else msg += " : Manqué.";
        log(msg);

        notifyObservers("REFRESH_ALL");

        // Vérifie si le joueur est vaincu
        if (humanPlayer.isDefeated()) {
            log("TOUS vos bateaux sont coulés !");
            endGame(computerPlayer);
            return;
        }

        lastComputerOutcome = outcome;

        // Met à jour la stratégie ciblée si l'IA a touché
        if (outcome == HitOutcome.HIT || outcome == HitOutcome.SUNK) {
            TargetedShotStrategy strategy = (TargetedShotStrategy) computerPlayer.getShotStrategy();
            strategy.setLastHit(target);
        }

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        isPlayerTurn = true;
    }


    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    private void notifyObservers(Object event) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            for (Observer observer : observers) {
                observer.update(event);
            }
        });
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public Model.Player.HumanPlayer getHumanPlayer() {
        return humanPlayer;
    }

    public Model.Player.ComputerPlayer getComputerPlayer() {
        return computerPlayer;
    }

    public void showBattleView() {
        if (config != null && !config.isIslandMode()) {
            placeTraps();
        }
        BattleView battleView = new BattleView(this, enemyGrid.getSize());
        battleView.display();
    }

    public CellState getCellState(int row, int col) {
        if (enemyGrid == null) return CellState.WATER;

        Coordinates coord = new Coordinates(row, col);
        Model.Map.GridCell cell = enemyGrid.getCell(coord);

        if (cell == null) return CellState.WATER;

        return CellState.WATER;
    }

    public CellState getCellStateAfterAttack(int row, int col) {
        if (enemyGrid == null) return CellState.WATER;

        Coordinates coord = new Coordinates(row, col);
        Model.Map.GridCell cell = enemyGrid.getCell(coord);

        if (cell == null) return CellState.WATER;

        if (!cell.isHit()) {
            return CellState.WATER;
        }

        if (!cell.isOccupied()) {
            return CellState.MISS;
        } else {
            return CellState.HIT;
        }
    }

    public Grid getGrid() {
        return enemyGrid;
    }

    public Grid getPlayerGrid() {
        return playerGrid;
    }

    public int getGridSize() {
        return enemyGrid.getSize();
    }

    public CellState getPlayerCellState(int row, int col) {
        if (playerGrid == null) return CellState.WATER;
        Coordinates coord = new Coordinates(row, col);
        Model.Map.GridCell cell = playerGrid.getCell(coord);
        if (cell == null) return CellState.WATER;

        if (cell.isIslandCell()) return CellState.ISLAND;

        if (!cell.isHit()) {
            if (cell.isOccupied()) {
                if (cell.getEntity() instanceof Model.Boat.Boat) {
                    return CellState.BOAT;
                } else {
                    return CellState.TRAP;
                }
            }
            return CellState.WATER;
        }

        if (!cell.isOccupied()) return CellState.MISS;

        if (cell.getEntity() instanceof Model.Boat.Boat) {
            if (((Model.Boat.Boat) cell.getEntity()).isSunk()) return CellState.SUNK;
        }
        return CellState.HIT;
    }

    public CellState getEnemyCellState(int row, int col) {
        if (enemyGrid == null) return CellState.WATER;

        Coordinates coord = new Coordinates(row, col);
        Model.Map.GridCell cell = enemyGrid.getCell(coord);

        if (cell == null) return CellState.WATER;

        if (cell.isIslandCell()) {
            if (!cell.isHit()) {
                return CellState.ISLAND;
            } else {
                if (cell.isOccupied()) {
                    return CellState.ITEM_FOUND;
                } else {
                    return CellState.EXPLORED;
                }
            }
        }

        if (!cell.isHit()) {
            return CellState.WATER;
        } else {
            if (!cell.isOccupied()) {
                return CellState.MISS;
            } else {
                if (cell.getEntity() instanceof Model.Boat.Boat) {
                    Model.Boat.Boat boat = (Model.Boat.Boat) cell.getEntity();
                    if (boat.isSunk()) {
                        return CellState.SUNK;
                    }
                }
                return CellState.HIT;
            }
        }
    }

    public CellState getEnemyCellStateAfterAttack(int row, int col) {
        return getEnemyCellState(row, col);
    }

    @Override
    public void update(Object event) {
    }

    public BattleController getBattleController() {
        return battleController;
    }

    private void endGame(Model.Player.Player winner) {
        if (winner == humanPlayer) {
            notifyObservers("GAME_OVER:VOUS_AVEZ_GAGNÉ");
        } else {
            notifyObservers("GAME_OVER:L_IA_A_GAGNÉ");
        }

        isPlayerTurn = false;
    }

    // Dévie les coordonnées de 5 cases avec wrapping (effet tornade)
    public Coordinates applyTornadoEffect(Coordinates originalCoord) {
        int gridSize = 10;
        if (config != null) {
            gridSize = config.getGridSize();
        }

        int newRow = (originalCoord.getRow() + 5) % gridSize;
        int newCol = (originalCoord.getColumn() + 5) % gridSize;

        return new Coordinates(newRow, newCol);
    }

    private void placeTraps() {

        placeEntityRandomly(new Model.Trap.BlackHole(), enemyGrid);
        placeEntityRandomly(new Model.Trap.Tornado(), enemyGrid);
    }

    boolean placeEntityRandomly(Model.GridEntity entity, Grid grid) {
        int attempts = 0;

        while (attempts < 100) {
            int row = (int) (Math.random() * grid.getSize());
            int col = (int) (Math.random() * grid.getSize());
            Coordinates startCoord = new Coordinates(row, col);

            Model.Map.GridCell cell = grid.getCell(startCoord);

            if (cell != null && !cell.isOccupied() && !cell.isIslandCell()) {
                cell.setEntity(entity);
                return true;
            }
            attempts++;
        }
        return false;
    }
}