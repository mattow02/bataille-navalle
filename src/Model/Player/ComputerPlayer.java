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

    // Choisit la prochaine cible selon le résultat du dernier tir
    public Coordinates chooseNextShot(HitOutcome lastOutcome) {
        return shotStrategy.getNextShot(targetGrid, lastOutcome);
    }

    public void setShotStrategy(ShotStrategy shotStrategy) {
        this.shotStrategy = shotStrategy;
    }

    @Override
    public boolean isDefeated() {
        return areAllBoatsSunk();
    }

    public void notifyShotResult(Model.Coordinates target, Model.HitOutcome outcome) {
        if (outcome == Model.HitOutcome.HIT || outcome == Model.HitOutcome.SUNK) {
            if (shotStrategy instanceof TargetedShotStrategy) {
                ((TargetedShotStrategy) shotStrategy).setLastHit(target);
            }
        }
    }
}