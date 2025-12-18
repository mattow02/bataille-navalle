package Model.Player;

/** Fabrique une stratégie de tir selon le niveau IA. */
public class LevelBasedShotStrategyFactory implements ShotStrategyFactory {

    @Override
    public ShotStrategy createForLevel(int computerShotLevel) {
        if (computerShotLevel == 1) {
            return new RandomShotStrategy();
        }
        return new TargetedShotStrategy();
    }
}
