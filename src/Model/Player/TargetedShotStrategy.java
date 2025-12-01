package Model.Player;

import Model.Map.Grid;
import Model.Coordinates;
import Model.HitOutcome;
import java.util.*;

public class TargetedShotStrategy implements ShotStrategy {
    private List<Coordinates> potentialTargets;
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
            Coordinates target = iterator.next();
            if (!isCellHit(targetGrid, target)) {
                iterator.remove();
                return target;
            } else {
                iterator.remove();
            }
        }
        return null;
    }

    private void addAdjacentTargets(Coordinates hit, Grid targetGrid) {
        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}};

        for (int[] dir : directions) {
            Coordinates adjacent = new Coordinates(hit.getRow() + dir[0], hit.getColumn() + dir[1]);
            if (isValidTarget(adjacent, targetGrid) &&
                    !isCellHit(targetGrid, adjacent) &&
                    !potentialTargets.contains(adjacent)) {
                potentialTargets.add(adjacent);
            }
        }
    }

    public void setLastHit(Coordinates hit) {
        this.lastHit = hit;
        this.isHunting = true;
    }

    private Coordinates getRandomShot(Grid targetGrid) {
        int gridSize = targetGrid.getSize();
        int attempts = 0;

        while (attempts < 100) {
            int row = (int) (Math.random() * gridSize);
            int col = (int) (Math.random() * gridSize);
            Coordinates target = new Coordinates(row, col);

            if (!isCellHit(targetGrid, target)) {
                return target;
            }
            attempts++;
        }

        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Coordinates target = new Coordinates(row, col);
                if (!isCellHit(targetGrid, target)) {
                    return target;
                }
            }
        }

        return new Coordinates(0, 0);
    }

    private boolean isValidTarget(Coordinates coord, Grid targetGrid) {
        return coord.isValid(targetGrid.getSize());
    }

    private boolean isCellHit(Grid targetGrid, Coordinates coord) {
        if (!isValidTarget(coord, targetGrid)) {
            return true;
        }

        Model.Map.GridCell cell = targetGrid.getCell(coord);
        if (cell == null) {
            return true;
        }

        return cell.isHit();
    }
}