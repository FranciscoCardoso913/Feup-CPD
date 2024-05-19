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

    public RegisterService(Database db, ConfigLoader configLoader ){
        this.db = db;
        this.registerTimeout= Integer.parseInt( configLoader.get("AUTH_TIMEOUT"));
    }

    public User registerUser (PrintWriter out, BufferedReader in){
        String username = "";
        try {
            while (true) {
                IO.writeMessage(out, "Insert your username:", MessageType.REQUEST);
                username =  Wrapper.readWithTimeout(in, registerTimeout, ()->this.handleTimeOut(out));
                if(username==null) return null;
                if (db.findUserByName(username) != null)
                    IO.writeMessage(out, "Username already taken", MessageType.MSG);
                else break;
            }

            IO.writeMessage(out, "Insert your password:", MessageType.REQUEST);
            String pass = Wrapper.readWithTimeout(in, registerTimeout, ()->this.handleTimeOut(out));
            if(pass==null) return null;
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
    public void handleTimeOut(PrintWriter out) {
        System.out.println("Time out");
        IO.writeMessage(out, "Register timed out. Please try again.", MessageType.MSG);
    }
}
