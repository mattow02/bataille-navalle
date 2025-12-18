package Model.Map;

import Model.GridEntity;
import Model.HitOutcome;
import Model.Player.Player;

/** Cellule individuelle d'une grille de jeu. */
public class GridCell {
    private GridEntity entity;
    private boolean isHit;
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

    public boolean isBoat() {
        return entity != null && entity.isBoat();
    }

    public boolean isBoatSunk() {
        if (!isBoat() || entity == null) return false;
        return entity.isSunk();
    }

    public boolean isDetectableBySonar() {
        return isOccupied() && entity.isDetectableBySonar();
    }

    public HitOutcome strike(Player attacker) {
        if (isHit) {
            return HitOutcome.INVALID;
        }

        this.isHit = true;

        if (!isOccupied()) return HitOutcome.MISS;
        return entity.handleImpact(attacker, boatSegmentIndex);
    }
}
