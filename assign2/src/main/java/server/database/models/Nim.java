package server.database.models;

import java.util.Random;

public class Nim {
    private int coins;
    private int currentPlayer;
    private int nPlayers;

    public Nim(int nPlayers) {
        Random random = new Random();
        this.nPlayers = nPlayers;
        int lowerBound = this.nPlayers * 8 - 7;
        int upperBound = this.nPlayers * 8 + 7;
        this.coins = lowerBound + random.nextInt(upperBound - lowerBound + 1);
        this.currentPlayer = 0;
    }

    // Method to make a move
    public int move(int coinsTaken) {
        if (coinsTaken < 1 || coinsTaken > 2) {
            return -1; // TODO: bad select.
        }
        if (coinsTaken >= coins) {
            return -2;  // TODO: end game.
        }

        // Update the state
        coins -= coinsTaken;
        currentPlayer = (currentPlayer + 1) % nPlayers;

        return getRemainingCoins();
    }
 
    // Method to get current player
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    // Method to get remaining coins
    public int getRemainingCoins() {
        return coins;
    }

    public boolean isEnd() {
        return getRemainingCoins() == 0;
    }
}
