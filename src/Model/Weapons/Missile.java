package Model.Weapons;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Map.Grid;
import Model.Player.Player;

/** Arme missile à tir unique. */
public class Missile implements Weapon {

    @Override
    public WeaponResult use(Player attacker, Grid targetGrid, Coordinates target) {
        var outcome = targetGrid.strikeCell(target, attacker);
        var valid = outcome != HitOutcome.INVALID;

        return new WeaponResult(
                WeaponType.MISSILE,
                valid,
                true,
                outcome,
                0,
                valid
        );
    }
}
