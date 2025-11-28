package Controller;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Map.Grid;
import Model.Player.Player;

public class BattleController implements Observer {
    private GameController mainController;
    private Grid targetGrid;

    public BattleController(GameController mainController) {
        this.mainController = mainController;
        this.targetGrid = mainController.getGrid();
    }

    @Override
    public void update(Object event) {
        // Observation des événements de jeu
    }

    public HitOutcome handlePlayerAttack(int row, int col) {
        if (!mainController.isPlayerTurn()) {
            return HitOutcome.INVALID;
        }

        if (targetGrid == null) return HitOutcome.INVALID;

        Coordinates target = new Coordinates(row, col);
        if (!target.isValid(targetGrid.getSize())) return HitOutcome.INVALID;

        HitOutcome outcome = targetGrid.getCell(target).strike(mainController.getHumanPlayer());
        mainController.playerAttacked(row, col, outcome);
        return outcome;
    }
}