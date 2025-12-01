package Model.Weapons;

import Model.Coordinates;
import Model.Map.Grid;
import Model.Map.GridCell;

public class Sonar {
    // Scanne une zone 3x3 et compte les bateaux et trous noirs détectés
    public int scan(Grid targetGrid, Coordinates target) {
        int occupiedCount = 0;
        int gridSize = targetGrid.getSize();

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {

                int r = target.getRow() + rowOffset;
                int c = target.getColumn() + colOffset;
                Coordinates scanCoord = new Coordinates(r, c);

                if (scanCoord.isValid(gridSize)) {
                    GridCell cell = targetGrid.getCell(scanCoord);

                    // Le sonar ne peut pas scanner l'île
                    if (!cell.isIslandCell()) {
                        // Détecte les bateaux et les trous noirs
                        if (cell.isOccupied() && cell.getEntity() instanceof Model.Boat.Boat) {
                            occupiedCount++;
                        } else if (cell.isOccupied() && cell.getEntity() instanceof Model.Trap.BlackHole) {
                            occupiedCount++;
                        }
                    }
                }
            }
        }
        return occupiedCount;
    }
}