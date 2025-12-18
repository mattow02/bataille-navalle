package Model.Events;

/** Types d'événements diffusés pendant une partie. */
public enum GameEventType {
    INIT_GAME,
    REFRESH_ALL,
    GAME_OVER_WIN,
    GAME_OVER_LOSE,
    WEAPON_FIRED,
    TURN_ENDED_PLAYER,
    TORNADO_EFFECT,
    TORNADO_HIT,
    BLACK_HOLE_TRIGGER,
    BLACK_HOLE_BACKFIRE,
    BLACK_HOLE_ABSORBED,
    ITEM_BOMB_FOUND,
    ITEM_SONAR_FOUND
}
