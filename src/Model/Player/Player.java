package Model.Player;

import Model.Map.Grid;
import Model.Coordinates;
import Model.HitOutcome;

public abstract class Player {
    protected Grid ownGrid;
    protected Grid targetGrid;

    public Player(Grid ownGrid, Grid targetGrid) {
        this.ownGrid = ownGrid;
        this.targetGrid = targetGrid;
    }

    public abstract boolean isDefeated();
    public abstract HitOutcome fire(Coordinates coordinates);
    public abstract HitOutcome handleIncomingFire(Coordinates coordinates);

    public Grid getOwnGrid() { return ownGrid; }
    public Grid getTargetGrid() { return targetGrid; }

    protected boolean areAllBoatsSunk() {
        int gridSize = ownGrid.getSize();

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Coordinates coord = new Coordinates(row, col);
                Model.Map.GridCell cell = ownGrid.getCell(coord);

                if (cell != null && cell.isOccupied()) {
                    Model.Boat.Boat boat = (Model.Boat.Boat) cell.getEntity();
                    if (!boat.isSunk()) {
                        return false; // Au moins un bateau n'est pas coulé
                    }
                }
            }
        }
        return true; // Tous les bateaux sont coulés
    }
}