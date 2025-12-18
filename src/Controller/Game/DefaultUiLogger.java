package Controller.Game;

import View.BattleViewInterface;

import java.util.function.Supplier;

/** Acheminer les logs vers la vue de bataille. */
public class DefaultUiLogger implements UiLogger {

    private final Supplier<BattleViewInterface> battleViewSupplier;
    private final UiExecutor uiExecutor;

    /** Initialise le logger UI avec son exécuteur. */
    public DefaultUiLogger(Supplier<BattleViewInterface> battleViewSupplier, UiExecutor uiExecutor) {
        this.battleViewSupplier = battleViewSupplier;
        this.uiExecutor = uiExecutor;
    }

    /** Publie un message dans l'interface si possible. */
    @Override
    public void log(String message) {
        if (message == null) return;
        uiExecutor.execute(() -> {
            var view = battleViewSupplier != null ? battleViewSupplier.get() : null;
            if (view != null) {
                view.addLog(message);
            }
        });
    }
}
