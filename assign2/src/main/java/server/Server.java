package server;

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
    ServerSocket serverSocket = null;

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
        // System.out.println("Here out!");
        // System.out.println(clientQueue.size());

        if(this.clientQueue.has(numberPlayers)){
            List<ClientHandler> clients = clientQueue.popMultiple(numberPlayers);
            Thread.ofVirtual().start(new GameHandler(clients));
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

        Runtime.getRuntime().addShutdownHook(cleanUpThread);

        System.out.println("Server started");

        authThread.join();
        gameThread.join();
    }

    public void handleAuth() throws IOException {
        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            // Start a new thread to handle the client
            Thread thread = new Thread(() -> {
                try {
                    addClientHandler(clientSocket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
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
    }
}
