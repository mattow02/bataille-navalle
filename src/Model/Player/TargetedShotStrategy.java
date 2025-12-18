package Model.Player;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Map.Grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Stratégie de tir ciblée après un coup au but. */
public class TargetedShotStrategy implements ShotStrategy {
    private final List<Coordinates> potentialTargets;
    private Coordinates lastHit;
    private boolean isHunting;

    public TargetedShotStrategy() {
        this.potentialTargets = new ArrayList<>();
        this.isHunting = false;
    }

    @Override
    public Coordinates getNextShot(Grid targetGrid, HitOutcome lastOutcome) {
        if (lastOutcome == HitOutcome.HIT) {
            isHunting = true;
            if (lastHit != null) {
                addAdjacentTargets(lastHit, targetGrid);
            }
        }

        if (lastOutcome == HitOutcome.SUNK) {
            potentialTargets.clear();
            lastHit = null;
            isHunting = false;
        }

        if (isHunting && !potentialTargets.isEmpty()) {
            Coordinates target = findNextValidTarget(targetGrid);
            if (target != null) {
                return target;
            } else {
                isHunting = false;
            }
        }

        return getRandomShot(targetGrid);
    }

    private Coordinates findNextValidTarget(Grid targetGrid) {
        Iterator<Coordinates> iterator = potentialTargets.iterator();
        while (iterator.hasNext()) {
            var target = iterator.next();
            if (isCellHit(targetGrid, target)) {
                iterator.remove();
                return target;
            } else {
                iterator.remove();
            }
        }
        return null;
    }

    private void addAdjacentTargets(Coordinates hit, Grid targetGrid) {
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

        for (int[] dir : directions) {
            var adjacent = new Coordinates(hit.row() + dir[0], hit.column() + dir[1]);
            if (isValidTarget(adjacent, targetGrid) &&
                    isCellHit(targetGrid, adjacent) &&
                    !potentialTargets.contains(adjacent)) {
                potentialTargets.add(adjacent);
            }
        }
    }

    public void setLastHit(Coordinates hit) {
        this.lastHit = hit;
        this.isHunting = true;
    }

    @Override
    public void notifyShotResult(Coordinates target, HitOutcome outcome) {
        if (outcome == HitOutcome.HIT || outcome == HitOutcome.SUNK) {
            setLastHit(target);
        }
    }

    private Coordinates getRandomShot(Grid targetGrid) {
        var gridSize = targetGrid.getSize();
        var attempts = 0;

        while (attempts < 100) {
            var row = (int) (Math.random() * gridSize);
            var col = (int) (Math.random() * gridSize);
            var target = new Coordinates(row, col);

            if (isCellHit(targetGrid, target)) {
                return target;
            }
            attempts++;
        }

        for (var row = 0; row < gridSize; row++) {
            for (var col = 0; col < gridSize; col++) {
                var target = new Coordinates(row, col);
                if (isCellHit(targetGrid, target)) {
                    return target;
                }
            }
        }

        return new Coordinates(0, 0);
    }

    private boolean isValidTarget(Coordinates cord, Grid targetGrid) {
        return cord.isValid(targetGrid.getSize());
    }

    private boolean isCellHit(Grid targetGrid, Coordinates cord) {
        if (!isValidTarget(cord, targetGrid)) return false;
        return !targetGrid.isCellHit(cord);
    }
}
