package Model.Weapons;

import Model.Coordinates;
import Model.Map.Grid;
import Model.Player.Player;
import Model.HitOutcome;
import java.util.ArrayList;
import java.util.List;

public class Bomb {
    // Explosion en croix : frappe 5 cases (centre + 4 directions)
    public void use(Grid targetGrid, Coordinates target, Player attacker) {
        int[][] offsets = {
                {0, 0},  // Centre
                {0, 1},  // Droite
                {0, -1}, // Gauche
                {1, 0},  // Bas
                {-1, 0}  // Haut
        };

        for (int[] offset : offsets) {
            int r = target.getRow() + offset[0];
            int c = target.getColumn() + offset[1];
            Coordinates impactCoord = new Coordinates(r, c);

            if (impactCoord.isValid(targetGrid.getSize())) {
                Model.Map.GridCell cell = targetGrid.getCell(impactCoord);

                // La bombe ne peut pas frapper l'île
                if (!cell.isIslandCell()) {
                    cell.strike(attacker);
                }
            }
        }
    }
}