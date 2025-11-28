package Controller;

import Model.Boat.BoatType;
import Model.Game.GameConfiguration;
import java.util.Map;

public class ConfigurationController implements Observer {
    private GameController mainController;

    public ConfigurationController(GameController mainController) {
        this.mainController = mainController;
    }

    public void handleConfigurationComplete(Map<BoatType, Integer> boatCounts) {
        for (Map.Entry<BoatType, Integer> entry : boatCounts.entrySet()) {
            System.out.println("  - " + entry.getKey().getName() + ": " + entry.getValue());
        }

        // Créer la configuration (autres valeurs par défaut pour l'instant)
        GameConfiguration config = new GameConfiguration(
                10, // Taille grille
                boatCounts,
                false, // Mode île
                0,     // Items spéciaux
                1,     // Niveau IA
                1      // Niveau placement
        );

        mainController.handleConfigurationComplete(config);
    }

    @Override
    public void update(Object event) {
        // Gérer les événements de configuration
    }
}