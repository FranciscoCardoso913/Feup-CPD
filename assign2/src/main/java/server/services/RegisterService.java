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

public class RegisterService {
    private Database db;

    protected final int registerTimeout;
    /**
     * Constructor for RegisterService.
     *
     * @param db            The database instance.
     * @param configLoader  The configuration loader to fetch the timeout value.
     */
    public RegisterService(Database db, ConfigLoader configLoader ){
        this.db = db;
        this.registerTimeout= Integer.parseInt( configLoader.get("AUTH_TIMEOUT"));
    }

    /**
     * Registers a new user.
     *
     * @param out The PrintWriter to send messages to the client.
     * @param in  The BufferedReader to receive messages from the client.
     * @return    The registered User object if registration is successful, otherwise null.
     */
    public User registerUser (PrintWriter out, BufferedReader in){
        String username = "";
        try {
            while (true) {
                IO.writeMessage(out, "Insert your username:", MessageType.REQUEST);
                username =  Wrapper.readWithTimeout(in, registerTimeout, ()->this.handleTimeOut(out));
                if(username==null) return null;
                if(username.isEmpty()) return null;
                if (db.findUserByName(username) != null)
                    IO.writeMessage(out, "Username already taken", MessageType.MSG);
                else break;
            }

            IO.writeMessage(out, "Insert your password:", MessageType.REQUEST);
            String pass = Wrapper.readWithTimeout(in, registerTimeout, ()->this.handleTimeOut(out));
            if(pass==null) return null;
            if(pass.isEmpty()) return null;
            User tmpUser = new User(username, pass, 0);
            boolean res = db.register(tmpUser);
            if (res) {
                db.save();
                IO.writeMessage(out, "Register done with success", MessageType.MSG);
                return tmpUser;
            } else {
                IO.writeMessage(out, "An error occur while registering", MessageType.MSG);
                return null;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Handles timeout scenarios during registration.
     *
     * @param out The PrintWriter to send the timeout message to the client.
     */
    public void handleTimeOut(PrintWriter out) {
        System.out.println("Time out");
        IO.writeMessage(out, "Register timed out. Please try again.", MessageType.MSG);
    }
}
