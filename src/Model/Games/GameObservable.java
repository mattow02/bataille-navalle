package Model.Games;

import Model.GameObserver;

/** Permet d'abonner des observateurs du jeu. */
public interface GameObservable {

    /** Ajoute un observateur de partie. */
    void addObserver(GameObserver observer);

    /** Retire un observateur de partie. */
    void removeObserver(GameObserver observer);
}
