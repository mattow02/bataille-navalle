package Model.Game;

import Model.Coordinates;

public class Game {
    private boolean gameOver = false;

    public void executeHumanTurn(Coordinates coord) {
        System.out.println("Joueur attaque: " + coord);
    }

    public void executeComputerTurn() {
        System.out.println("Ordinateur joue son tour");
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}