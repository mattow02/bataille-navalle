package Model.Player;

/** Fabrique une stratégie de tir en fonction du niveau. */
public interface ShotStrategyFactory {

    ShotStrategy createForLevel(int computerShotLevel);
}
