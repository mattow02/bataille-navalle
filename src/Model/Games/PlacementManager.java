package Model.Games;

import Model.Boat.Boat;
import Model.Boat.BoatFactory;
import Model.Boat.BoatType;
import Model.Coordinates;
import Model.GridEntity;
import Model.Map.Grid;
import Model.Orientation;
import Model.Trap.TrapFactory;

import java.util.ArrayList;
import java.util.List;

/** Gère la création et le placement des entités. */
public class PlacementManager {

    private final BoatFactory boatFactory;
    private final TrapFactory trapFactory;

    public PlacementManager(BoatFactory boatFactory, TrapFactory trapFactory) {
        this.boatFactory = boatFactory;
        this.trapFactory = trapFactory;
    }

    public List<Boat> createBoats(GameConfiguration config) {
        var boats = new ArrayList<Boat>();
        for (var entry : config.boatCounts().entrySet()) {
            for (var i = 0; i < entry.getValue(); i++) {
                boats.add(boatFactory.create(entry.getKey()));
            }
        }
        return boats;
    }

    public List<GridEntity> createTraps(boolean islandMode) {
        return trapFactory.createTraps(islandMode);
    }

    public boolean placeEntityOnGridByType(Grid grid, GridEntity entity, Coordinates coord, Orientation orientation) {
        if (entity == null || coord == null || grid == null) return false;
        if (entity.placeOnGridWithOrientation(grid, coord, orientation)) {
            return true;
        }
        return grid.placeEntity(entity, coord);
    }
}

