package Model.Player;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Map.Grid;
import Model.Weapons.WeaponType;

/** Joueur contrôlé par l'ordinateur. */
public class ComputerPlayer extends Player {
    private ShotStrategy shotStrategy;

    public ComputerPlayer(Grid ownGrid, Grid targetGrid) {
        super(ownGrid, targetGrid);
        this.shotStrategy = new TargetedShotStrategy();
    }

    @Override
    public HitOutcome fire(Coordinates coordinates) {
        return strikeTarget(coordinates);
    }

    public Coordinates chooseNextShot(HitOutcome lastOutcome) {
        return shotStrategy.getNextShot(getTargetGrid(), lastOutcome);
    }

    @Override
    public boolean hasAmmo(WeaponType type) {
        return true;
    }

    @Override
    public void consumeAmmo(WeaponType type) {
    }

    @Override
    public void addAmmo(WeaponType type) {
    }

    public void setShotStrategy(ShotStrategy shotStrategy) {
        this.shotStrategy = shotStrategy;
    }

    @Override
    public boolean isDefeated() {
        return areAllBoatsSunk();
    }

    public void notifyShotResult(Model.Coordinates target, Model.HitOutcome outcome) {
        shotStrategy.notifyShotResult(target, outcome);
    }
}
