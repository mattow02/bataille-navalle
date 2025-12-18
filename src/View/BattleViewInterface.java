package View;

import View.Model.BattleInfo;
import View.Model.CellState;
import View.Model.EndGameStats;
import View.Model.WeaponViewModel;

import java.util.List;

/** Vue de la bataille affichant grilles, armes et logs. */
public interface BattleViewInterface extends GameView {

    void updateGrids(CellState[][] playerStates, CellState[][] enemyStates);

    void updateInfo(BattleInfo info);

    void addLog(String message);

    void showEndGame(EndGameStats stats);

    void renderWeapons(List<WeaponViewModel> weapons);

    void setSelectedWeapon(String weaponId);
}
