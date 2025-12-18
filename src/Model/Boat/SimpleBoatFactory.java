package Model.Boat;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/** Fabrique simple de bateaux via un registre. */
public class SimpleBoatFactory implements BoatFactory {

    private final Map<BoatType, Supplier<Boat>> boatRegistry;

    /** Initialise le registre des constructeurs de bateaux. */
    public SimpleBoatFactory() {
        this.boatRegistry = new EnumMap<>(BoatType.class);
        initRegistry();
    }

    private void initRegistry() {
        boatRegistry.put(BoatType.AIRCRAFT_CARRIER, AircraftCarrier::new);
        boatRegistry.put(BoatType.CRUISER, Cruiser::new);
        boatRegistry.put(BoatType.DESTROYER, Destroyer::new);
        boatRegistry.put(BoatType.SUBMARINE, Submarine::new);
        boatRegistry.put(BoatType.TORPEDO, TorpedoBoat::new);
    }

    @Override
    public Boat create(BoatType type) {
        var supplier = boatRegistry.get(type);
        if (supplier == null) {
            throw new IllegalArgumentException("Type de bateau non supporté : " + type);
        }
        return supplier.get();
    }
}
