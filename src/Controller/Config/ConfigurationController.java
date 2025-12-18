package Controller.Config;

import Controller.IGameControllerCoordinator;
import Model.Boat.BoatType;
import Model.Games.GameConfiguration;
import Model.Games.GameConfigValidator;
import View.Model.BoatTypeViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Coordonne la configuration initiale de la partie. */
public record ConfigurationController(IGameControllerCoordinator mainController) implements IConfigurationController {

    private static final GameConfigValidator validator = new GameConfigValidator();

    /** Fournit la liste des types de bateaux disponibles. */
    public List<BoatTypeViewModel> getAvailableBoatTypes() {
        var viewModels = new ArrayList<BoatTypeViewModel>();
        for (var type : BoatType.values()) {
            viewModels.add(new BoatTypeViewModel(
                    type.name(),
                    type.getName(),
                    type.getSize()
            ));
        }
        return viewModels;
    }

    /** Calcule le total de cases occupées pour une configuration. */
    public int calculateTotalCells(Map<String, Integer> rawBoatCounts) {
        var boatCounts = toBoatCounts(rawBoatCounts);
        return validator.calculateTotalCells(boatCounts);
    }

    /** Valide la configuration fournie par la vue. */
    public IConfigurationController.ValidationResult validateConfiguration(Map<String, Integer> rawBoatCounts) {
        var boatCounts = toBoatCounts(rawBoatCounts);
        var result = validator.validate(boatCounts);
        if (!result.valid()) {
            var message = result.errorMessage();
            if (result.totalCells() > 0) {
                message += "\nActuel: " + result.totalCells() + " cases";
            }
            return IConfigurationController.ValidationResult.invalid(message, result.totalCells());
        }
        return IConfigurationController.ValidationResult.valid(result.totalCells());
    }

    /** Transmet une configuration validée au contrôleur principal. */
    public void handleConfigurationComplete(Map<String, Integer> rawBoatCounts, boolean isIslandMode, int aiLevel) {
        var validation = validateConfiguration(rawBoatCounts);
        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.errorMessage());
        }
        var boatCounts = toBoatCounts(rawBoatCounts);
        var config = new GameConfiguration(
                10,
                boatCounts,
                isIslandMode,
                aiLevel
        );
        mainController.handleConfigurationComplete(config);
    }

    private Map<BoatType, Integer> toBoatCounts(Map<String, Integer> rawBoatCounts) {
        var boatCounts = new HashMap<BoatType, Integer>();
        for (var entry : rawBoatCounts.entrySet()) {
            try {
                var type = BoatType.valueOf(entry.getKey());
                boatCounts.put(type, entry.getValue() != null ? entry.getValue() : 0);
            } catch (IllegalArgumentException e) {
                System.err.println("Type de bateau inconnu reçu de la vue : " + entry.getKey());
            }
        }
        return boatCounts;
    }
}
