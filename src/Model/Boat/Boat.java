package Model.Boat;

import Model.GridEntity;
import Model.HitOutcome;

public interface Boat extends GridEntity {
    boolean isSunk();
    HitOutcome receiveHit(int index);
    int size();
    String name();
    BoatType getType();  // ← AJOUTER CETTE MÉTHODE
}