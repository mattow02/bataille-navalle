package Model.Weapons;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/** Fabrique des armes à partir de leur type. */
public class WeaponFactory {

    private static final WeaponFactory DEFAULT = WeaponFactory.withDefaults();

    private final Map<WeaponType, Supplier<Weapon>> registry;

    public WeaponFactory() {
        this(true);
    }

    public WeaponFactory(boolean registerDefaults) {
        this.registry = new EnumMap<>(WeaponType.class);
        if (registerDefaults) {
            registerDefaults();
        }
    }

    public static WeaponFactory withDefaults() {
        return new WeaponFactory(true);
    }

    private void registerDefaults() {
        register(WeaponType.MISSILE, Missile::new);
        register(WeaponType.BOMB, Bomb::new);
        register(WeaponType.SONAR, Sonar::new);
    }

    public WeaponFactory register(WeaponType type, Supplier<Weapon> supplier) {
        if (type != null && supplier != null) {
            registry.put(type, supplier);
        }
        return this;
    }

    public Weapon create(WeaponType type) {
        var supplier = registry.get(type);
        if (supplier == null) {
            throw new IllegalArgumentException("Type d'arme non supporté ou non enregistré : " + type);
        }
        return supplier.get();
    }

    public static void registerWeapon(WeaponType type, Supplier<Weapon> supplier) {
        DEFAULT.register(type, supplier);
    }

    public static Weapon createWeapon(WeaponType type) {
        return DEFAULT.create(type);
    }
}
