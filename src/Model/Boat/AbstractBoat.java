package Model.Boat;

import Model.HitOutcome;
import Model.Player.Player;

/** Base commune aux bateaux avec gestion des impacts. */
public abstract class AbstractBoat implements Boat {
    protected final BoatType type;
    protected final boolean[] hits;
    protected boolean sunk;

    /** Initialise un bateau avec son type. */
    public AbstractBoat(BoatType type) {
        this.type = type;
        this.hits = new boolean[type.getSize()];
        this.sunk = false;
    }

    @Override
    public HitOutcome receiveHit(int index) {
        if (index < 0 || index >= hits.length) {
            return HitOutcome.INVALID;
        }
        if (hits[index]) {
            return HitOutcome.INVALID;
        }
        hits[index] = true;
        checkIfSunk();
        return sunk ? HitOutcome.SUNK : HitOutcome.HIT;
    }

    private void checkIfSunk() {
        sunk = true;
        for (var hit : hits) {
            if (!hit) {
                sunk = false;
                break;
            }
        }
    }

    @Override
    public boolean isSunk() {
        return sunk;
    }

    @Override
    public int size() {
        return type.getSize();
    }

    @Override
    public String name() {
        return type.getName();
    }

    @Override
    public BoatType getType() {
        return type;
    }

    @Override
    public String entityType() {
        return "BOAT";
    }

    @Override
    public boolean isBoat() {
        return true;
    }

    @Override
    public boolean isDetectableBySonar() {
        return true;
    }

    @Override
    public HitOutcome handleImpact(Player attacker, int segmentIndex) {
        return receiveHit(segmentIndex);
    }

    @Override
    public String getDisplayName() {
        return name();
    }
}
