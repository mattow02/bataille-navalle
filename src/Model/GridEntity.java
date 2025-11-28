package Model;

import Model.Player.Player;

public interface GridEntity {
    HitOutcome handleImpact(Player attacker, Coordinates coordinates);
    String entityType();
    int size();
}