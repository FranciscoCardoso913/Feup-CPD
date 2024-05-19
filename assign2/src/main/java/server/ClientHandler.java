package server;

import server.database.Database;
import server.database.models.User;
import server.services.AuthService;
import server.services.RegisterService;
import message.*;
import utils.Wrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.*;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private final RegisterService registerService;
    private final AuthService authService;
    private User user = null;
    private long sessionStartTime;
    
    private long lastSeen;
    private String reconnectionMSG;
    public volatile PrintWriter out;
    public volatile BufferedReader in;

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

    public String readMessage(){
        String inputLine;
        try {
            while ((inputLine = in.readLine()) == null) {}

            System.out.println("Client: " + inputLine);
            return inputLine;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;

    }

    public void run() {
        try {
            boolean result = false;
            while(!result) {
                IO.writeMessage(out, "[1]Login\n[2]Register\nChoose an option:", MessageType.REQUEST);
                String inputLine = readMessage();
                result = switch (inputLine) {
                    case "1" -> authService();
                    case "2" -> registerService();
                    case "quit" -> quit();
                    default -> invalidOption();
                };
            }
        } catch (Exception e) {
            try {
                this.quit();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }

    public boolean authService() throws Exception {
            User res = Wrapper.withTimeOut(
                    () -> this.authService.authUser(out, in),
                    40,
                    () -> this.authService.handleTimeOut(out)
            );
            this.setUser(res);
            return res != null;
    }
    public boolean registerService() throws Exception {
        User res = Wrapper.withTimeOut(
                ()->this.registerService.registerUser(out, in),
                40,
                ()-> this.registerService.handleTimeOut(out)
        );
        this.setUser(res);
        return res!=null;
    }
    public boolean quit() throws IOException {
        IO.writeMessage(this.out, "QUIT", MessageType.QUIT);
        this.close();
        return true;
    }
    public boolean invalidOption(){
        IO.writeMessage(this.out, "Invalid Option", MessageType.MSG);
        return false;
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
