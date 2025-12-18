package Model;

import Model.Map.Grid;
import Model.Player.Player;

/** Représente une entité plaçable sur une grille. */
public interface GridEntity {

    /** Traite un impact sur l'entité. */
    HitOutcome handleImpact(Player attacker, int segmentIndex);

    /** Identifie le type d'entité. */
    String entityType();

    /** Taille de l'entité en cases. */
    int size();

    default void applySpecialEffectIfPresent(SpecialEffectContext context, Coordinates target, boolean isHumanAttacker) {
    }

    default boolean isBoat() {
        return false;
    }

    default boolean isDetectableBySonar() {
        return false;
    }

    default boolean isSunk() {
        return false;
    }

    default String getDisplayName() {
        return entityType();
    }

    default boolean placeOnGridWithOrientation(Grid grid, Coordinates startCoord, Orientation orientation) {
        return false;
    }

    default boolean isSubmarine() {
        return false;
    }
}
