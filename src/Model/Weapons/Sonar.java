package Model.Weapons;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Map.Grid;
import Model.Player.Player;

/** Arme sonar pour détecter les entités. */
public class Sonar implements Weapon {

    @Override
    public WeaponResult use(Player attacker, Grid targetGrid, Coordinates target) {
        if (!attacker.hasAmmo(WeaponType.SONAR)) {
            return new WeaponResult(WeaponType.SONAR, false, false, HitOutcome.INVALID, 0, false);
        }
        if (!attacker.isSubmarineAlive()) {
            return new WeaponResult(WeaponType.SONAR, false, false, HitOutcome.INVALID, -1, false);
        }
        attacker.consumeAmmo(WeaponType.SONAR);

        var detected = 0;
        var gridSize = targetGrid.getSize();

        for (int r = target.row() - 1; r <= target.row() + 1; r++) {
            for (int c = target.column() - 1; c <= target.column() + 1; c++) {
                var scanCord = new Coordinates(r, c);
                if (scanCord.isValid(gridSize) && targetGrid.isDetectableBySonar(scanCord)) {
                    detected++;
                }
            }
        }

        return new WeaponResult(
                WeaponType.SONAR,
                false,
                false,
                HitOutcome.MISS,
                detected,
                true
        );
    }
}
