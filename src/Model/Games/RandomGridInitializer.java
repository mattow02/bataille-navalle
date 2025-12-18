package Model.Games;

import Model.Boat.Boat;
import Model.Boat.BoatType;
import Model.Coordinates;
import Model.GridEntity;
import Model.Map.Grid;
import Model.Orientation;
import Model.Trap.TrapFactory;
import Model.Island.IslandItemFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

/** Initialise aléatoirement la grille ennemie et les items d'île. */
public class RandomGridInitializer implements GridInitializer {
    private final Model.Boat.BoatFactory boatFactory;
    private final TrapFactory trapFactory;
    private final IslandItemFactory islandItemFactory;
    private final Supplier<Double> randomSupplier;
    private final Random random;

    public RandomGridInitializer(Model.Boat.BoatFactory boatFactory,
                                 TrapFactory trapFactory,
                                 IslandItemFactory islandItemFactory,
                                 Supplier<Double> randomSupplier,
                                 Random random) {
        this.boatFactory = boatFactory;
        this.trapFactory = trapFactory;
        this.islandItemFactory = islandItemFactory;
        this.random = random != null ? random : new Random();
        this.randomSupplier = randomSupplier != null ? randomSupplier : this.random::nextDouble;
    }

    public static RandomGridInitializer withDefaults() {
        var trapFactory = new Model.Trap.DefaultTrapFactory();
        return new RandomGridInitializer(
                new Model.Boat.SimpleBoatFactory(),
                trapFactory,
                new Model.Island.DefaultIslandItemFactory(trapFactory),
                null,
                null
        );
    }

    @Override
    public void setupGrids(GameConfiguration config, Grid playerGrid, Grid enemyGrid) {
        if (config.islandMode()) {
            var islandStart = config.gridSize() / 2 - 2;
            playerGrid.markIslandZone(islandStart, islandStart);
            enemyGrid.markIslandZone(islandStart, islandStart);
            placeIslandItemsRandomly(enemyGrid);
        } else {
            placeTraps(enemyGrid);
        }
        placeEnemyBoats(config, enemyGrid);
    }

    private void placeEnemyBoats(GameConfiguration config, Grid grid) {
        for (var entry : config.boatCounts().entrySet()) {
            for (var i = 0; i < entry.getValue(); i++) {
                placeBoatRandomly(boatFactory.create(entry.getKey()), grid);
            }
        }
    }

    private void placeBoatRandomly(Boat boat, Grid grid) {
        var attempts = 0;
        while (attempts < 100) {
            var row = random.nextInt(grid.getSize());
            var col = random.nextInt(grid.getSize());
            var orientation = randomSupplier.get() > 0.5 ? Orientation.HORIZONTAL : Orientation.VERTICAL;
            if (grid.placeBoat(boat, new Coordinates(row, col), orientation)) {
                return;
            }
            attempts++;
        }
    }

    private void placeTraps(Grid grid) {
        placeEntityRandomly(trapFactory.createBlackHole(), grid);
        placeEntityRandomly(trapFactory.createTornado(), grid);
    }

    private void placeEntityRandomly(GridEntity entity, Grid grid) {
        var attempts = 0;
        while (attempts < 100) {
            var row = random.nextInt(grid.getSize());
            var col = random.nextInt(grid.getSize());
            var startCord = new Coordinates(row, col);
            if (grid.placeEntity(entity, startCord)) {
                return;
            }
            attempts++;
        }
    }

    private void placeIslandItemsRandomly(Grid grid) {
        var gridSize = grid.getSize();
        var islandStart = gridSize / 2 - 2;
        var islandCords = new ArrayList<Coordinates>();
        for (int r = islandStart; r < islandStart + 4; r++) {
            for (int c = islandStart; c < islandStart + 4; c++) {
                var cord = new Coordinates(r, c);
                if (grid.isIslandCellFree(cord)) {
                    islandCords.add(cord);
                }
            }
        }
        List<GridEntity> items = islandItemFactory != null ? islandItemFactory.createIslandItems() : List.of();
        Collections.shuffle(islandCords, random);
        for (GridEntity item : items) {
            if (!islandCords.isEmpty()) {
                var coord = islandCords.removeFirst();
                placeEntityOnIsland(grid, item, coord);
            }
        }
    }

    private void placeEntityOnIsland(Grid grid, GridEntity entity, Coordinates coord) {
        grid.placeIslandEntity(entity, coord);
    }
}

