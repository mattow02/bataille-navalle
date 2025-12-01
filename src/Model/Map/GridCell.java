package Model.Map;

import Model.Coordinates;
import Model.GridEntity;
import Model.HitOutcome;
import Model.Player.Player;
import Model.Boat.Boat;
import Model.Trap.BlackHole;

public class GridCell {
    // Entité présente sur cette case (bateau, piège, item d'île)
    private GridEntity entity;
    private boolean isHit;
    // Index du segment du bateau (pour les bateaux multi-cases)
    private int boatSegmentIndex = -1;
    private boolean isIslandCell;

    public GridCell() {
        this.entity = null;
        this.isHit = false;
        this.isIslandCell = false;
    }

    public void setBoatSegmentIndex(int index) {
        this.boatSegmentIndex = index;
    }

    public boolean isIslandCell() {
        return isIslandCell;
    }

    public void setIsIslandCell(boolean isIslandCell) {
        this.isIslandCell = isIslandCell;
    }

    public boolean isOccupied() {
        return entity != null;
    }

    public GridEntity getEntity() {
        return entity;
    }

    public void setEntity(GridEntity entity) {
        this.entity = entity;
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean hit) {
        this.isHit = hit;
    }

    // Gère un tir sur cette case : bateau, piège ou item d'île
    public HitOutcome strike(Player attacker) {
        if (isHit) {
            return HitOutcome.INVALID;
        }

        this.isHit = true;

        if (!isOccupied()) {
            return HitOutcome.MISS;
        }

        // Bateau : utilise receiveHit avec l'index du segment
        if (entity instanceof Boat boat) {
            return boat.receiveHit(boatSegmentIndex);
        }

        // Piège ou item d'île : utilise handleImpact
        if (entity != null) {
            Coordinates fakeCoords = new Coordinates(0, 0);
            HitOutcome outcome = entity.handleImpact(attacker, fakeCoords);

            // Les pièges sont à usage unique
            if (outcome == HitOutcome.TRAP_TRIGGERED) {
                this.entity = null;
            }

            return outcome;
        }

        return HitOutcome.MISS;
    }
}