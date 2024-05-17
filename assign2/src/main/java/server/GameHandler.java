package server;

import message.IO;
import message.MessageType;
import server.database.models.Nim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static server.Server.numberPlayers;

public class GameHandler implements Runnable{

    Nim game = new Nim(numberPlayers);
    List<ClientHandler> clients;

    public GameHandler(List<ClientHandler> clients) {
        this.clients = clients;
        System.out.println("Game started!");
        for (ClientHandler cl : clients){
            IO.writeMessage(cl.out,"Game started!", MessageType.MSG);
        }
    }

    public void run() {
        boolean run = true;
        ClientHandler currentPlayer = null;

        while (run) {
            try{
                currentPlayer = clients.get(game.getCurrentPlayer());
                IO.writeMessage(currentPlayer.out, "Write number of coins: ", MessageType.REQUEST);
                String inputLine = currentPlayer.readMessage();
                int move = Integer.parseInt(inputLine);
                int reminder_coins = game.move(move);

                if (reminder_coins == -1) {
                    continue;
                }

                run = reminder_coins > 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        currentPlayer.getUser().changeScore(10);
    }
}
