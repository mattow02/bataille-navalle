package Model;

/** Entité capable d'appliquer un effet spécial. */
public interface SpecialEffectEntity extends GridEntity {

    /** Applique l'effet spécial associé. */
    void applySpecialEffect(SpecialEffectContext context, Coordinates target, boolean isHumanAttacker);

    @Override
    default void applySpecialEffectIfPresent(SpecialEffectContext context, Coordinates target, boolean isHumanAttacker) {
        applySpecialEffect(context, target, isHumanAttacker);
    }
}
