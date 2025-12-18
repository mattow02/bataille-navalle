package Model.Trap;

import Model.HitOutcome;
import Model.Player.Player;
import Model.SpecialEffectContext;
import Model.SpecialEffectEntity;

/** Piège de type trou noir. */
public class BlackHole implements SpecialEffectEntity {
    private final int size = 1;
    private boolean isTriggered = false;

    @Override
    public HitOutcome handleImpact(Player attacker, int segmentIndex) {
        if (!isTriggered) {
            isTriggered = true;
            return HitOutcome.TRAP_TRIGGERED;
        }
        return HitOutcome.MISS;
    }

    @Override
    public String entityType() {
        return "TRAP_BLACK_HOLE";
    }

    @Override
    public boolean isDetectableBySonar() {
        return true;
    }

    @Override
    public String getDisplayName() {
        return "Trou Noir (Piège)";
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void applySpecialEffect(SpecialEffectContext context, Model.Coordinates target, boolean isHumanAttacker) {
        context.notifyBlackHoleTrigger();

        var victimGrid = isHumanAttacker ? context.playerGrid() : context.enemyGrid();
        var victim = isHumanAttacker ? context.humanPlayer() : context.computerPlayer();

        if (target != null && target.isValid(victimGrid.getSize())) {
            var backfire = victimGrid.strikeCell(target, victim);
            if (backfire == HitOutcome.HIT || backfire == HitOutcome.SUNK) {
                context.notifyBlackHoleBackfire(target);
            } else {
                context.notifyBlackHoleAbsorbed(target);
            }
        }
    }
}
