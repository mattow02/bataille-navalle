package Model.Boat;

public interface BoatFactory {
    Boat create(BoatType type);
}