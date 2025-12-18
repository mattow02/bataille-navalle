package Model.Island;

import Model.HitOutcome;
import Model.Player.Player;
import Model.SpecialEffectContext;
import Model.SpecialEffectEntity;

/** Case d'île vide sans effet. */
public class IslandEmpty implements IslandItem, SpecialEffectEntity {

    @Override
    public HitOutcome onAcquired(Player player) {
        return HitOutcome.MISS;
    }

    @Override
    public String entityType() {
        return "ISLAND_EMPTY";
    }

    @Override
    public String getDisplayName() {
        return "Zone Explorée";
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void applySpecialEffect(SpecialEffectContext context, Model.Coordinates target, boolean isHumanAttacker) {
    }
}
