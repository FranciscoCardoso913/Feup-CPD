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
    private final int port;
    private final ConcurrentQueue<ClientHandler> clientQueue;
    static int numberPlayers = 3;

    public Server(int port, int mode){
        this.db = new Database();
        this.port = port;
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
        this.clientQueue.push(ch);
    }

    public void addGame() {
        if(this.clientQueue.has(numberPlayers)){
            List<ClientHandler> clients = clientQueue.popMultiple(numberPlayers);
            clients.get(0).out.println("Hello\0");
            Thread thread = new Thread(new GameHandler(clients));
            thread.start();
        }
        System.out.println("In queue: ");
        System.out.print(this.clientQueue.size());
        System.out.println();
    }

    public void main() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started. Waiting for clients...");
        this.db = new Database();

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
            try {
                thread.join();
            } catch(InterruptedException e) {}

            // addGame();
        }
    }

    /*public void main() throws IOException {

        Thread.ofVirtual().start(() -> {
            try {
                handleAuth();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        Thread.ofVirtual().start(() -> handleGames());
        Thread saveState = new Thread(() -> db.save());

        Runtime.getRuntime().addShutdownHook(saveState);

        System.out.println("Server started");
    }*/

    public void handleAuth() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);

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
}
