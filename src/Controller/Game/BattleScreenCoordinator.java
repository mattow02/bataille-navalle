package Controller.Game;

import Controller.IGameController;
import View.BattleViewInterface;

/** Coordonne l'ouverture et la fermeture de la vue de bataille. */
public class BattleScreenCoordinator {

    private final IGameController controller;
    private final BattleFlowController battleFlow;
    private final WeaponCommandHandler weaponHandler;

    /** Prépare le coordinateur de l'écran de bataille. */
    public BattleScreenCoordinator(IGameController controller,
                                   BattleFlowController battleFlow,
                                   WeaponCommandHandler weaponHandler) {
        this.controller = controller;
        this.battleFlow = battleFlow;
        this.weaponHandler = weaponHandler;
    }

    /** Affiche la vue de bataille. */
    public void showBattle() {
        battleFlow.showBattleView(controller, weaponHandler);
    }

    /** Ferme la vue de bataille si elle est ouverte. */
    public void closeBattleView() {
        var view = battleFlow.getBattleView();
        if (view != null) {
            view.close();
        }
    }

    /** Expose la vue de bataille courante. */
    public BattleViewInterface getBattleView() {
        return battleFlow.getBattleView();
    }
}
