package Model.Player;

import Model.Map.Grid;
import Model.Coordinates;
import Model.HitOutcome;

public interface ShotStrategy {
    Coordinates getNextShot(Grid targetGrid, HitOutcome lastOutcome);
}