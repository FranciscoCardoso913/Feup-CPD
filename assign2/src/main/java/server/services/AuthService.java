package server.services;

import message.IO;
import message.MessageType;
import server.ClientHandler;
import server.database.Database;
import server.database.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AuthService {
    private final Database db;
    int loginTimeOut = 40;
    final int MAX_ATTEMPS = 3;

    public AuthService(Database db){
        this.db = db;
    }

    public User authUser(PrintWriter out, BufferedReader in){

        System.out.println("Authenticating");
        int attemps = 0;
        String username;
        String password;


        try {
            while (attemps < MAX_ATTEMPS) {
                IO.writeMessage(out, "Insert your username:", MessageType.REQUEST);
                username = in.readLine();
                IO.writeMessage(out, "Insert your password:", MessageType.REQUEST);
                password = in.readLine();
                attemps++;
    
                if (username == null || password == null) {
                    throw new IOException("Client closed connection");
                }
    
                User tmpUser = db.findUserByName(username);
    
                if (tmpUser == null) {
                    IO.writeMessage(out, "Username doesn't exist", MessageType.MSG);
                    continue;
                }
    
                if (!tmpUser.login(password)) {
                    IO.writeMessage(out, "Incorrect username or password", MessageType.MSG);
                    continue;
                }
                IO.writeMessage(out, "Login successful!", MessageType.MSG);
                return tmpUser;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
    public void handleTimeOut(PrintWriter out){
        System.out.println("Time out");
        IO.writeMessage(out, "Login timed out. Please try again.", MessageType.MSG);
    }
}
