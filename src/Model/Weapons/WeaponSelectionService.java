package Model.Weapons;

import Model.Games.GameReader;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;

/** Évalue la disponibilité des armes pour le joueur. */
public class WeaponSelectionService {

    public enum Status {
        ACTIVATED,
        NOT_AVAILABLE,
        INVALID_SUBMARINE
    }

    public record WeaponSelectionResult(WeaponType selectedType, Status status) {}

    @FunctionalInterface
    public interface SelectionRule extends BiFunction<WeaponType, GameReader, WeaponSelectionResult> {}

    private final Map<WeaponType, SelectionRule> rules = new EnumMap<>(WeaponType.class);

    public WeaponSelectionService() {
        registerDefaultRules();
    }

    public void registerRule(WeaponType type, SelectionRule rule) {
        if (type != null && rule != null) {
            rules.put(type, rule);
        }
    }

    public WeaponSelectionResult evaluate(WeaponType requested, GameReader game) {
        var safeRequested = requested != null ? requested : WeaponType.MISSILE;
        var rule = rules.get(safeRequested);
        if (rule != null) {
            return rule.apply(safeRequested, game);
        }
        return new WeaponSelectionResult(WeaponType.MISSILE, Status.ACTIVATED);
    }

    private void registerDefaultRules() {
        registerRule(WeaponType.BOMB, (type, game) -> {
            if (game.hasPlayerBomb()) {
                return new WeaponSelectionResult(WeaponType.BOMB, Status.ACTIVATED);
            }
            return new WeaponSelectionResult(WeaponType.MISSILE, Status.NOT_AVAILABLE);
        });

        registerRule(WeaponType.SONAR, (type, game) -> {
            if (game.hasPlayerSonar() && game.isPlayerSubmarineAlive()) {
                return new WeaponSelectionResult(WeaponType.SONAR, Status.ACTIVATED);
            }
            return new WeaponSelectionResult(WeaponType.MISSILE, Status.INVALID_SUBMARINE);
        });

        registerRule(WeaponType.MISSILE, (type, game) -> new WeaponSelectionResult(WeaponType.MISSILE, Status.ACTIVATED));
    }
}
