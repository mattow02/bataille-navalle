package Model.Player;

import Model.Map.Grid;

/** Fabrique par défaut des joueurs. */
public class StandardPlayerFactory implements PlayerFactory {

    @Override
    public HumanPlayer createHumanPlayer(String name, Grid ownGrid, Grid targetGrid) {
        return new HumanPlayer(name, ownGrid, targetGrid);
    }

    @Override
    public ComputerPlayer createComputerPlayer(Grid ownGrid, Grid targetGrid) {
        return new ComputerPlayer(ownGrid, targetGrid);
    }
}
