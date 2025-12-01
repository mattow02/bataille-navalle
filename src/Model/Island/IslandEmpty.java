package Model.Island;

import Model.HitOutcome;
import Model.Player.Player;
import Model.Coordinates;
import Model.GridEntity;

public class IslandEmpty implements IslandItem {

    @Override
    public HitOutcome onAcquired(Player player) {
        return HitOutcome.MISS;
    }


    public HitOutcome handleImpact(Player attacker, Coordinates coordinates) {
        return onAcquired(attacker);
    }

    @Override
    public String entityType() {
        return "ISLAND_EMPTY";
    }

    @Override
    public int size() {
        return 1;
    }
}