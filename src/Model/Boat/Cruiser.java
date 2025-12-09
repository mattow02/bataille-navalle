package Model.Boat;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Player.Player;

public class Cruiser implements Boat {
    private final int size = 4;
    private boolean[] hits;
    private boolean sunk;

    public Cruiser() {
        this.hits = new boolean[size];
        this.sunk = false;
    }

    @Override
    public HitOutcome handleImpact(Player attacker, Coordinates coordinates) {
        return HitOutcome.INVALID;
    }

    @Override
    public String entityType() {
        return "";
    }

    @Override
    public boolean isSunk() {
        return sunk;
    }

    @Override
    public HitOutcome receiveHit(int index) {
        if (index < 0 || index >= size || hits[index]) {
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
    public int size() {
        return size;
    }

    @Override
    public String name() {
        return BoatType.CRUISER.getName();
    }

    @Override
    public BoatType getType() {
        return BoatType.CRUISER;
    }
}