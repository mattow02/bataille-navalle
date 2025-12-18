package Model.Island;

import Model.GridEntity;
import Model.HitOutcome;
import Model.Player.Player;

/** Décrit un item présent sur une case d'île. */
public interface IslandItem extends GridEntity {

    /** Applique l'effet d'acquisition de l'item. */
    HitOutcome onAcquired(Player player);

    @Override
    default HitOutcome handleImpact(Player attacker, int segmentIndex) {
        return onAcquired(attacker);
    }
}
