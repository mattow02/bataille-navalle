package Model.Player;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Map.Grid;

/** Stratégie de tir totalement aléatoire. */
public class RandomShotStrategy implements ShotStrategy {

    @Override
    public Coordinates getNextShot(Grid targetGrid, HitOutcome lastOutcome) {
        var gridSize = targetGrid.getSize();
        int row;
        int col;
        var attempts = 0;
        do {
            row = (int) (Math.random() * gridSize);
            col = (int) (Math.random() * gridSize);
            attempts++;
        } while (targetGrid.isCellHit(new Coordinates(row, col)) && attempts < 100);

        return new Coordinates(row, col);
    }
}
