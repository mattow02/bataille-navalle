package Model.Boat;

/** Fabrique des instances de bateaux selon leur type. */
public interface BoatFactory {

    /** Crée un bateau pour le type demandé. */
    Boat create(BoatType type);
}
