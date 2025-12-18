package Controller.Game;

/** Exécute des actions sur le thread UI approprié. */
public interface UiExecutor {

    /** Programme une action à exécuter sur l'interface. */
    void execute(Runnable action);
}
