package Model.Trap;

import Model.Coordinates;
import Model.GridEntity;
import Model.HitOutcome;
import Model.Player.Player;

public class BlackHole implements GridEntity {
    private final int size = 1;
    private boolean isTriggered = false;

    @Override
    public HitOutcome handleImpact(Player attacker, Coordinates coordinates) {
        if (!isTriggered) {
            isTriggered = true;
            return onTriggered();
        }
        return HitOutcome.MISS;
    }

    public HitOutcome onTriggered() {
        return HitOutcome.TRAP_TRIGGERED;
    }

    @Override
    public String entityType() {
        return "TRAP_BLACK_HOLE";
    }

    @Override
    public int size() {
        return size;
    }
}