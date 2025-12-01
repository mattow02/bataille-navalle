package Model.Player;

import Model.Map.Grid;
import Model.Coordinates;
import Model.HitOutcome;

public class RandomShotStrategy implements ShotStrategy {
    @Override
    public Coordinates getNextShot(Grid targetGrid, HitOutcome lastOutcome) {
        int gridSize = targetGrid.getSize();
        int row, col;

        int attempts = 0;
        do {
            row = (int) (Math.random() * gridSize);
            col = (int) (Math.random() * gridSize);
            attempts++;
        } while (targetGrid.getCell(new Coordinates(row, col)).isHit() && attempts < 100);

        return new Coordinates(row, col);
    }
}