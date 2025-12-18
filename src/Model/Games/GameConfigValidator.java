package Model.Games;

import Model.Boat.BoatType;

import java.util.Map;

/** Valide les paramètres de configuration de partie. */
public class GameConfigValidator {

    /** Porte le résultat de validation. */
    public record Result(boolean valid, String errorMessage, int totalCells) {

        /** Crée un résultat valide. */
        public static Result ok(int totalCells) {
            return new Result(true, null, totalCells);
        }

        /** Crée un résultat invalide avec message. */
        public static Result error(String message, int totalCells) {
            return new Result(false, message, totalCells);
        }
    }

    /** Vérifie la validité des nombres de bateaux. */
    public Result validate(Map<BoatType, Integer> boatCounts) {
        var totalCells = calculateTotalCells(boatCounts);
        if (totalCells > 35) {
            return Result.error("Trop de cases ! Maximum 35.", totalCells);
        }
        if (totalCells == 0) {
            return Result.error("Vous devez avoir au moins 1 bateau !", totalCells);
        }
        return Result.ok(totalCells);
    }

    /** Calcule le nombre total de cases occupées. */
    public int calculateTotalCells(Map<BoatType, Integer> boatCounts) {
        var total = 0;
        if (boatCounts == null) {
            return 0;
        }
        for (var entry : boatCounts.entrySet()) {
            var boatType = entry.getKey();
            var count = entry.getValue();
            if (boatType != null && count != null) {
                total += boatType.getSize() * count;
            }
        }
        return total;
    }
}
