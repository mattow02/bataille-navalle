package View.Screen;

import Controller.Game.UiExecutor;

import javax.swing.SwingUtilities;

/** Exécuteur UI basé sur l'EDT Swing. */
public class SwingUiExecutor implements UiExecutor {

    @Override
    public void execute(Runnable action) {
        if (SwingUtilities.isEventDispatchThread()) {
            action.run();
        } else {
            SwingUtilities.invokeLater(action);
        }
    }
}
