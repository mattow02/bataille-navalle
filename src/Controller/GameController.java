package Controller;

import Model.*;
import Model.GameConfiguration;
import Model.Player.ComputerPlayer;
import Model.Player.HumanPlayer;
import Model.Player.RandomShotStrategy;
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
    private final java.util.List<Observer> observers = new java.util.ArrayList<>();

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

    private void initializeGame() {
        // 1. Réinitialisation des compteurs (Pour le bouton Rejouer)
        this.turnCount = 1;
        this.computerTornadoTurnsLeft = 0;
        this.playerTornadoTurnsLeft = 0;
        this.lastComputerOutcome = null;
        this.isPlayerTurn = true;

        // 2. Création des grilles et joueurs
        this.playerGrid = new Grid(config.getGridSize());
        this.enemyGrid = new Grid(config.getGridSize());

        this.humanPlayer = new HumanPlayer("Joueur", playerGrid, enemyGrid);
        this.computerPlayer = new ComputerPlayer(enemyGrid, playerGrid);

        if (config.getComputerShotLevel() == 1) {
            computerPlayer.setShotStrategy(new RandomShotStrategy());
            log("🤖 IA configurée en mode FACILE (Aléatoire).");
        } else {
            computerPlayer.setShotStrategy(new TargetedShotStrategy());
            log("🤖 IA configurée en mode DIFFICILE (Chasse).");
        }

        this.battleController = new BattleController(this);

        // 3. Gestion du Mode Île
        if (config.isIslandMode()) {
            int islandStart = config.getGridSize() / 2 - 2;
            playerGrid.markIslandZone(islandStart, islandStart);
            enemyGrid.markIslandZone(islandStart, islandStart);
            placeIslandItemsRandomly(enemyGrid);
        }

        // 4. Gestion du Mode Standard (Pas d'île)
        if (!config.isIslandMode()) {
            humanPlayer.addBomb();
            humanPlayer.addSonar();
            log("Mode Standard : 1 Bombe et 1 Sonar ajoutés.");
        }

        // 5. Placement des bateaux IA
        BoatFactory factory = new SimpleBoatFactory();
        placeEnemyBoats(factory);

        // 6. Lancement du placement joueur
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
        List<Coordinates> islandCords = new java.util.ArrayList<>();

        for (int r = islandStart; r < islandStart + 4; r++) {
            for (int c = islandStart; c < islandStart + 4; c++) {
                Coordinates cord = new Coordinates(r, c);
                if (grid.getCell(cord).isIslandCell() && !grid.getCell(cord).isOccupied()) {
                    islandCords.add(cord);
                }
            }
        }

        List<Model.GridEntity> itemsToPlace = new java.util.ArrayList<>();
        itemsToPlace.add(new Model.Island.BombItem());
        itemsToPlace.add(new Model.Island.SonarItem());
        itemsToPlace.add(new Model.Trap.BlackHole());
        itemsToPlace.add(new Model.Trap.Tornado());

        java.util.Collections.shuffle(islandCords);

        int itemsCount = itemsToPlace.size();
        for (GridEntity gridEntity : itemsToPlace) {
            if (!islandCords.isEmpty()) {
                Coordinates cord = islandCords.removeFirst();
                grid.getCell(cord).setEntity(gridEntity);
            }
        }

        for (Coordinates cord : islandCords) {
            grid.getCell(cord).setEntity(new Model.Island.IslandEmpty());
        }

        log("Île initialisée. Total d'entités cachées: " + itemsCount);
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

    void placeBoatRandomly(Boat boat, Grid grid) {
        int attempts = 0;
        while (attempts < 100) {
            int row = (int) (Math.random() * grid.getSize());
            int col = (int) (Math.random() * grid.getSize());
            Orientation orientation = Math.random() > 0.5 ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            if (grid.placeBoat(boat, new Coordinates(row, col), orientation)) {
                return;
            }
            attempts++;
        }
    }

    // Gère l'attaque du joueur et passe le tour à l'IA après un délai
    public void playerAttacked( HitOutcome outcome) {
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

        // Délai de 0.5s avant le tour de l'IA pour la lisibilité
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        executeComputerTurn();
                    }
                },
                500
        );
    }


    // Exécute le tour de l'IA avec gestion de la tornade et mise à jour de la stratégie
    public void executeComputerTurn() {
        // Bloc de sécurité pour garantir que le jeu ne se fige jamais
        try {
            HitOutcome outcome;
            Coordinates target;

            // 1. Sélection de la cible par l'IA (selon sa stratégie actuelle)
            Coordinates chosenTarget = computerPlayer.chooseNextShot(lastComputerOutcome);
            target = chosenTarget;

            // 2. Application de l'effet Tornade (si actif sur l'IA)
            // Dévie le tir de +5 cases en X et Y.
            if (computerTornadoTurnsLeft > 0) {
                target = applyTornadoEffect(chosenTarget);
                computerTornadoTurnsLeft--;
                log("🌪️ Tornade active (Reste " + computerTornadoTurnsLeft + " tours). Tir ciblé en " + chosenTarget + " décalé en " + target);
            }

            // 3. Exécution du tir
            outcome = computerPlayer.fire(target);

            // 4. Construction du message de log pour l'interface
            String cordStr = (char) ('A' + target.getRow()) + "" + (target.getColumn() + 1);
            String msg = "L'IA attaque en " + cordStr;
            if (outcome == HitOutcome.HIT) msg += " : TOUCHÉ !";
            else if (outcome == HitOutcome.SUNK) msg += " : COULÉ !";
            else msg += " : Manqué.";
            log(msg);

            // Mise à jour de l'affichage
            notifyObservers("REFRESH_ALL");

            // 5. Vérification de la condition de victoire
            if (humanPlayer.isDefeated()) {
                log("TOUS vos bateaux sont coulés !");
                endGame(computerPlayer);
                return; // Fin du traitement
            }

            lastComputerOutcome = outcome;

           computerPlayer.notifyShotResult(target, outcome);

            Thread.sleep(500);

        } catch (Exception e) {
            System.err.println("Erreur durant le tour de l'IA : " + e.getMessage());

        } finally {

            isPlayerTurn = true;
        }
    }


    public void addObserver(Observer observer) {
        observers.add(observer);
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

        Coordinates cord = new Coordinates(row, col);
        Model.Map.GridCell cell = playerGrid.getCell(cord);

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

        Coordinates cord = new Coordinates(row, col);
        Model.Map.GridCell cell = enemyGrid.getCell(cord);

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
                if (cell.getEntity() instanceof Boat boat) {
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
    public Coordinates applyTornadoEffect(Coordinates originalCord) {
        int gridSize = 10;
        if (config != null) {
            gridSize = config.getGridSize();
        }

        int newRow = (originalCord.getRow() + 5) % gridSize;
        int newCol = (originalCord.getColumn() + 5) % gridSize;

        return new Coordinates(newRow, newCol);
    }

    private void placeTraps() {

        placeEntityRandomly(new Model.Trap.BlackHole(), enemyGrid);
        placeEntityRandomly(new Model.Trap.Tornado(), enemyGrid);
    }

    void placeEntityRandomly(GridEntity entity, Grid grid) {
        int attempts = 0;

        while (attempts < 100) {
            int row = (int) (Math.random() * grid.getSize());
            int col = (int) (Math.random() * grid.getSize());
            Coordinates startCord = new Coordinates(row, col);

            Model.Map.GridCell cell = grid.getCell(startCord);

            if (cell != null && !cell.isOccupied() && !cell.isIslandCell()) {
                cell.setEntity(entity);
                return;
            }
            attempts++;
        }
    }

    public int getPlayerAliveBoatsCount() {
        return humanPlayer.getAliveBoatsCount();
    }

    public int getComputerAliveBoatsCount() {
        return computerPlayer.getAliveBoatsCount();
    }
}