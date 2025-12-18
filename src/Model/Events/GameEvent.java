package Model.Events;

import Model.Coordinates;
import Model.Weapons.WeaponResult;

/** Transporte un événement de jeu générique. */
public record GameEvent(GameEventType type, Coordinates coords, WeaponResult weaponResult) {

    /** Crée un événement simple sans données associées. */
    public GameEvent(GameEventType type) {
        this(type, null, null);
    }

    /** Crée un événement positionnel. */
    public GameEvent(GameEventType type, Coordinates coords) {
        this(type, coords, null);
    }

    /** Crée un événement lié à une arme. */
    public GameEvent(GameEventType type, WeaponResult result) {
        this(type, null, result);
    }
}
