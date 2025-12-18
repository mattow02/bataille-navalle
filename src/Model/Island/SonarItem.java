package Model.Island;

import Model.HitOutcome;
import Model.Player.Player;
import Model.SpecialEffectContext;
import Model.SpecialEffectEntity;

/** Bonus d'île offrant un sonar. */
public class SonarItem implements IslandItem, SpecialEffectEntity {

    @Override
    public HitOutcome onAcquired(Player player) {
        player.addAmmo(Model.Weapons.WeaponType.SONAR);
        return HitOutcome.ACQUIRED_WEAPON;
    }

    @Override
    public String entityType() {
        return "ISLAND_ITEM_SONAR";
    }

    @Override
    public String getDisplayName() {
        return "Sonar (Bonus)";
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public void applySpecialEffect(SpecialEffectContext context, Model.Coordinates target, boolean isHumanAttacker) {
        context.notifyItemSonarFound();
    }
}
