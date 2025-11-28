package Model.Game;

import Model.Boat.BoatType;
import java.util.Map;

public class GameConfiguration {
    private int gridSize;
    private Map<BoatType, Integer> boatCounts;
    private boolean islandMode;
    private int numSpecialItems;
    private int computerShotLevel;
    private int placementLevel;


    public GameConfiguration(int gridSize, Map<BoatType, Integer> boatCounts,
                             boolean islandMode, int numSpecialItems,
                             int computerShotLevel, int placementLevel) {
        this.gridSize = gridSize;
        setBoatCounts(boatCounts); // ✅ VALIDATION
        this.islandMode = islandMode;
        this.numSpecialItems = numSpecialItems;
        this.computerShotLevel = computerShotLevel;
        this.placementLevel = placementLevel;

    }

    //  VALIDATION : Maximum 35 cases et 3 bateaux par type
    public void setBoatCounts(Map<BoatType, Integer> boatCounts) {
        int totalCells = calculateTotalCells(boatCounts);
        if (totalCells > 35) {
            throw new IllegalArgumentException(" Maximum 35 cases de bateaux ! Actuel: " + totalCells);
        }

        for (Map.Entry<BoatType, Integer> entry : boatCounts.entrySet()) {
            if (entry.getValue() < 0 || entry.getValue() > 3) {
                throw new IllegalArgumentException(" 1 à 3 bateaux maximum par type !");
            }
        }

        this.boatCounts = boatCounts;
    }

    //  CALCUL du total de cases
    private int calculateTotalCells(Map<BoatType, Integer> boatCounts) {
        int total = 0;
        for (Map.Entry<BoatType, Integer> entry : boatCounts.entrySet()) {
            total += entry.getKey().getSize() * entry.getValue();
        }
        return total;
    }

    // Getters
    public int getGridSize() { return gridSize; }
    public Map<BoatType, Integer> getBoatCounts() { return boatCounts; }
    public boolean isIslandMode() { return islandMode; }
    public int getNumSpecialItems() { return numSpecialItems; }
    public int getComputerShotLevel() { return computerShotLevel; }
    public int getPlacementLevel() { return placementLevel; }

    //  GETTER pour le total de cases
    public int getTotalBoatCells() {
        return calculateTotalCells(boatCounts);
    }
}