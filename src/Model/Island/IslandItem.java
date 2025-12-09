package Model.Island;

import Model.GridEntity;
import Model.HitOutcome;
import Model.Player.Player;

public interface IslandItem extends GridEntity {
    HitOutcome onAcquired(Player player);
}