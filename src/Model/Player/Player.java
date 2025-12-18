package Model.Player;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Map.Grid;
import Model.Weapons.WeaponType;

/** Représente un joueur humain ou IA. */
public abstract class Player {
    private final Grid ownGrid;
    private final Grid targetGrid;

    public Player(Grid ownGrid, Grid targetGrid) {
        this.ownGrid = ownGrid;
        this.targetGrid = targetGrid;
    }

    public abstract boolean isDefeated();

    public abstract HitOutcome fire(Coordinates coordinates);

    public abstract boolean hasAmmo(WeaponType type);

    public abstract void consumeAmmo(WeaponType type);

    public abstract void addAmmo(WeaponType type);

    protected HitOutcome strikeTarget(Coordinates coordinates) {
        return targetGrid.strikeCell(coordinates, this);
    }

    protected Grid getTargetGrid() {
        return targetGrid;
    }

    protected boolean areAllBoatsSunk() {
        return ownGrid.areAllBoatsSunk();
    }

    public int getAliveBoatsCount() {
        return ownGrid.getAliveBoatsCount();
    }

    public boolean isSubmarineAlive() {
        return ownGrid.isSubmarineAlive();
    }
}
