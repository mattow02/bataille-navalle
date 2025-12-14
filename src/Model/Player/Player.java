package Model.Player;

import Model.Map.Grid;
import Model.Coordinates;
import Model.HitOutcome;
import Model.Boat.Boat;

public abstract class Player {
    protected Grid ownGrid;
    protected Grid targetGrid;

    public Player(Grid ownGrid, Grid targetGrid) {
        this.ownGrid = ownGrid;
        this.targetGrid = targetGrid;
    }

    public abstract boolean isDefeated();
    public abstract HitOutcome fire(Coordinates coordinates);

    public Grid getOwnGrid() { return ownGrid; }

    protected boolean areAllBoatsSunk() {
        int gridSize = ownGrid.getSize();

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Coordinates cord = new Coordinates(row, col);
                Model.Map.GridCell cell = ownGrid.getCell(cord);

                if (cell != null && cell.isOccupied()) {
                    if (cell.getEntity() instanceof Boat boat) {
                        if (!boat.isSunk()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public int getAliveBoatsCount() {
        java.util.Set<Model.Boat.Boat> uniqueBoats = new java.util.HashSet<>();
        int size = ownGrid.getSize();

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Model.Map.GridCell cell = ownGrid.getCell(new Model.Coordinates(r, c));
                if (cell.isOccupied() && cell.getEntity() instanceof Model.Boat.Boat) {
                    uniqueBoats.add((Model.Boat.Boat) cell.getEntity());
                }
            }
        }

        int aliveCount = 0;
        for (Model.Boat.Boat boat : uniqueBoats) {
            if (!boat.isSunk()) {
                aliveCount++;
            }
        }
        return aliveCount;
    }
}