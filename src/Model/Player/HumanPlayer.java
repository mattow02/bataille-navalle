package Model.Player;

import Model.Map.Grid;
import Model.HitOutcome;
import Model.Coordinates;

public class HumanPlayer extends Player {
    private final String name;
    // Inventaire des armes spéciales
    private int bombCount = 0;
    private int sonarCount = 0;

    public HumanPlayer(String name, Grid ownGrid, Grid targetGrid) {
        super(ownGrid, targetGrid);
        this.name = name;
    }

    public void addBomb() {
        this.bombCount++;
    }

    public boolean hasBomb() {
        return bombCount > 0;
    }

    public void useBomb() {
        if (bombCount > 0) bombCount--;
    }

    public int getBombCount() { return bombCount; }


    public void addSonar() {
        this.sonarCount++;
    }

    public boolean hasSonar() {
        return sonarCount > 0;
    }

    public void useSonar() {
        if (sonarCount > 0) sonarCount--;
    }

    public int getSonarCount() { return sonarCount; }

    @Override
    public HitOutcome fire(Coordinates coordinates) {
        return targetGrid.getCell(coordinates).strike(this);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isDefeated() {
        return areAllBoatsSunk();
    }

    // Vérifie si le sous-marin est encore en vie (nécessaire pour utiliser le sonar)
    public boolean isSubmarineAlive() {
        Grid grid = getOwnGrid();
        for(int r=0; r<grid.getSize(); r++) {
            for(int c=0; c<grid.getSize(); c++) {
                Model.Map.GridCell cell = grid.getCell(new Model.Coordinates(r, c));
                if (cell.isOccupied() && cell.getEntity() instanceof Model.Boat.Submarine sub) {
                    if (!sub.isSunk()) return true;
                }
            }
        }
        return false;
    }
}