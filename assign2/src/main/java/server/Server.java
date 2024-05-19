package server;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ExecutorService;

import config.ConfigLoader;
import message.IO;
import message.MessageType;
import server.database.Database;
import server.services.ConcurrentQueue;
import server.services.RankedQueue;
import server.services.SimpleQueue;
import utils.Wrapper;

import java.io.*;
import java.net.*;
import java.util.List;
import java.lang.Thread;
import java.util.HashSet;
import java.util.Set;


public class Server {

    private Database db;
    private volatile ConcurrentQueue<ClientHandler> clientQueue;
    private volatile Set<ClientHandler> clientsInGame;

    protected final ReentrantLock gameLock = new ReentrantLock();
    ServerSocket serverSocket = null;
    ExecutorService gameThreadPool;
    ExecutorService clientThreadPool;
    
    // Default values
    int CLIENT_TIMEOUT;
    int PLAYER_PER_GAME;
    int PLAY_TIMEOUT;
    int PING_PERIOD;

    private ConfigLoader configLoader;
    
    public Server(ConfigLoader config, int mode){
        try {
            this.configLoader= config;
            int port = Integer.parseInt(config.get("SERVER_PORT"));
            this.serverSocket = new ServerSocket(port);
            this.CLIENT_TIMEOUT = Integer.parseInt(config.get("CLIENT_TIMEOUT"));
            this.PLAYER_PER_GAME = Integer.parseInt(config.get("PLAYER_PER_GAME"));
            this.PING_PERIOD = Integer.parseInt(config.get("PING_PERIOD"));
            this.PLAY_TIMEOUT = Integer.parseInt(config.get("PLAY_TIMEOUT"));
            this.db = new Database(config.get("DB_PATH"));
            this.clientQueue = switch (mode) {
                case 0 -> new SimpleQueue(PLAYER_PER_GAME);
                case 1 -> new RankedQueue(PLAYER_PER_GAME);
                default -> throw new IllegalStateException("Unexpected value: " + mode);
            };
            this.gameThreadPool = Executors.newVirtualThreadPerTaskExecutor();
            this.clientThreadPool = Executors.newVirtualThreadPerTaskExecutor();

            this.clientsInGame = new HashSet<>();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void addClientHandler(Socket clientSocket) throws IOException {
        ClientHandler ch = new ClientHandler(clientSocket, db, this.configLoader );
        ch.run();
        if(ch.getUser()== null) return;
        ch.updateSessionStartTime();
        // TODO
        if (!ch.getUser().isEmpty()){
            boolean inGame = false;
            for(ClientHandler cl : this.clientsInGame){
                if(cl.getUser().equals(ch.getUser())){
                    inGame = true;
                    cl.setSocket(ch.getSocket());
                }
            }
            if(!inGame){

                this.clientQueue.push(ch);
                IO.writeMessage(ch.out, "Waiting in Queue for a Game!", MessageType.MSG);
                
                System.out.print("In queue: ");
                System.out.print(this.clientQueue.size());
                System.out.println();
            }
        }
    }

    // TODO: Maybe keep number of connected players for efficiency
    public void addGame() {
       if (this.clientQueue.has(PLAYER_PER_GAME)) {
            List<ClientHandler> clients = clientQueue.popMultiple(PLAYER_PER_GAME);
            if (clients == null) return;
            clients.forEach( clientHandler -> clientHandler.setReconnectionMSG("Reconnected, In the middle of a Game"));

            gameThreadPool.execute(() -> {
                Wrapper.withLock(()->this.clientsInGame.addAll(clients), this.gameLock);
                (new GameHandler(clients, this.clientQueue, this.PLAYER_PER_GAME, this.PLAY_TIMEOUT)).run();
                Wrapper.withLock(()-> clients.forEach(this.clientsInGame::remove), this.gameLock);
            });
       }
    }

    public void main() throws InterruptedException {
        Thread authThread = Thread.ofVirtual().start(() -> {
            try {
                handleAuth();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread gameThread = Thread.ofVirtual().start(() -> handleGames());
        Thread cleanUpThread = new Thread(() -> cleanUp());
        Thread pingClientsThread = Thread.ofVirtual().start(() -> pingActiveClients()); // this::pingActiveClients
        Runtime.getRuntime().addShutdownHook(cleanUpThread);

        System.out.println("Server started");

        authThread.join();
        gameThread.join();
        pingClientsThread.join();
    }

    public void handleAuth() throws IOException {
        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            // Start a new thread to handle the client
            this.clientThreadPool.execute(() -> {
                try {
                    addClientHandler(clientSocket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void handleGames() {
        while (true) {
            addGame();
        }
    }

    public void cleanUp() {
        db.save();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.gameThreadPool.shutdown();
        this.clientThreadPool.shutdown();
    }

    public void pingActiveClients() {
        while(true){
            System.out.println("[PING] All clients");

            long currTime = System.currentTimeMillis();

            // Ping
            this.clientQueue.removeIf(clientHandler -> {
                clientHandler.checkConnection();
                return currTime - clientHandler.getLastSeen() >= CLIENT_TIMEOUT;
            });
            

            // TODO: remover de set?

            System.out.println("Queue size: " + this.clientQueue.size());

            // this.clientQueue.forEach((clientHandler) -> {
            //     clientHandler.checkConnection();
            //     if (System.currentTimeMillis() - clientHandler.getLastSeen() >= CLIENT_TIMEOUT) {
            //         this.clientQueue.remove(clientHandler); // TODO Use iterators for higher efficiency
            //         System.out.println("Removed user");
            //         System.out.println("Queue size: " + this.clientQueue.size());
            //     }
            // });

            // Sleep 
            try {
                Thread.sleep(PING_PERIOD);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Ping thread interrupted, stopping.");
                break;
            }
        }
    }
}
