package Model.Games;

/** Agrège les capacités lecture, commande et configuration du jeu. */
public interface IGame extends GameReader, GameCommander, GameSetup, GameObservable {
}
