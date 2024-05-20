package server.database.models;

import java.util.Random;

public class Nim {
    private int coins;
    private int currentPlayer;
    private int nPlayers;
    /**
     * Constructor for the Nim class.
     * Initializes the game with a random number of coins within a specified range.
     *
     * @param nPlayers The number of players in the game.
     */
    public Nim(int nPlayers, int coins) {
        Random random = new Random();
        this.nPlayers = nPlayers;
        int lowerBound = this.nPlayers * 8 - 7;
        int upperBound = this.nPlayers * 8 + 7;
        this.coins = lowerBound + random.nextInt(upperBound - lowerBound + 1);
        this.currentPlayer = 0;

        // Clean
        this.coins = coins;
    }

    /**
     * Method to make a move in the game.
     *
     * @param coinsTaken The number of coins taken by the current player.
     * @return The number of remaining coins after the move.
     *         Returns -1 if the move is invalid (less than 1 or more than 2 coins taken).
     *         Returns -2 if the game is ended.
     */
    public int move(int coinsTaken) {
        if (coinsTaken < 1 || coinsTaken > 2) {
            return -1;
        }
        if (coinsTaken >= coins) {
            return -2;
        }

        // Update the state
        coins -= coinsTaken;
        currentPlayer = (currentPlayer + 1) % nPlayers;

        return getRemainingCoins();
    }

    /**
     * Getter for the current player.
     *
     * @return The index of the current player.
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Getter for the remaining coins.
     *
     * @return The number of remaining coins in the game.
     */
    public int getRemainingCoins() {
        return coins;
    }

    public boolean isEnd() {
        return getRemainingCoins() == 0;
    }
}
