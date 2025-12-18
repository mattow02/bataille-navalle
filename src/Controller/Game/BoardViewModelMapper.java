package Controller.Game;

import Model.Map.CellStateInfo;
import View.Model.BattleInfo;
import View.Model.CellState;

/** Convertit les informations du modèle en états pour la vue. */
public class BoardViewModelMapper {

    /** Traduit un état de cellule interne vers l'état visible. */
    public CellState toCellState(CellStateInfo info, boolean isPlayerView) {
        if (info == null) return CellState.WATER;
        if (info.isIslandCell()) {
            if (!info.isHit()) return CellState.ISLAND;
            return info.isOccupied() ? CellState.ITEM_FOUND : CellState.EXPLORED;
        }
        if (!isPlayerView && !info.isHit()) return CellState.WATER;
        if (!info.isHit()) {
            if (info.isOccupied()) {
                if (info.isBoat()) return CellState.BOAT;
                return CellState.TRAP;
            }
            return CellState.WATER;
        }
        if (!info.isOccupied()) return CellState.MISS;
        if (info.isBoat() && info.isBoatSunk()) return CellState.SUNK;
        return CellState.HIT;
    }

    /** Crée le view model d'informations de bataille. */
    public BattleInfo toBattleInfo(int turnCount,
                                   int playerAliveBoats,
                                   int enemyAliveBoats,
                                   int bombCount,
                                   int sonarCount,
                                   boolean hasBomb,
                                   boolean hasSonar) {
        return new BattleInfo(
                turnCount,
                playerAliveBoats,
                enemyAliveBoats,
                bombCount,
                sonarCount,
                hasBomb,
                hasSonar
        );
    }
}
