package Model.Player;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Map.Grid;
import Model.Weapons.WeaponType;

import java.util.EnumMap;
import java.util.Map;

/** Joueur humain avec gestion des munitions. */
public class HumanPlayer extends Player {
    private final String name;
    private final Map<WeaponType, Integer> ammoByType = new EnumMap<>(WeaponType.class);

    public HumanPlayer(String name, Grid ownGrid, Grid targetGrid) {
        super(ownGrid, targetGrid);
        this.name = name;
    }

    public void addBomb() {
        addAmmo(WeaponType.BOMB);
    }

    public int getBombCount() {
        return ammoByType.getOrDefault(WeaponType.BOMB, 0);
    }

    public void addSonar() {
        addAmmo(WeaponType.SONAR);
    }

    public int getSonarCount() {
        return ammoByType.getOrDefault(WeaponType.SONAR, 0);
    }

    @Override
    public boolean hasAmmo(WeaponType type) {
        if (type == WeaponType.MISSILE) {
            return true;
        }
        return ammoByType.getOrDefault(type, 0) > 0;
    }

    @Override
    public void consumeAmmo(WeaponType type) {
        if (type == WeaponType.MISSILE) {
            return;
        }
        ammoByType.computeIfPresent(type, (t, count) -> Math.max(0, count - 1));
    }

    @Override
    public void addAmmo(WeaponType type) {
        if (type == WeaponType.MISSILE) {
            return;
        }
        ammoByType.merge(type, 1, Integer::sum);
    }

    @Override
    public HitOutcome fire(Coordinates coordinates) {
        return strikeTarget(coordinates);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isDefeated() {
        return areAllBoatsSunk();
    }
}
