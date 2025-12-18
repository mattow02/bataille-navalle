package Model;

/** Représente une coordonnée de grille. */
public record Coordinates(int row, int column) {

    /** Formate la coordonnée pour affichage (ex: A1). */
    public String toFormattedString() {
        return (char) ('A' + row) + "" + (column + 1);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }

    /** Vérifie si la coordonnée est dans la grille. */
    public boolean isValid(int gridSize) {
        return row >= 0 && row < gridSize && column >= 0 && column < gridSize;
    }
}
