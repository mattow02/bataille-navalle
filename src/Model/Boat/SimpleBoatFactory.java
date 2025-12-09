package Model.Boat;

public class SimpleBoatFactory implements BoatFactory {
    public Boat create(BoatType type) {
        switch(type) {
            case AIRCRAFT_CARRIER:
                return new AircraftCarrier();
            case CRUISER:
                return new Cruiser();
            case DESTROYER:
                return new Destroyer();
            case SUBMARINE:
                return new Submarine();
            case TORPEDO:
                return new TorpedoBoat();
            default:
                throw new IllegalArgumentException("Type de bateau inconnu: " + type);
        }
    }
}