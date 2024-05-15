package server;

import server.database.Database;
import server.services.ConcurrentQueue;
import server.services.RankedQueue;
import server.services.SimpleQueue;

import java.io.*;
import java.net.*;

public class Server {

    private Database db;
    private final int port;
    private final ConcurrentQueue<ClientHandler> clientQueue;

    public Server(int port, int mode){
        this.port = port;
        this.clientQueue = switch (mode) {
            case 0 -> new SimpleQueue(3);
            case 1 -> new RankedQueue(3);
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };
    }

    public void addClientHandler(Socket clientSocket){
        ClientHandler ch = new ClientHandler(clientSocket, db);
        ch.run();
        this.clientQueue.push(ch);
    }

    public void main() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started. Waiting for clients...");
        this.db = new Database();

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            // Start a new thread to handle the client
            Thread thread = new Thread(() -> addClientHandler(clientSocket));
            thread.start();
            try {
                thread.join();
            } catch(InterruptedException e) {}
            // this.clientQueue.has(1);
        }
    }
}
