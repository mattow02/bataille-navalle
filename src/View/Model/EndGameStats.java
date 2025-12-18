package View.Model;

/** Données affichées à l'écran de fin de partie. */
public record EndGameStats(
        String resultMessage,
        int turnCount,
        int playerAliveBoats,
        int computerAliveBoats
) {}
