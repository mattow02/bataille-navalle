package Model.Island;

import Model.HitOutcome;
import Model.Player.Player;
import Model.Coordinates;
import Model.GridEntity;

public class BombItem implements IslandItem {

    @Override
    public HitOutcome onAcquired(Player player) {
        if (player instanceof Model.Player.HumanPlayer) {
            ((Model.Player.HumanPlayer) player).addBomb();
        }

        return HitOutcome.ACQUIRED_WEAPON;
    }


    public HitOutcome handleImpact(Player attacker, Coordinates coordinates) {
        return onAcquired(attacker);
    }

    @Override
    public String entityType() {
        return "ISLAND_ITEM_BOMB";
    }

    @Override
    public int size() {
        return 1;
    }
}