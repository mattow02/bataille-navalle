package Model.Boat;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Player.Player;

public class Destroyer implements Boat {
    final private int size;
    final private boolean[] hits;
    private boolean sunk;

    public Destroyer() {
        this.size = 3;
        this.hits = new boolean[size];
        this.sunk = false;
    }

    @Override
    public HitOutcome handleImpact(Player attacker, Coordinates coordinates) {
        for (int i = 0; i < size; i++) {
            if (!hits[i]) {
                return receiveHit(i);
            }
        }

        return receiveHit(0);
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
        return "Contre-Torpilleur";
    }

    @Override
    public BoatType getType() {
        return BoatType.DESTROYER;
    }
}