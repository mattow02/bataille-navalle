package Model.Player;

import Model.Map.Grid;
import Model.Coordinates;
import Model.HitOutcome;

public class ComputerPlayer extends Player {
    // Stratégie de tir intelligente (ciblée autour des touches)
    private ShotStrategy shotStrategy;

    public ComputerPlayer(Grid ownGrid, Grid targetGrid) {
        super(ownGrid, targetGrid);
        this.shotStrategy = new TargetedShotStrategy();
    }

    @Override
    public HitOutcome fire(Coordinates coordinates) {
        return targetGrid.getCell(coordinates).strike(this);
    }

    @Override
    public HitOutcome handleIncomingFire(Coordinates coordinates) {
        return ownGrid.getCell(coordinates).strike(null);
    }

    // Choisit la prochaine cible selon le résultat du dernier tir
    public Coordinates chooseNextShot(HitOutcome lastOutcome) {
        return shotStrategy.getNextShot(targetGrid, lastOutcome);
    }

    public ShotStrategy getShotStrategy() {
        return shotStrategy;
    }

    @Override
    public boolean isDefeated() {
        return areAllBoatsSunk();
    }
}