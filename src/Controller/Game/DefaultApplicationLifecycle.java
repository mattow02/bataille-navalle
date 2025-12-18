package Controller.Game;

/** Implémente la fermeture standard de l'application. */
public class DefaultApplicationLifecycle implements ApplicationLifecycle {

    /** Termine le processus courant. */
    @Override
    public void quit() {
        System.exit(0);
    }
}
