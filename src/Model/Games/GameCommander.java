package Model.Games;

import Model.Coordinates;
import Model.Weapons.Weapon;

/** Pilote l'exécution des tours de jeu. */
public interface GameCommander {

    /** Indique si c'est au tour du joueur. */
    boolean isPlayerTurn();

    /** Traite une attaque avec l'arme donnée. */
    void handleWeaponAttack(Weapon weapon, Coordinates target);

    /** Résout le tour de l'ordinateur. */
    void resolveComputerTurn();
}
