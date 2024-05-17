package server;

import message.IO;
import message.MessageType;
import server.database.models.Nim;
import server.database.models.User;

import java.util.List;

import static server.Server.numberPlayers;

public class GameHandler implements Runnable{

    Nim game = new Nim(numberPlayers);
    List<ClientHandler> clients;

    public GameHandler(List<ClientHandler> clients) {
        this.clients = clients;
        System.out.println("Game started!");
        
        StringBuilder initMessage = new StringBuilder()
            .append("Game started!\n")
            .append("\t- The pile has " + game.getRemainingCoins() + " coins!\n")
            .append("\t- You can take 1 or 2 coins in each of your turns.\n")
            .append("\t- The player who takes the last coin wins!\n")
            .append("\t- There are " + numberPlayers + "in this gamen");
        
        for (ClientHandler cl : clients){
            IO.writeMessage(cl.out, initMessage.toString(), MessageType.MSG);
        }
    }

    public void run() {
        boolean run = true;
        ClientHandler currentPlayer = null;

        while (run) {
            try{
                currentPlayer = clients.get(game.getCurrentPlayer());
                IO.writeMessage(currentPlayer.out, "Coins in the pile: " + game.getRemainingCoins(), MessageType.MSG);
                IO.writeMessage(currentPlayer.out, "Write number of coins: ", MessageType.REQUEST);
                String inputLine = currentPlayer.readMessage();
                int move = Integer.parseInt(inputLine);
                int reminder_coins = game.move(move);

                if (reminder_coins == -1) {
                    IO.writeMessage(currentPlayer.out, "Invalid number of coins taken: " + move, MessageType.MSG);
                    IO.writeMessage(currentPlayer.out, "Take 1 or 2 coins.", MessageType.MSG);
                    continue;
                }

                run = reminder_coins != -2;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < numberPlayers; i++){
            ClientHandler cl = clients.get(i);
            User currUser = cl.getUser();
            
            if (i != game.getCurrentPlayer()){
                IO.writeMessage(cl.out, "You lost...", MessageType.MSG);
                currUser.changeScore(-1);
            }else{
                IO.writeMessage(cl.out, "You won!", MessageType.MSG);
                currUser.changeScore(3);
            }
            IO.writeMessage(cl.out, "New rating: " + currUser.getScore(), MessageType.MSG);
            
        }
    }
}
