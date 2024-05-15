package server;

import server.database.Database;
import server.services.RegisterService;

import java.io.*;
import java.net.*;

public class Server {

    private Database db;
    private RegisterService registerService;
    public void main() throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Server started. Waiting for clients...");
        this.db = new Database();

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            // Start a new thread to handle the client
            Thread thread = new Thread(new ClientHandler(clientSocket, db));
            thread.start();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private RegisterService registerService;

    public ClientHandler(Socket clientSocket, Database db) {
        this.clientSocket = clientSocket;
        this.registerService = new RegisterService(db);
    }
    public String readMessage(BufferedReader in) throws IOException {
        String inputLine;
        while ((inputLine = in.readLine()) == null) {


        }
        System.out.println("Client: " + inputLine);
        return inputLine;

    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.println("[1]Login\n[2]Register\nChoose an option:\0");

            String inputLine =readMessage(in);
            String result = switch (inputLine) {
                case "1" -> "Login\0";
                case "2" -> this.registerService.registerUser(out,in);
                case "quit" -> "User quited\0";
                default -> "Invalid option\0";
            };
            System.out.println(result);
            out.println(result);
            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
