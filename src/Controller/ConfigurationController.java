package Controller;

import Model.Boat.BoatType;
import Model.GameConfiguration;
import java.util.Map;

public record ConfigurationController(GameController mainController) implements Observer {

    public void handleConfigurationComplete(Map<BoatType, Integer> boatCounts, boolean isIslandMode, int aiLevel) {
        GameConfiguration config = new GameConfiguration(
                10,
                boatCounts,
                isIslandMode,
                0,
                aiLevel,
                1
        );

        mainController.handleConfigurationComplete(config);
    }

    @Override
    public void update(Object event) {
    }
}