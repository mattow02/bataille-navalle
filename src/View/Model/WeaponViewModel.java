package View.Model;

/** View model d'une arme affichée dans l'arsenal. */
public record WeaponViewModel(
        String id,
        String label,
        boolean enabled
) {}
