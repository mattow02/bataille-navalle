package Model.Map;

import Model.Boat.Boat;
import Model.Coordinates;
import Model.Orientation;
import java.util.ArrayList;
import java.util.List;

public class Grid {
    private final int size;
    private final GridCell[][] cells;

    public Grid(int size) {
        this.size = size;
        this.cells = new GridCell[size][size];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                cells[row][col] = new GridCell();
            }
        }
    }

    // NOUVELLE MÉTHODE : Placer un bateau
    public boolean placeBoat(Boat boat, Coordinates startCoord, Orientation orientation) {
        List<Coordinates> positions = calculateBoatPositions(boat, startCoord, orientation);

        if (!isValidPlacement(positions)) {
            return false;
        }

        //  TOUTES LES CELLULES DOIVENT AVOIR LA MÊME INSTANCE DE BATEAU
        for (int i = 0; i < positions.size(); i++) {
            Coordinates coord = positions.get(i);
            cells[coord.getRow()][coord.getColumn()].setEntity(boat); // Même instance !
        }

        return true;
    }

    //  Calculer les positions occupées par le bateau
    private List<Coordinates> calculateBoatPositions(Boat boat, Coordinates start, Orientation orientation) {
        List<Coordinates> positions = new ArrayList<>();
        int boatSize = boat.size();

        for (int i = 0; i < boatSize; i++) {
            int row = start.getRow();
            int col = start.getColumn();

            if (orientation == Orientation.HORIZONTAL) {
                col += i;
            } else { // VERTICAL
                row += i;
            }

            positions.add(new Coordinates(row, col));
        }

        return positions;
    }

    //  Vérifier si le placement est valide
    private boolean isValidPlacement(List<Coordinates> positions) {
        for (Coordinates coord : positions) {
            // Vérifier si dans la grille
            if (!coord.isValid(size)) {
                return false;
            }
            // Vérifier si la case est déjà occupée
            if (cells[coord.getRow()][coord.getColumn()].isOccupied()) {
                return false;
            }
        }
        return true;
    }

    // Méthodes existantes...
    public GridCell getCell(Coordinates coord) {
        if (coord.isValid(size)) {
            return cells[coord.getRow()][coord.getColumn()];
        }
        return null;
    }

    public int getSize() {
        return size;
    }

    public GridCell[][] getCells() {
        return cells;
    }
}