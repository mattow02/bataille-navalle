package Model.Boat;

/** Représente un sous-marin. */
public class Submarine extends AbstractBoat {

    /** Crée un sous-marin. */
    public Submarine() {
        super(BoatType.SUBMARINE);
    }

    @Override
    public boolean isSubmarine() {
        return true;
    }
}
