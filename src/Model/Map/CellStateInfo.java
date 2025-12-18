package Model.Map;

/** Snapshot d'état d'une cellule pour la vue. */
public record CellStateInfo(
        boolean isIslandCell,
        boolean isHit,
        boolean isOccupied,
        boolean isBoat,
        boolean isBoatSunk
) {

    /** Construit l'état depuis une cellule de grille. */
    public static CellStateInfo fromGridCell(GridCell cell) {
        if (cell == null) {
            return new CellStateInfo(false, false, false, false, false);
        }
        var isBoat = cell.isBoat();
        var isBoatSunk = cell.isBoatSunk();
        return new CellStateInfo(
                cell.isIslandCell(),
                cell.isHit(),
                cell.isOccupied(),
                isBoat,
                isBoatSunk
        );
    }
}
