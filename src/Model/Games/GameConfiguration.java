package Model.Games;

import Model.Boat.BoatType;

import java.util.Collections;
import java.util.Map;

/** Transporte la configuration initiale d'une partie. */
public record GameConfiguration(int gridSize, Map<BoatType, Integer> boatCounts, boolean islandMode,
                                int computerShotLevel) {

    /** Valide et fige la configuration fournie. */
    public GameConfiguration(int gridSize, Map<BoatType, Integer> boatCounts,
                             boolean islandMode, int computerShotLevel) {
        this.gridSize = gridSize;
        this.islandMode = islandMode;
        this.computerShotLevel = computerShotLevel;
        var totalCells = calculateTotalCells(boatCounts);
        if (totalCells > 35) {
            throw new IllegalArgumentException("Maximum 35 cases de bateaux ! Actuel: " + totalCells);
        }
        this.boatCounts = Collections.unmodifiableMap(boatCounts);
    }

    private int calculateTotalCells(Map<BoatType, Integer> boatCounts) {
        var total = 0;
        for (var entry : boatCounts.entrySet()) {
            var boatType = entry.getKey();
            var count = entry.getValue();
            total += boatType.getSize() * count;
        }
        return total;
    }
}
