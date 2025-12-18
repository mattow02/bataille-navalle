package Model;

import Model.Weapons.WeaponResult;

/** Observe les événements du déroulement de la partie. */
public interface GameObserver {

    /** Signale l'initialisation complète du jeu. */
    default void onGameInitialized() {}

    /** Demande un rafraîchissement de la vue. */
    default void onRefreshRequested() {}

    /** Indique la fin du tour joueur. */
    default void onPlayerTurnEnded() {}

    /** Informe de la fin de partie. */
    default void onGameOver(boolean playerWon, int turnCount, int playerAliveBoats, int computerAliveBoats) {}

    /** Informe d'un tir exécuté. */
    default void onWeaponFired(WeaponResult result) {}

    /** Informe de la résolution d'un tir. */
    default void onShotResolved(Coordinates target, HitOutcome outcome, boolean isHuman) {}

    /** Informe d'un effet de tornade. */
    default void onTornadoEffect(Coordinates coords) {}

    /** Informe d'un impact de tornade. */
    default void onTornadoHit() {}

    /** Informe d'un déclenchement de trou noir. */
    default void onBlackHoleTrigger() {}

    /** Informe d'un retour de trou noir. */
    default void onBlackHoleBackfire(Coordinates coords) {}

    /** Informe d'un trou noir absorbé. */
    default void onBlackHoleAbsorbed(Coordinates coords) {}

    /** Informe d'un bonus bombe obtenu. */
    default void onItemBombFound() {}

    /** Informe d'un bonus sonar obtenu. */
    default void onItemSonarFound() {}
}
