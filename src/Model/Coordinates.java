package Model;

public class Coordinates {
    private final int row;
    private final int column;

    public Coordinates(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() { return row; }
    public int getColumn() { return column; }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }

    public boolean isValid(int gridSize) {
        return row >= 0 && row < gridSize && column >= 0 && column < gridSize;
    }
}