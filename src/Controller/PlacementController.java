package Controller;

import Model.Boat.Boat;
import Model.Boat.BoatFactory;
import Model.Boat.BoatType;
import Model.Boat.SimpleBoatFactory;
import Model.Coordinates;
import Model.GameConfiguration;
import Model.GridEntity;
import Model.Map.Grid;
import Model.Orientation;
import View.CellState;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class PlacementController implements Observer {
    private final GameController mainController;
    private final Grid playerGrid;
    // File d'attente des bateaux et pièges à placer
    private final Queue<GridEntity> itemsToPlace;
    private GridEntity currentItem;

    private Orientation currentOrientation;
    private View.PlacementView view;

    public PlacementController(GameController mainController) {
        this.mainController = mainController;
        this.playerGrid = mainController.getPlayerGrid();
        this.currentOrientation = Orientation.HORIZONTAL;
        this.itemsToPlace = new LinkedList<>();
    }

    public void setView(View.PlacementView view) {
        this.view = view;
    }

    // Initialise la file de placement avec tous les bateaux et pièges
    public void initializeBoats(GameConfiguration config) {
        BoatFactory factory = new SimpleBoatFactory();
        itemsToPlace.clear();

        // Ajoute tous les bateaux selon la configuration
        for (Map.Entry<BoatType, Integer> entry : config.getBoatCounts().entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                itemsToPlace.add(factory.create(entry.getKey()));
            }
        }

        // En mode standard, ajoute les pièges à placer
        if (!config.isIslandMode()) {
            itemsToPlace.add(new Model.Trap.BlackHole());
            itemsToPlace.add(new Model.Trap.Tornado());
        }

        nextItem();
    }

    private void nextItem() {
        currentItem = itemsToPlace.poll();

        if (currentItem == null) {
            finishPlacement();
        } else {
            notifyObservers("UPDATE_PLACEMENT");
        }
    }

    // Gère le clic sur la grille : place le bateau ou piège courant
    public void handleGridClick(int row, int col) {
        if (currentItem == null) return;

        Coordinates cord = new Coordinates(row, col);
        boolean success = false;

        // Placement différent selon le type (bateau multi-cases ou piège 1 case)
        if (currentItem instanceof Boat) {
            success = playerGrid.placeBoat((Boat) currentItem, cord, currentOrientation);
        } else {
            Model.Map.GridCell cell = playerGrid.getCell(cord);
            if (cell != null && !cell.isOccupied() && !cell.isIslandCell()) {
                cell.setEntity(currentItem);
                success = true;
            }
        }

        if (success) {
            notifyObservers("UPDATE_PLACEMENT");
            nextItem();
        } else {
            notifyObservers("INVALID_PLACEMENT");
        }
    }

    private void finishPlacement() {
        currentItem = null;
        JOptionPane.showMessageDialog(null, "Flotte et Pièges prêts ! La bataille commence...", "Déploiement terminé", JOptionPane.INFORMATION_MESSAGE);
        startGame();
    }

    public void toggleOrientation() {
        currentOrientation = (currentOrientation == Orientation.HORIZONTAL)
                ? Orientation.VERTICAL : Orientation.HORIZONTAL;
        notifyObservers("UPDATE_PLACEMENT");
    }

    public void startGame() {
        if (view != null) view.close();
        mainController.showBattleView();
    }

    public CellState getCellState(int row, int col) {
        return mainController.getPlayerCellState(row, col);
    }

    public String getCurrentItemName() {
        return switch (currentItem) {
            case null -> "";
            case Boat boat -> boat.name();
            case Model.Trap.BlackHole _ -> "Trou Noir (Piège)";
            case Model.Trap.Tornado _ -> "Tornade (Piège)";
            default -> "Objet Inconnu";
        };
    }

    public int getCurrentItemSize() {
        return currentItem != null ? currentItem.size() : 0;
    }

    public Orientation getOrientation() { return currentOrientation; }
    public int getGridSize() { return playerGrid.getSize(); }

    private final List<Observer> observers = new ArrayList<>();
    public void addObserver(Observer o) { observers.add(o); }
    private void notifyObservers(String msg) { for(Observer o : observers) o.update(msg); }
    @Override public void update(Object event) {}
}