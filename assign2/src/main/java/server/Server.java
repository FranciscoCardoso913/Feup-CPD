package server;

import server.database.Database;
import server.services.Q;
import server.services.SimpleQueue;

import java.io.*;
import java.net.*;

public class Server {

    private Database db;
    private final int port;
    private final int mode;
    private final Q<ClientHandler> clientQueue;

    public Server(int port, int mode){
        this.port = port;
        this.mode = mode;
        this.clientQueue = switch (mode) {
            case 0 -> new SimpleQueue<>();
            case 1 -> new SimpleQueue<>();
            default -> new SimpleQueue<>();
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
        }
    }
}
