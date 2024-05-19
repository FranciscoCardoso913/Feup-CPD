package server.services;

import config.ConfigLoader;
import message.IO;
import message.MessageType;
import server.database.Database;
import server.database.models.User;
import utils.Wrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthService {
    private final Database db;
    int loginTimeOut;
    int MAX_ATTEMPTS;

    public AuthService(Database db, ConfigLoader configLoader){
        this.db = db;
        this.loginTimeOut= Integer.parseInt( configLoader.get("AUTH_TIMEOUT"));
        this.MAX_ATTEMPTS = Integer.parseInt( configLoader.get("LOGIN_MAX_ATTEMPTS"));
    }

    public User authUser(PrintWriter out, BufferedReader in){

        System.out.println("Authenticating");
        int attemps = 0;
        String username;
        String password;


        try {
            while (attemps < MAX_ATTEMPTS) {
                IO.writeMessage(out, "Insert your username:", MessageType.REQUEST);
                username = Wrapper.readWithTimeout(in, loginTimeOut, ()->this.handleTimeOut(out)) ;
                if(username==null)return null;
                IO.writeMessage(out, "Insert your password:", MessageType.REQUEST);
                password = Wrapper.readWithTimeout(in, loginTimeOut, ()->this.handleTimeOut(out)) ;
                if(password==null) return null;
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
