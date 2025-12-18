package Model.Boat;

/** Référence les types de bateaux disponibles. */
public enum BoatType {
    AIRCRAFT_CARRIER(5, "Porte-avions"),
    CRUISER(4, "Croiseur"),
    DESTROYER(3, "Contre-torpilleur"),
    SUBMARINE(3, "Sous-marin"),
    TORPEDO(2, "Torpilleur");

    private final int size;
    private final String name;

    BoatType(int size, String name) {
        this.size = size;
        this.name = name;
    }

    /** Retourne la taille en cases. */
    public int getSize() {
        return size;
    }

    /** Retourne le nom affiché. */
    public String getName() {
        return name;
    }
}
