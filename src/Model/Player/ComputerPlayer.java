package Model.Player;

import Model.Map.Grid;
import Model.Coordinates;
import Model.HitOutcome;

public class ComputerPlayer extends Player {
    private ShotStrategy shotStrategy;

    public ComputerPlayer(Grid ownGrid, Grid targetGrid) {
        super(ownGrid, targetGrid);
        this.shotStrategy = new TargetedShotStrategy(); //  Stratégie intelligente
    }


    @Override
    public HitOutcome fire(Coordinates coordinates) {
        System.out.println(" IA tire en " + coordinates);
        return targetGrid.getCell(coordinates).strike(this);
    }

    @Override
    public HitOutcome handleIncomingFire(Coordinates coordinates) {
        return ownGrid.getCell(coordinates).strike(null);
    }

    //  MÉTHODE UNIQUE : Choix du tir avec mémoire du dernier résultat
    public Coordinates chooseNextShot(HitOutcome lastOutcome) {
        return shotStrategy.getNextShot(targetGrid, lastOutcome);
    }

    // GETTER pour la stratégie
    public ShotStrategy getShotStrategy() {
        return shotStrategy;
    }

    @Override
    public boolean isDefeated() {
        return areAllBoatsSunk();
    }
}