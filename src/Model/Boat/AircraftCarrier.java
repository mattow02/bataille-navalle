package Model.Boat;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Player.Player;

public class AircraftCarrier implements Boat {
    private int size;
    private boolean[] hits;
    private boolean sunk;

    public AircraftCarrier() {
        this.size = 5;
        this.hits = new boolean[size];
        this.sunk = false;
    }
    public HitOutcome handleImpact(Player attacker, Coordinates coordinates) {

        for (int i = 0; i < size; i++) {
            if (!hits[i]) {
                System.out.println(" Première case non touchée trouvée: " + i);
                return receiveHit(i);
            }
        }

        // Si toutes les cases sont touchées, on touche la première
        System.out.println(" Toutes les cases déjà touchées, on touche la première");
        return receiveHit(0);
    }

    private int calculateHitIndex(Coordinates coordinates) {
        if (coordinates == null) return 0;


        return coordinates.getColumn() - 2; // 2 = colonne de départ
    }
    public boolean isSunk() {
        return sunk;
    }

    public HitOutcome receiveHit(int index) {

        if (index < 0 || index >= size) {
            return HitOutcome.INVALID;
        }

        hits[index] = true;

        sunk = true;
        for (boolean hit : hits) {
            if (!hit) {
                sunk = false;
                break;
            }
        }

        if (sunk) {

            return HitOutcome.SUNK;
        } else {
            return HitOutcome.HIT;
        }
    }
    public String entityType() {
        return "BOAT";
    }

    public int size() {
        return size;
    }

    public String name() {
        return "Porte-avions";
    }

    public BoatType getType() {
        return BoatType.AIRCRAFT_CARRIER;
    }
}