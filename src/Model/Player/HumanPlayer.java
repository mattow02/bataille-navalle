package Model.Player;

import Model.Map.Grid;
import Model.Coordinates;
import Model.HitOutcome;

public class HumanPlayer extends Player {
    private String name;

    public HumanPlayer(String name, Grid ownGrid, Grid targetGrid) {
        super(ownGrid, targetGrid);
        this.name = name;
    }


    @Override
    public HitOutcome fire(Coordinates coordinates) {
        return targetGrid.getCell(coordinates).strike(this);
    }

    @Override
    public HitOutcome handleIncomingFire(Coordinates coordinates) {
        return ownGrid.getCell(coordinates).strike(null);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isDefeated() {
        return areAllBoatsSunk();
    }
}