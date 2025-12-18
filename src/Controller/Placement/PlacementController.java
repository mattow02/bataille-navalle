package Controller.Placement;

import Controller.IGameControllerCoordinator;
import Model.Boat.Boat;
import Model.Coordinates;
import Model.Games.GameSetup;
import Model.GridEntity;
import Model.Orientation;
import View.Model.CellState;
import View.PlacementViewInterface;

import java.util.LinkedList;
import java.util.Queue;

/** Gère le placement des entités avant la bataille. */
public class PlacementController implements IPlacementController {
    private final IGameControllerCoordinator mainController;
    private final GameSetup game;
    private final Queue<GridEntity> itemsToPlace;
    private GridEntity currentItem;
    private Orientation currentOrientation;
    private PlacementViewInterface view;
    private Runnable onFinished;

    /** Initialise le contrôleur de placement. */
    public PlacementController(IGameControllerCoordinator mainController, GameSetup game) {
        this.mainController = mainController;
        this.game = game;
        this.currentOrientation = Orientation.HORIZONTAL;
        this.itemsToPlace = new LinkedList<>();
    }

    /** Associe la vue de placement. */
    public void setView(PlacementViewInterface view) {
        this.view = view;
    }

    /** Définit l'action à exécuter une fois le placement terminé. */
    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }

    /** Prépare la file des entités à placer. */
    public void initializeBoats() {
        itemsToPlace.clear();
        var boats = game.getBoatsToPlace();
        itemsToPlace.addAll(boats);
        var traps = game.getTrapsToPlace();
        itemsToPlace.addAll(traps);
        nextItem();
    }

    private void nextItem() {
        currentItem = itemsToPlace.poll();
        if (currentItem == null) {
            finishPlacement();
        } else {
            updateView();
        }
    }

    /** Traite un clic de placement sur la grille. */
    public void handleGridClick(int row, int col) {
        if (currentItem == null) return;
        var cord = new Coordinates(row, col);
        var success = game.placeEntityOnPlayerGridByType(currentItem, cord, currentOrientation);
        if (success) {
            updateView();
            nextItem();
        } else if (view != null) {
            view.showError(PlacementError.INVALID_PLACEMENT);
        }
    }

    private void finishPlacement() {
        currentItem = null;
        if (view != null) {
            view.showPlacementFinished();
        }
        if (onFinished != null) {
            if (view != null) {
                view.close();
            }
            onFinished.run();
        } else {
            startGame();
        }
    }

    /** Bascule l'orientation courante. */
    public void toggleOrientation() {
        currentOrientation = currentOrientation == Orientation.HORIZONTAL
                ? Orientation.VERTICAL
                : Orientation.HORIZONTAL;
        updateView();
    }

    private void updateView() {
        if (view != null) {
            view.refreshView();
        }
    }

    /** Lance la bataille après le placement. */
    public void startGame() {
        if (view != null) view.close();
        mainController.showBattleView();
    }

    /** Retourne l'état d'une cellule du joueur. */
    public CellState getCellState(int row, int col) {
        return mainController.getPlayerCellState(row, col);
    }

    /** Donne le nom de l'élément actuel à placer. */
    public String getCurrentItemName() {
        if (currentItem == null) {
            return "";
        }
        return currentItem.getDisplayName();
    }

    /** Donne la taille de l'élément actuel à placer. */
    public int getCurrentItemSize() {
        return currentItem != null ? currentItem.size() : 0;
    }

    /** Indique si l'orientation est horizontale. */
    public boolean isHorizontal() {
        return currentOrientation == Orientation.HORIZONTAL;
    }

    /** Retourne la taille de la grille de placement. */
    public int getGridSize() {
        return game.getGridSize();
    }
}
