package server;

import message.IO;
import message.MessageType;
import server.database.models.Nim;
import server.database.models.User;
import server.services.ConcurrentQueue;
import utils.Wrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class GameHandler implements Runnable{

    Nim game;
    List<ClientHandler> clients;
    volatile ConcurrentQueue<ClientHandler> clientQueue;
    int playersPerGame;
    int playTimeOut;

    public GameHandler(List<ClientHandler> clients, ConcurrentQueue<ClientHandler> clientQueue, int PLAYER_PER_GAME, int PLAY_TIMEOUT) {
        this.game = new Nim(PLAYER_PER_GAME);
        this.playersPerGame = PLAYER_PER_GAME;
        this.clients = clients;
        this.clientQueue = clientQueue;
        this.playTimeOut = PLAY_TIMEOUT;
        
        StringBuilder initMessage = new StringBuilder()
            .append("Game started!\n")
            .append("\t- The pile has " + game.getRemainingCoins() + " coins!\n")
            .append("\t- You can take 1 or 2 coins in each of your turns.\n")
            .append("\t- The player who takes the last coin wins!\n")
            .append("\t- There are " + PLAYER_PER_GAME + " in this gamen");
        
        for (ClientHandler cl : clients){
            IO.writeMessage(cl.out, "CLEAR", MessageType.CMD);
            IO.writeMessage(cl.out, initMessage.toString(), MessageType.MSG);
        }
    }

    public void run() {
        boolean run = true;

        while (run) {
            try{
                final ClientHandler currentPlayer = clients.get(game.getCurrentPlayer());
                IO.writeMessage(currentPlayer.out, "Your turn! You have "+ this.playTimeOut +"s to play!" , MessageType.MSG);
                IO.writeMessage(currentPlayer.out, "Coins in the pile: " + game.getRemainingCoins(), MessageType.MSG);
                IO.writeMessage(currentPlayer.out, "Write number of coins: ", MessageType.REQUEST);

                informOtherPlayers();
                String inputLine = Wrapper.readWithTimeout(
                        currentPlayer.in,
                        10,
                        this::randomMove
                );
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
        for (int i = 0; i < playersPerGame; i++){

            ClientHandler cl = clients.get(i);
 
            User currUser = cl.getUser();
            
            if( !cl.checkConnection())
               currUser.changeScore(-1);
            else if (i != game.getCurrentPlayer()){
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
        if(!cl.checkConnection()){
            cl.close();
            return;
        }
        while (true) {
            IO.writeMessage(cl.out, "Do you want to play again? (Y/n)", MessageType.REQUEST);
            String res = cl.readMessage();
            if (res.equals("y") || res.equals("Y") || res.isEmpty()) {
                cl.updateSessionStartTime();
                this.clientQueue.push(cl);
                cl.setReconnectionMSG("Reconnected, waiting in queue");
                System.out.println("In queue again");
                IO.writeMessage(cl.out, "CLEAR", MessageType.CMD);
                IO.writeMessage(cl.out,"You are waiting in queue again!",MessageType.MSG);
                break;
            } else if (res.equals("n") || res.equals("N")) {
                IO.writeMessage(cl.out, "QUIT", MessageType.QUIT);
                break;
            }
        }
    }
    public void informOtherPlayers(){
        int currentPlayer = game.getCurrentPlayer();
        User user = this.clients.get(currentPlayer).getUser();
        for (int i = 0; i < playersPerGame; i++){

            ClientHandler cl = clients.get(i);

            if(i!= currentPlayer){
                IO.writeMessage(cl.out, user.getName()+ " turn!", MessageType.MSG);
                IO.writeMessage(cl.out, "Coins in the pile: " + game.getRemainingCoins(), MessageType.MSG);
            }
        }
    }

    public String randomMove(){
        try {
            final ClientHandler currentPlayer = clients.get(game.getCurrentPlayer());

            int randomMove = new Random().nextInt(2) + 1;

            IO.writeMessage(currentPlayer.out, "Time's up! Random move: " + randomMove, MessageType.MSG);
            return String.valueOf(randomMove);
        } catch (Exception e) {
            e.printStackTrace();
            return "1";
        }
    }
}
