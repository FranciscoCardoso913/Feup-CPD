package server;

import server.database.Database;
import server.database.models.User;
import server.services.AuthService;
import server.services.RegisterService;
import message.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private final RegisterService registerService;
    private final AuthService authService;
    private User user = null;
    private long sessionStartTime;
    
    private long lastSeen;
    private String reconnectionMSG;
    public PrintWriter out;
    public BufferedReader in;

    public ClientHandler(Socket clientSocket, Database db) throws IOException {
        this.clientSocket = clientSocket;
        this.registerService = new RegisterService(db);
        this.authService = new AuthService(db);

        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.sessionStartTime = System.currentTimeMillis();
        this.lastSeen = System.currentTimeMillis();
        this.reconnectionMSG = "Reconnected, waiting in queue";
    }

    public void close() throws IOException {
        out.close();
        in.close();
        clientSocket.close();
    }

    public long getSessionStartTime() {
        return sessionStartTime;
    }

    public void updateSessionStartTime() {
        sessionStartTime = System.currentTimeMillis();
    }

    public void setUser(User user) {this.user = user;}

    public User getUser() {
        return user;
    }
    
    public long getLastSeen() {
        return this.lastSeen;
    }

    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis();
    }

    public void setSocket(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;

        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        IO.writeMessage(out, this.reconnectionMSG, MessageType.MSG);
    }

    public Socket getSocket() {
        return clientSocket;
    }

    public String readMessage() throws IOException {
        String inputLine;
        while ((inputLine = in.readLine()) == null) {}

        System.out.println("Client: " + inputLine);
        return inputLine;
    }

    public void run() {
        try {
            IO.writeMessage(out,"[1]Login\n[2]Register\nChoose an option:", MessageType.REQUEST);
            String inputLine = readMessage();
            String result = switch (inputLine) {
                case "1" -> this.authService.authUser(out, in, this);
                case "2" -> this.registerService.registerUser(out, in, this.user);
                case "quit" -> "User quited\0";
                default -> "Invalid option\0";
            };

            System.out.println(result + " for " + user.getName());
            IO.writeMessage(out, result, MessageType.MSG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkConnection() {
        IO.writeMessage(this.out, "PING", MessageType.PING);
        String res;
		
        try {
			res = this.in.readLine();

            if (res != null) {
                updateLastSeen();
                return true;
            }
		} catch (IOException e) {
            System.out.println("IOException when pinging client");
			e.printStackTrace();
		}
        
        return false;
    }

    public void setReconnectionMSG(String msg){
        this.reconnectionMSG = msg;
    }
}
