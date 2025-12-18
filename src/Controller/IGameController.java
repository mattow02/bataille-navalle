package Controller;

import Model.Weapons.WeaponType;
import View.Model.EndGameStats;

/** Expose les commandes principales du contrôleur de jeu. */
public interface IGameController {

    /** Traite une attaque joueur sur la grille ennemie. */
    void handlePlayerAttack(int row, int col);

    /** Sélectionne une arme par identifiant. */
    void selectWeapon(String weaponId);

    /** Sélectionne une arme par type. */
    void selectWeapon(WeaponType type);

    /** Lance l'application et ses vues initiales. */
    void startApplication();

    /** Redémarre l'application avec un nouvel état. */
    void restartApplication();

    /** Ferme proprement l'application. */
    void quitApplication();

    /** Affiche l'écran de fin de partie. */
    void showEndGame(EndGameStats stats);
}
