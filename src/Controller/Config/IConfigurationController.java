package Controller.Config;

import View.Model.BoatTypeViewModel;

import java.util.List;
import java.util.Map;

/** Décrit les actions exposées pour configurer une partie. */
public interface IConfigurationController {

    record ValidationResult(boolean isValid, String errorMessage, int totalCells) {

        /** Crée un résultat valide avec le total de cases. */
        public static ValidationResult valid(int totalCells) {
            return new ValidationResult(true, null, totalCells);
        }

        /** Crée un résultat invalide avec message et total de cases. */
        public static ValidationResult invalid(String errorMessage, int totalCells) {
            return new ValidationResult(false, errorMessage, totalCells);
        }
    }

    /** Fournit les types de bateaux disponibles pour la configuration. */
    List<BoatTypeViewModel> getAvailableBoatTypes();

    /** Calcule le nombre total de cases pour les bateaux saisis. */
    int calculateTotalCells(Map<String, Integer> rawBoatCounts);

    /** Valide les paramètres de configuration de la partie. */
    ValidationResult validateConfiguration(Map<String, Integer> rawBoatCounts);

    /** Traite la configuration validée envoyée par la vue. */
    void handleConfigurationComplete(Map<String, Integer> rawBoatCounts, boolean isIslandMode, int aiLevel);
}
