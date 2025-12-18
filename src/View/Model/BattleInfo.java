package View.Model;

/** Données affichées dans le panneau d'information de bataille. */
public record BattleInfo(
        int turnCount,
        int playerAliveBoats,
        int enemyAliveBoats,
        int bombCount,
        int sonarCount,
        boolean hasBomb,
        boolean hasSonar
) {}
