package server;

import server.database.Database;
import server.database.models.User;
import server.services.AuthService;
import server.services.RegisterService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private final RegisterService registerService;
    private final AuthService authService;
    private final User user = new User();
    private long sessionStartTime = System.currentTimeMillis();


    public ClientHandler(Socket clientSocket, Database db) {
        this.clientSocket = clientSocket;
        this.registerService = new RegisterService(db);
        this.authService = new AuthService(db);
    }

    public long getSessionStartTime() {
        return sessionStartTime;
    }

    public void updateSessionStartTime() {
        sessionStartTime = System.currentTimeMillis();
    }

    public User getUser() {
        return user;
    }

    public void setSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public Socket getSocket() {
        return clientSocket;
    }

    public String readMessage(BufferedReader in) throws IOException {
        String inputLine;

        while ((inputLine = in.readLine()) == null) {}

        System.out.println("Client: " + inputLine);
        return inputLine;
    }

    public void run() {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.println("[1]Login\n[2]Register\nChoose an option:\0");

            String inputLine = readMessage(in);
            String result = switch (inputLine) {
                case "1" -> this.authService.authUser(out, in, this.user);
                case "2" -> this.registerService.registerUser(out, in, this.user);
                case "quit" -> "User quited\0";
                default -> "Invalid option\0";
            };

            System.out.println(result + " for " + user.getName());
            out.println(result);

            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
