package Model.Weapons;

import Model.HitOutcome;

/** Résultat d'utilisation d'une arme. */
public record WeaponResult(
        WeaponType type,
        boolean endsTurn,
        boolean requiresRefresh,
        HitOutcome primaryOutcome,
        int value,
        boolean success
) {}
