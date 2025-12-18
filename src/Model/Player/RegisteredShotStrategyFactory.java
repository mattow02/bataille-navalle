package Model.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Fabrique de stratégies de tir via registre. */
public class RegisteredShotStrategyFactory implements ShotStrategyFactory {
    private final Map<Integer, Supplier<ShotStrategy>> registry = new HashMap<>();

    public RegisteredShotStrategyFactory() {
        registerLevel(1, RandomShotStrategy::new);
        registerLevel(2, TargetedShotStrategy::new);
    }

    public void registerLevel(int level, Supplier<ShotStrategy> supplier) {
        if (supplier != null) {
            registry.put(level, supplier);
        }
    }

    @Override
    public ShotStrategy createForLevel(int computerShotLevel) {
        var supplier = registry.get(computerShotLevel);
        if (supplier != null) {
            return supplier.get();
        }
        return new TargetedShotStrategy();
    }
}
