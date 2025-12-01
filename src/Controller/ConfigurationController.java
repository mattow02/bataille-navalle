package Controller;

import Model.Boat.BoatType;
import Model.GameConfiguration;
import java.util.Map;

public class ConfigurationController implements Observer {
    private GameController mainController;

    public ConfigurationController(GameController mainController) {
        this.mainController = mainController;
    }

    public void handleConfigurationComplete(Map<BoatType, Integer> boatCounts, boolean isIslandMode) {
        GameConfiguration config = new GameConfiguration(
                10,
                boatCounts,
                isIslandMode,
                0,
                1,
                1
        );

        mainController.handleConfigurationComplete(config);
    }

    @Override
    public void update(Object event) {
    }
}