package View;

import Controller.Placement.PlacementError;

/** Vue dédiée au placement des entités. */
public interface PlacementViewInterface extends GameView {

    void refreshView();

    void showError(PlacementError error);

    void showPlacementFinished();
}
