package Model.Player;

import Model.Map.Grid;

/** Fabrique des joueurs humains et ordinateurs. */
public interface PlayerFactory {

    HumanPlayer createHumanPlayer(String name, Grid ownGrid, Grid targetGrid);

    ComputerPlayer createComputerPlayer(Grid ownGrid, Grid targetGrid);
}
