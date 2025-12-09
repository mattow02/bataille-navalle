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

    // Marque une zone 4x4 comme île (pour le mode île)
    public void markIslandZone(int startRow, int startCol) {
        for (int r = startRow; r < startRow + 4; r++) {
            for (int c = startCol; c < startCol + 4; c++) {
                Coordinates coord = new Coordinates(r, c);
                if (coord.isValid(size)) {
                    cells[r][c].setIsIslandCell(true);
                }
            }
        }
    }

    // Place un bateau sur la grille avec vérifications (hors île, cases libres)
    public boolean placeBoat(Boat boat, Coordinates startCoord, Orientation orientation) {
        List<Coordinates> positions = calculateBoatPositions(boat, startCoord, orientation);

        if (!isValidPlacement(positions)) {
            return false;
        }

        for (Coordinates coord : positions) {
            if (cells[coord.getRow()][coord.getColumn()].isIslandCell()) {
                return false;
            }
        }

        for (int i = 0; i < positions.size(); i++) {
            Coordinates coord = positions.get(i);
            GridCell cell = cells[coord.getRow()][coord.getColumn()];

            cell.setEntity(boat);
            cell.setBoatSegmentIndex(i);
        }

        return true;
    }

    // Vérifie que toutes les positions sont valides et libres
    private boolean isValidPlacement(List<Coordinates> positions) {
        for (Coordinates coord : positions) {
            if (!coord.isValid(size)) {
                return false;
            }
            if (cells[coord.getRow()][coord.getColumn()].isOccupied()) {
                return false;
            }
        }
        return true;
    }

    // Calcule toutes les positions occupées par le bateau selon son orientation
    private List<Coordinates> calculateBoatPositions(Boat boat, Coordinates start, Orientation orientation) {
        List<Coordinates> positions = new ArrayList<>();
        int boatSize = boat.size();

        for (int i = 0; i < boatSize; i++) {
            int row = start.getRow();
            int col = start.getColumn();

            if (orientation == Orientation.HORIZONTAL) {
                col += i;
            } else {
                row += i;
            }

            positions.add(new Coordinates(row, col));
        }

        return positions;
    }



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