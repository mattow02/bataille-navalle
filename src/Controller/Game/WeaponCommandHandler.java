package Controller.Game;

import Model.Games.GameReader;
import Model.Weapons.WeaponSelectionService;
import Model.Weapons.WeaponType;

/** Gère la sélection courante de l'arme du joueur. */
public class WeaponCommandHandler {
    private final GameReader game;
    private final WeaponSelectionService selectionService;
    private WeaponType currentWeaponType = WeaponType.MISSILE;

    /** Initialise le gestionnaire d'arme. */
    public WeaponCommandHandler(GameReader game, WeaponSelectionService selectionService) {
        this.game = game;
        this.selectionService = selectionService;
    }

    /** Sélectionne une arme selon les règles disponibles. */
    public WeaponSelectionService.WeaponSelectionResult select(WeaponType type) {
        var result = selectionService.evaluate(type, game);
        currentWeaponType = result.selectedType();
        return result;
    }

    /** Retourne l'arme actuellement sélectionnée. */
    public WeaponType getCurrentWeaponType() {
        return currentWeaponType;
    }

    /** Réinitialise l'arme sur le missile par défaut. */
    public void resetToDefault() {
        currentWeaponType = WeaponType.MISSILE;
    }
}
