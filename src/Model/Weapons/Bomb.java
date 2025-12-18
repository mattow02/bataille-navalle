package Model.Weapons;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Map.Grid;
import Model.Player.Player;

/** Arme de type bombe avec dégâts de zone. */
public class Bomb implements Weapon {

    @Override
    public WeaponResult use(Player attacker, Grid targetGrid, Coordinates target) {
        if (!attacker.hasAmmo(WeaponType.BOMB)) {
            return new WeaponResult(WeaponType.BOMB, false, false, HitOutcome.INVALID, 0, false);
        }
        attacker.consumeAmmo(WeaponType.BOMB);

        int[][] offsets = {{0, 0}, {0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        var hitSomething = false;

        for (var offset : offsets) {
            var impact = new Coordinates(target.row() + offset[0], target.column() + offset[1]);
            if (impact.isValid(targetGrid.getSize()) && !targetGrid.isIslandCell(impact)) {
                var outcome = targetGrid.strikeCell(impact, attacker);
                if (outcome == HitOutcome.HIT || outcome == HitOutcome.SUNK) {
                    hitSomething = true;
                }
            }
        }

        return new WeaponResult(
                WeaponType.BOMB,
                true,
                true,
                HitOutcome.HIT,
                0,
                hitSomething
        );
    }
}
