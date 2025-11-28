package Model.Boat;

import Model.Coordinates;
import Model.HitOutcome;
import Model.Player.Player;

public class Cruiser implements Boat {
    private int size;
    private boolean[] hits;
    private boolean sunk;

    public Cruiser() {
        this.size = 4;
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

    public boolean isSunk() {
        return sunk;
    }

    public HitOutcome receiveHit(int index) {
        if (index < 0 || index >= size) {
            return HitOutcome.INVALID;
        }

        //  MARQUER COMME TOUCHÉ
        hits[index] = true;

        //  VÉRIFIER SI LE BATEAU EST COULÉ
        sunk = true;
        for (boolean hit : hits) {
            if (!hit) {
                sunk = false;
                break;
            }
        }

        //  RETOURNER LE BON RÉSULTAT
        if (sunk) {
            System.out.println( name() + " COULÉ !");
            return HitOutcome.SUNK;
        } else {
            System.out.println(name() + " touché à la position " + index);
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
        return "Croiseur";
    }

    public BoatType getType() {
        return BoatType.CRUISER;
    }
}