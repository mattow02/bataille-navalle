package Model;

import Model.Map.Grid;
import Model.Player.Player;

/** Fournit le contexte pour appliquer des effets spéciaux. */
public interface SpecialEffectContext {

    /** Grille du joueur humain. */
    Grid playerGrid();

    /** Grille de l'ennemi. */
    Grid enemyGrid();

    /** Joueur humain. */
    Player humanPlayer();

    /** Joueur ordinateur. */
    Player computerPlayer();

    /** Notifie un impact de tornade. */
    void notifyTornadoHit();

    /** Accorde des tours bonus liés à la tornade. */
    void grantTornadoTurnsToAttacker(boolean isHumanAttacker, int turns);

    /** Notifie le déclenchement d'un trou noir. */
    void notifyBlackHoleTrigger();

    /** Notifie un retour de trou noir. */
    void notifyBlackHoleBackfire(Coordinates coords);

    /** Notifie l'absorption d'un trou noir. */
    void notifyBlackHoleAbsorbed(Coordinates coords);

    /** Notifie la découverte d'une bombe. */
    void notifyItemBombFound();

    /** Notifie la découverte d'un sonar. */
    void notifyItemSonarFound();
}
