package Controller;

import Model.Game.GameConfiguration;
import Model.HitOutcome;
import Model.Player.ComputerPlayer;
import Model.Player.HumanPlayer;
import Model.Player.TargetedShotStrategy;
import View.BattleView;
import Model.Map.Grid;
import Model.Boat.*;
import Model.Coordinates;
import Model.Orientation;
import View.CellState;
import View.PlacementView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameController implements Observer {
    private Grid playerGrid;
    private Grid enemyGrid;
    private BattleController battleController;
    private GameConfiguration config;
    private Model.Player.HumanPlayer humanPlayer;
    private Model.Player.ComputerPlayer computerPlayer;
    private boolean isPlayerTurn;
    private HitOutcome lastComputerOutcome = null;

    public GameController() {


        this.playerGrid = new Grid(10);
        this.enemyGrid = new Grid(10);

        this.humanPlayer = new Model.Player.HumanPlayer("Joueur", playerGrid, enemyGrid);
        this.computerPlayer = new Model.Player.ComputerPlayer(enemyGrid, playerGrid);

        this.battleController = new BattleController(this);
        this.isPlayerTurn = true; // Le joueur commence
    }

    public void startApplication() {


        showConfigurationView();
    }

    private void showConfigurationView() {

        ConfigurationController configController = new ConfigurationController(this);
        View.ConfigurationView configView = new View.ConfigurationView(configController);
        configView.display();
    }

    private void showPlacementView() {


        // Créer la liste des bateaux à placer
        List<Boat> boatsToPlace = new ArrayList<>();
        BoatFactory factory = new SimpleBoatFactory();

        for (Map.Entry<BoatType, Integer> entry : config.getBoatCounts().entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                boatsToPlace.add(factory.create(entry.getKey()));
            }
        }

        PlacementController placementController = new PlacementController(this, boatsToPlace);
        PlacementView placementView = new PlacementView(placementController, boatsToPlace);
        placementView.display();
    }

    // Modifiez handleConfigurationComplete pour utiliser le placement :
    public void handleConfigurationComplete(GameConfiguration config) {

        this.config = config;
        initializeGame();
        showPlacementView(); //   MAINTENANT ON PASSE PAR L'ÉCRAN DE PLACEMENT
    }

    // MÉTHODE : Initialiser le jeu avec la configuration
    private void initializeGame() {

        // Réinitialiser les grilles
        this.playerGrid = new Grid(config.getGridSize());
        this.enemyGrid = new Grid(config.getGridSize());

        // Réinitialiser les joueurs
        this.humanPlayer = new HumanPlayer("Joueur", playerGrid, enemyGrid);
        this.computerPlayer = new ComputerPlayer(enemyGrid, playerGrid);

        this.battleController = new BattleController(this);
        this.isPlayerTurn = true;

        // Placer les bateaux selon la configuration
        placeBoatsFromConfiguration();
    }

    //  MÉTHODE : Placer les bateaux selon la configuration
    private void placeBoatsFromConfiguration() {

        BoatFactory factory = new SimpleBoatFactory();

        //PLACER LES BATEAUX DU JOUEUR

        placePlayerBoats(factory);

        // PLACER LES BATEAUX ENNEMIS
        System.out.println("Placement des bateaux ennemis:");
        placeEnemyBoats(factory);
    }

    //  MÉTHODE : Placer les bateaux du joueur
    private void placePlayerBoats(BoatFactory factory) {
        for (Map.Entry<BoatType, Integer> entry : config.getBoatCounts().entrySet()) {
            BoatType type = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                Boat boat = factory.create(type);
                boolean placed = placeBoatRandomly(boat, playerGrid);
                System.out.println(placed ?
                        boat.name() + " placé" :
                        boat.name() + " échec placement");
            }
        }
    }

    //  MÉTHODE : Placer les bateaux ennemis
    private void placeEnemyBoats(BoatFactory factory) {
        for (Map.Entry<BoatType, Integer> entry : config.getBoatCounts().entrySet()) {
            BoatType type = entry.getKey();
            int count = entry.getValue();

            for (int i = 0; i < count; i++) {
                Boat boat = factory.create(type);
                boolean placed = placeBoatRandomly(boat, enemyGrid);
                System.out.println(placed ?
                        " Ennemi " + boat.name() + " placé" :
                        " Ennemi " + boat.name() + " échec placement");
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

    private void testPlayerBoatPlacement() {
        BoatFactory factory = new SimpleBoatFactory();

        Boat carrier = factory.create(BoatType.AIRCRAFT_CARRIER);
        boolean placed1 = playerGrid.placeBoat(carrier, new Coordinates(0, 0), Orientation.HORIZONTAL);

        Boat torpedo = factory.create(BoatType.TORPEDO);
        boolean placed2 = playerGrid.placeBoat(torpedo, new Coordinates(5, 5), Orientation.VERTICAL);

    }

    private void testEnemyBoatPlacement() {

        BoatFactory factory = new SimpleBoatFactory();

        // Placer des bateaux ennemis à des positions différentes
        Boat enemyCarrier = factory.create(BoatType.AIRCRAFT_CARRIER);
        boolean placed1 = enemyGrid.placeBoat(enemyCarrier, new Coordinates(2, 2), Orientation.HORIZONTAL);


        Boat enemyTorpedo = factory.create(BoatType.TORPEDO);
        boolean placed2 = enemyGrid.placeBoat(enemyTorpedo, new Coordinates(7, 7), Orientation.VERTICAL);


        Boat enemyCruiser = factory.create(BoatType.CRUISER);
        boolean placed3 = enemyGrid.placeBoat(enemyCruiser, new Coordinates(4, 1), Orientation.VERTICAL);

    }

    public void playerAttacked(int row, int col, HitOutcome outcome) {
        if (!isPlayerTurn) {

            return;
        }

        System.out.println(" Vous avez attaqué en (" + row + "," + col + ")");

        // Le joueur a joué, passer à l'IA
        isPlayerTurn = false;

        // GÉRER L'AFFICHAGE ET LA DÉTECTION DE VICTOIRE
        if (outcome == HitOutcome.SUNK) {
            System.out.println("🔥 Bateau ennemi coulé -> rafraîchissement complet");
            notifyObservers("REFRESH_ALL");

            // VÉRIFIER SI VOUS AVEZ GAGNÉ
            if (computerPlayer.isDefeated()) {
                System.out.println(" TOUS les bateaux ennemis sont coulés !");
                endGame(humanPlayer); //
                return; // La partie s'arrête ici
            }
        }

        // Lancer le tour de l'IA après un délai
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


    public void executeComputerTurn() {
        System.out.println(" Tour de l'IA...");

        Coordinates target = computerPlayer.chooseNextShot(lastComputerOutcome);
        System.out.println("IA tire en " + (char)('A' + target.getRow()) + (target.getColumn() + 1));

        HitOutcome outcome = computerPlayer.fire(target);


        switch(outcome) {
            case HIT: System.out.println(" Touché"); break;
            case MISS: System.out.println("Manqué"); break;
            case SUNK: System.out.println("Coulé !"); break;
        }


        lastComputerOutcome = outcome;

        if (outcome == HitOutcome.HIT || outcome == HitOutcome.SUNK) {
            TargetedShotStrategy strategy = (TargetedShotStrategy) computerPlayer.getShotStrategy();
            strategy.setLastHit(target);
            System.out.println("IA: Stratégie mise à jour avec touché en " + target);
        }

        // Mise à jour affichage
        if (outcome == HitOutcome.SUNK) {
            System.out.println("Bateau coulé ");
            notifyObservers("REFRESH_ALL");

            //  DÉTECTION FIN DE PARTIE : Vérifier si le joueur a perdu
            if (humanPlayer.isDefeated()) {
                System.out.println("TOUS vos bateaux sont coulés !");
                endGame(computerPlayer); // L'IA a gagné
                return; // Arrêter ici, la partie est finie
            }
        } else {
            notifyObservers("PLAYER_GRID_UPDATE:" + target.getRow() + ":" + target.getColumn());
        }

        try { Thread.sleep(1000); } catch (InterruptedException e) {}

        isPlayerTurn = true;
        System.out.println("🎮 À vous de jouer");
    }

    private java.util.List<Observer> observers = new java.util.ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    private void notifyObservers(Object event) {
        for (Observer observer : observers) {
            observer.update(event);
        }
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    //  GETTERS pour les joueurs
    public Model.Player.HumanPlayer getHumanPlayer() {
        return humanPlayer;
    }

    public Model.Player.ComputerPlayer getComputerPlayer() {
        return computerPlayer;
    }

    void showBattleView() {

        BattleView battleView = new BattleView(this, enemyGrid.getSize()); // Taille de la grille ennemie
        battleView.display();
    }

    // ÉTAT INITIAL : Pour l'instant on affiche toujours de l'eau (grille ennemie cachée)
    public CellState getCellState(int row, int col) {
        if (enemyGrid == null) return CellState.WATER;

        Coordinates coord = new Coordinates(row, col);
        Model.Map.GridCell cell = enemyGrid.getCell(coord);

        if (cell == null) return CellState.WATER;

        // GRILLE ENNEMIE : toujours afficher de l'eau (bateaux cachés)
        return CellState.WATER;
    }

    //  ÉTAT APRÈS ATTAQUE : Afficher le résultat du tir
    public CellState getCellStateAfterAttack(int row, int col) {
        if (enemyGrid == null) return CellState.WATER;

        Coordinates coord = new Coordinates(row, col);
        Model.Map.GridCell cell = enemyGrid.getCell(coord);

        if (cell == null) return CellState.WATER;

        if (!cell.isHit()) {
            return CellState.WATER; // Pas encore tiré
        }

        // Tir effectué - afficher le résultat
        if (!cell.isOccupied()) {
            return CellState.MISS; // Manqué
        } else {
            return CellState.HIT;  // Touché
        }
    }

    //  GETTER pour BattleController (retourne enemyGrid - où on tire)
    public Grid getGrid() {
        return enemyGrid;
    }

    // GETTER pour votre grille (au cas où)
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

        if (!cell.isHit()) {
            // Pas encore tiré - afficher bateau ou eau
            return cell.isOccupied() ? CellState.BOAT : CellState.WATER;
        }

        // Case déjà touchée
        if (!cell.isOccupied()) {
            return CellState.MISS; // Manqué
        } else {
            //  VÉRIFIER SI LE BATEAU EST COULÉ
            Model.Boat.Boat boat = (Model.Boat.Boat) cell.getEntity();
            if (boat != null && boat.isSunk()) {
                return CellState.SUNK; // BATEAU COULÉ - NOIR
            } else {
                return CellState.HIT;  //  BATEAU TOUCHÉ - ROUGE
            }
        }
    }
    public CellState getEnemyCellState(int row, int col) {
        if (enemyGrid == null) return CellState.WATER;

        Coordinates coord = new Coordinates(row, col);
        Model.Map.GridCell cell = enemyGrid.getCell(coord);

        if (cell == null) return CellState.WATER;

        if (!cell.isHit()) {
            // Grille ennemie : toujours eau si pas tiré (bateaux cachés)
            return CellState.WATER;
        } else {
            // Case déjà touchée
            if (!cell.isOccupied()) {
                return CellState.MISS; // Manqué
            } else {
                //  VÉRIFIER SI LE BATEAU EST COULÉ
                Model.Boat.Boat boat = (Model.Boat.Boat) cell.getEntity();

                // AJOUT DES LOGS DE DEBUG
                System.out.println(" Case (" + row + "," + col + ") - Hash: " + System.identityHashCode(boat));
                System.out.println("Coulé: " + (boat != null ? boat.isSunk() : "boat null"));

                if (boat != null && boat.isSunk()) {
                    System.out.println(" bateau coulé");
                    return CellState.SUNK; // BATEAU COULÉ - NOIR
                } else {
                    System.out.println("bateau touché mais pas coulé");
                    return CellState.HIT;  //  BATEAU TOUCHÉ - ROUGE
                }
            }
        }
    }

    public CellState getEnemyCellStateAfterAttack(int row, int col) {
        // Même logique que getEnemyCellState
        return getEnemyCellState(row, col);
    }

    @Override
    public void update(Object event) {
    }

    public BattleController getBattleController() {
        return battleController;
    }

    //  AJOUTEZ CETTE MÉTHODE dans GameController
    private void endGame(Model.Player.Player winner) {
        System.out.println(" PARTIE TERMINÉE !");

        if (winner == humanPlayer) {
            System.out.println(" VOUS AVEZ GAGNÉ ! Félicitations !");
            notifyObservers("GAME_OVER:VOUS_AVEZ_GAGNÉ");
        } else {
            System.out.println(" L'IA A GAGNÉ ! Essayez encore !");
            notifyObservers("GAME_OVER:L_IA_A_GAGNÉ");
        }

        // Optionnel : désactiver les clics
        isPlayerTurn = false;
    }
}