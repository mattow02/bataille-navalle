package Model.Boat;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Player.Player;

public class TorpedoBoat implements Boat {
    private int size;
    private boolean[] hits;
    private boolean sunk;

    public TorpedoBoat() {
        this.size = 2;
        this.hits = new boolean[size];
        this.sunk = false;
    }

    @Override
    public HitOutcome handleImpact(Player attacker, Coordinates coordinates) {
        return HitOutcome.INVALID;
    }

    @Override
    public boolean isSunk() {
        return sunk;
    }

    @Override
    public HitOutcome receiveHit(int index) {
        if (index < 0 || index >= size) {
            return HitOutcome.INVALID;
        }

        hits[index] = true;

        sunk = true;
        for (boolean hit : hits) {
            if (!hit) {
                sunk = false;
                break;
            }
        }

        if (sunk) {
            return HitOutcome.SUNK;
        } else {
            return HitOutcome.HIT;
        }
    }

    @Override
    public String entityType() {
        return "BOAT";
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String name() {
        return "Torpilleur";
    }

    @Override
    public BoatType getType() {
        return BoatType.TORPEDO;
    }
}