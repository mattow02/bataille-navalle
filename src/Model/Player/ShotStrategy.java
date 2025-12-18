package Model.Player;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Map.Grid;

/** Détermine la prochaine cible de tir IA. */
public interface ShotStrategy {

    Coordinates getNextShot(Grid targetGrid, HitOutcome lastOutcome);

    default void notifyShotResult(Coordinates target, HitOutcome outcome) {
    }
}
