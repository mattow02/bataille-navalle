package Model.Map;

import Model.Boat.Boat;
import Model.Coordinates;
import Model.Orientation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Représente une grille de jeu et ses entités. */
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

    public void markIslandZone(int startRow, int startCol) {
        for (int r = startRow; r < startRow + 4; r++) {
            for (int c = startCol; c < startCol + 4; c++) {
                var cord = new Coordinates(r, c);
                if (cord.isValid(size)) {
                    cells[r][c].setIsIslandCell(true);
                }
            }
        }
    }

    public boolean placeBoat(Boat boat, Coordinates startCord, Orientation orientation) {
        var positions = calculateBoatPositions(boat, startCord, orientation);

        if (!isValidPlacement(positions)) {
            return false;
        }

        for (var cord : positions) {
            if (cells[cord.row()][cord.column()].isIslandCell()) {
                return false;
            }
        }

        for (int i = 0; i < positions.size(); i++) {
            var cord = positions.get(i);
            var cell = cells[cord.row()][cord.column()];
            cell.setEntity(boat);
            cell.setBoatSegmentIndex(i);
        }

        return true;
    }

    private boolean isValidPlacement(List<Coordinates> positions) {
        for (var cord : positions) {
            if (!cord.isValid(size)) {
                return false;
            }
            var cell = getCell(cord);
            if (cell != null && cell.isOccupied()) {
                return false;
            }
        }
        return true;
    }

    private List<Coordinates> calculateBoatPositions(Boat boat, Coordinates start, Orientation orientation) {
        var positions = new ArrayList<Coordinates>();
        var boatSize = boat.size();

        for (int i = 0; i < boatSize; i++) {
            var row = start.row();
            var col = start.column();

            if (orientation == Orientation.HORIZONTAL) {
                col += i;
            } else {
                row += i;
            }

            positions.add(new Coordinates(row, col));
        }

        return positions;
    }

    private GridCell getCell(Coordinates cord) {
        if (cord.isValid(size)) {
            return cells[cord.row()][cord.column()];
        }
        return null;
    }

    public CellStateInfo getCellStateInfo(Coordinates coord) {
        return CellStateInfo.fromGridCell(getCell(coord));
    }

    public boolean isCellHit(Coordinates cord) {
        var cell = getCell(cord);
        return cell != null && cell.isHit();
    }

    public int getSize() {
        return size;
    }

    public boolean placeEntity(Model.GridEntity entity, Coordinates cord) {
        if (entity == null || cord == null) {
            return false;
        }

        if (!cord.isValid(size)) {
            return false;
        }

        var cell = getCell(cord);
        if (cell == null) {
            return false;
        }

        if (cell.isOccupied() || cell.isIslandCell()) {
            return false;
        }

        cell.setEntity(entity);
        return true;
    }

    public boolean placeIslandEntity(Model.GridEntity entity, Coordinates coord) {
        if (entity == null || coord == null || !coord.isValid(size)) {
            return false;
        }
        var cell = getCell(coord);
        if (cell == null || !cell.isIslandCell() || cell.isOccupied()) {
            return false;
        }
        cell.setEntity(entity);
        return true;
    }

    public boolean isIslandCellFree(Coordinates coord) {
        var cell = getCell(coord);
        return cell != null && cell.isIslandCell() && !cell.isOccupied();
    }

    public boolean isIslandCell(Coordinates coord) {
        var cell = getCell(coord);
        return cell != null && cell.isIslandCell();
    }

    public boolean isDetectableBySonar(Coordinates coord) {
        var cell = getCell(coord);
        return cell != null && !cell.isIslandCell() && cell.isDetectableBySonar();
    }

    public Model.GridEntity extractEntity(Coordinates coord) {
        var cell = getCell(coord);
        if (cell == null) return null;
        var entity = cell.getEntity();
        if (entity != null) {
            cell.setEntity(null);
        }
        return entity;
    }

    public Model.HitOutcome strikeCell(Coordinates cord, Model.Player.Player attacker) {
        var cell = getCell(cord);
        if (cell == null) {
            return Model.HitOutcome.INVALID;
        }
        return cell.strike(attacker);
    }

    public boolean areAllBoatsSunk() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                var cord = new Coordinates(row, col);
                var cell = getCell(cord);
                if (cell != null && cell.isOccupied()) {
                    var entity = cell.getEntity();
                    if (entity != null && entity.isBoat() && !entity.isSunk()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public int getAliveBoatsCount() {
        Set<Model.GridEntity> uniqueBoats = new HashSet<>();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                var cell = getCell(new Coordinates(r, c));
                if (cell != null && cell.isOccupied()) {
                    var entity = cell.getEntity();
                    if (entity != null && entity.isBoat()) {
                        uniqueBoats.add(entity);
                    }
                }
            }
        }
        var aliveCount = 0;
        for (var entity : uniqueBoats) {
            if (!entity.isSunk()) {
                aliveCount++;
            }
        }
        return aliveCount;
    }

    public boolean isSubmarineAlive() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                var cell = getCell(new Coordinates(r, c));
                if (cell != null && cell.isOccupied()) {
                    var entity = cell.getEntity();
                    if (entity != null && entity.isBoat() && entity.isSubmarine() && !entity.isSunk()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
