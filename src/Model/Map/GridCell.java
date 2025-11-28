package Model.Map;

import Model.Coordinates;
import Model.GridEntity;
import Model.HitOutcome;
import Model.Player.Player;

public class GridCell {
    private GridEntity entity;
    private boolean isHit;

    public GridCell() {
        this.entity = null;
        this.isHit = false;
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

    public HitOutcome strike(Player attacker) {
        if (isHit) {
            return HitOutcome.INVALID;
        }

        this.isHit = true;

        if (!isOccupied()) {
            return HitOutcome.MISS;
        }


        Coordinates fakeCoords = new Coordinates(0, 0); // À améliorer

        System.out.println("🎯 Strike sur case avec coordonnées: " + fakeCoords);
        HitOutcome outcome = entity.handleImpact(attacker, fakeCoords);

        return outcome;
    }


    private int calculateHitIndex() {
        // Pour l'instant, on retourne un index basé sur la position de la cellule
        // Ce n'est pas parfait mais ça permettra de tester
        return (int) (Math.random() * entity.size());
    }
}