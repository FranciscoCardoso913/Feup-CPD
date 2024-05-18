package server;

import message.IO;
import message.MessageType;
import server.database.models.Nim;
import server.database.models.User;
import server.services.ConcurrentQueue;

import java.io.IOException;
import java.util.List;

import static server.Server.numberPlayers;

public class GameHandler implements Runnable{

    Nim game = new Nim(numberPlayers);
    List<ClientHandler> clients;
    ConcurrentQueue<ClientHandler> clientQueue;

    public GameHandler(List<ClientHandler> clients, ConcurrentQueue<ClientHandler> clientQueue) {
        this.clients = clients;
        this.clientQueue = clientQueue;

        System.out.println("Game started!");
        
        StringBuilder initMessage = new StringBuilder()
            .append("Game started!\n")
            .append("\t- The pile has " + game.getRemainingCoins() + " coins!\n")
            .append("\t- You can take 1 or 2 coins in each of your turns.\n")
            .append("\t- The player who takes the last coin wins!\n")
            .append("\t- There are " + numberPlayers + " in this gamen");
        
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

        updateRanks();

        for (ClientHandler cl : clients){
            Thread.ofVirtual().start(() -> {
                try {
                    checkPlayAgain(cl);
                } catch (IOException e) {
                    System.out.println("Error while getting input");
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void updateRanks() {
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

    public void checkPlayAgain(ClientHandler cl) throws IOException {
        while (true) {
            IO.writeMessage(cl.out, "Do you want to play again? (Y/n)", MessageType.REQUEST);
            String res = cl.readMessage();

            if (res.equals("y") || res.equals("Y") || res.isEmpty()) {
                this.clientQueue.push(cl);
                System.out.println("In queue again");
                break;
            } else if (res.equals("n") || res.equals("N")) {
                IO.writeMessage(cl.out, "QUIT", MessageType.QUIT);
                System.out.println("Exited?");
                break;
            }
        }
    }
}
