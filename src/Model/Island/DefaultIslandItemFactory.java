package Model.Island;

import Model.GridEntity;
import Model.Trap.TrapFactory;

import java.util.ArrayList;
import java.util.List;

/** Fabrique standard des items d'île. */
public class DefaultIslandItemFactory implements IslandItemFactory {

    private final TrapFactory trapFactory;

    /** Initialise la fabrique d'items d'île. */
    public DefaultIslandItemFactory(TrapFactory trapFactory) {
        this.trapFactory = trapFactory;
    }

    @Override
    public List<GridEntity> createIslandItems() {
        var items = new ArrayList<GridEntity>();
        items.add(new BombItem());
        items.add(new SonarItem());
        if (trapFactory != null) {
            items.add(trapFactory.createBlackHole());
            items.add(trapFactory.createTornado());
        }
        return items;
    }
}
