package Model.Trap;

import Model.GridEntity;

import java.util.ArrayList;
import java.util.List;

/** Fabrique standard des pièges. */
public class DefaultTrapFactory implements TrapFactory {

    @Override
    public BlackHole createBlackHole() {
        return new BlackHole();
    }

    @Override
    public Tornado createTornado() {
        return new Tornado();
    }

    @Override
    public List<GridEntity> createTraps(boolean islandMode) {
        var traps = new ArrayList<GridEntity>();
        if (!islandMode) {
            traps.add(createBlackHole());
            traps.add(createTornado());
        }
        return traps;
    }
}
