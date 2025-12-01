package Model.Games;

import Model.Coordinates;

public class Game {
    private boolean gameOver = false;

    public void executeHumanTurn(Coordinates coord) {
    }

    public void executeComputerTurn() {
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}