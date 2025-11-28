package Model.Player;

import Model.Map.Grid;
import Model.Coordinates;
import Model.HitOutcome;
import java.util.*;

public class TargetedShotStrategy implements ShotStrategy {
    private List<Coordinates> potentialTargets;
    private Coordinates lastHit;
    private boolean isHunting; // MODE CHASSE

    public TargetedShotStrategy() {
        this.potentialTargets = new ArrayList<>();
        this.isHunting = false;
    }

    @Override
    public Coordinates getNextShot(Grid targetGrid, HitOutcome lastOutcome) {
        System.out.println(" IA Strategy - lastOutcome: " + lastOutcome + ", lastHit: " + lastHit + ", hunting: " + isHunting);

        //  CORRECTION : GESTION AMÉLIORÉE DES RÉSULTATS
        if (lastOutcome == HitOutcome.HIT) {
            System.out.println(" IA: Touché ! Activation mode chasse");
            isHunting = true;
            if (lastHit != null) {
                System.out.println("IA: Ajout cases autour de " + lastHit);
                addAdjacentTargets(lastHit, targetGrid);
            }
        }

        if (lastOutcome == HitOutcome.SUNK) {
            System.out.println(" IA: Bateau coulé, fin mode chasse");
            potentialTargets.clear();
            lastHit = null;
            isHunting = false;
        }

        // EN MODE CHASSE : PRIORITÉ AUX CIBLES ADJACENTES
        if (isHunting && !potentialTargets.isEmpty()) {
            Coordinates target = findNextValidTarget(targetGrid);
            if (target != null) {
                return target;
            } else {
                isHunting = false;
            }
        }

        //  FALLBACK: TIR ALÉATOIRE
        System.out.println(" IA: Mode aléatoire");
        return getRandomShot(targetGrid);
    }

    //  NOUVELLE MÉTHODE : Trouver cible valide
    private Coordinates findNextValidTarget(Grid targetGrid) {
        Iterator<Coordinates> iterator = potentialTargets.iterator();
        while (iterator.hasNext()) {
            Coordinates target = iterator.next();
            if (!isCellHit(targetGrid, target)) {
                iterator.remove();
                return target;
            } else {
                iterator.remove(); // Enlever les cibles déjà touchées
            }
        }
        return null;
    }

    //  MODIFIÉ : Éviter les doublons
    private void addAdjacentTargets(Coordinates hit, Grid targetGrid) {
        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}};

        for (int[] dir : directions) {
            Coordinates adjacent = new Coordinates(hit.getRow() + dir[0], hit.getColumn() + dir[1]);
            if (isValidTarget(adjacent, targetGrid) &&
                    !isCellHit(targetGrid, adjacent) &&
                    !potentialTargets.contains(adjacent)) { // Éviter doublons
                potentialTargets.add(adjacent);
                System.out.println(" IA: Ajout cible " + adjacent);
            }
        }
    }

    //  MODIFIÉ : setLastHit doit aussi activer le mode chasse
    public void setLastHit(Coordinates hit) {
        this.lastHit = hit;
        this.isHunting = true; // ✅ ACTIVER LE MODE CHASSE
        System.out.println(" IA: Dernier touché en " + hit + ", mode chasse activé");
    }



    private Coordinates getRandomShot(Grid targetGrid) {
        int gridSize = targetGrid.getSize();
        int attempts = 0;

        while (attempts < 100) {
            int row = (int) (Math.random() * gridSize);
            int col = (int) (Math.random() * gridSize);
            Coordinates target = new Coordinates(row, col);

            if (!isCellHit(targetGrid, target)) {
                return target; //  PLUS DE LOG ICI
            }
            attempts++;
        }

        // Fallback silencieux
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Coordinates target = new Coordinates(row, col);
                if (!isCellHit(targetGrid, target)) {
                    return target;
                }
            }
        }

        return new Coordinates(0, 0);
    }

    private boolean isValidTarget(Coordinates coord, Grid targetGrid) {
        return coord.isValid(targetGrid.getSize());
    }

    private boolean isCellHit(Grid targetGrid, Coordinates coord) {
        System.out.println("🔍 IA vérifie case " + coord);

        if (!isValidTarget(coord, targetGrid)) {
            System.out.println(" Case invalide: " + coord);
            return true;
        }

        Model.Map.GridCell cell = targetGrid.getCell(coord);
        if (cell == null) {
            System.out.println(" Cellule null pour: " + coord);
            return true;
        }

        boolean isHit = cell.isHit();
        System.out.println(" Case " + coord + " - isHit: " + isHit + ", isOccupied: " + cell.isOccupied());

        return isHit;
    }


}