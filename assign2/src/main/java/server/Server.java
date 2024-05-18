package server;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import server.database.Database;
import server.services.ConcurrentQueue;
import server.services.RankedQueue;
import server.services.SimpleQueue;

import java.io.*;
import java.net.*;
import java.util.List;
import java.lang.Thread;


public class Server {

    private Database db;
    private volatile ConcurrentQueue<ClientHandler> clientQueue;
    static int numberPlayers = 2;
    static int pingPeriod = 10000;
    ServerSocket serverSocket = null;
    ExecutorService gameThreadPool;
    ExecutorService clientThreadPool;
    private int CLIENT_TIMEOUT = 20000;
    
    public Server(int port, int mode){
        try{
            this.serverSocket = new ServerSocket(port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.db = new Database();
        this.clientQueue = switch (mode) {
            case 0 -> new SimpleQueue(numberPlayers);
            case 1 -> new RankedQueue(numberPlayers);
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };
        this.gameThreadPool = Executors.newVirtualThreadPerTaskExecutor();
        this.clientThreadPool = Executors.newVirtualThreadPerTaskExecutor();
    }

    public void addClientHandler(Socket clientSocket) throws IOException {
        ClientHandler ch = new ClientHandler(clientSocket, db);
        ch.run();
        // TODO
        if (!ch.getUser().isEmpty()){
            this.clientQueue.push(ch);
        }
        
        System.out.print("In queue: ");
        System.out.print(this.clientQueue.size());
        System.out.println();
    }

    public void addGame() {
        if(this.clientQueue.has(numberPlayers)){
            List<ClientHandler> clients = clientQueue.popMultiple(numberPlayers);
            gameThreadPool.execute(new GameHandler(clients, this.clientQueue)); 
        }
    }

    public void main() throws IOException, InterruptedException {
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

            this.clientQueue.forEach((clientHandler) -> {
                clientHandler.checkConnection();
                if (System.currentTimeMillis() - clientHandler.getLastSeen() >= CLIENT_TIMEOUT) {
                    this.clientQueue.remove(clientHandler); // TODO Use iterators for higher efficiency
                    System.out.println("Removed user");
                    System.out.println("Queue size: " + this.clientQueue.size());
                }
            });
            try {
                Thread.sleep(pingPeriod);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Ping thread interrupted, stopping.");
                break;
            }
        }
        
        
        /*
        while (!Thread.currentThread().isInterrupted()) {
            this.clientQueue.forEach((clientHandler) -> {
                clientHandler.checkConnection();
                System.out.println("pingActiveClients() - Client pinged");
            });
    
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Ping thread interrupted, stopping.");
                break;
            }
        }*/
    }
}
