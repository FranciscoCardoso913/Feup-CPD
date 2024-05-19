package server.services;

import message.IO;
import message.MessageType;
import server.database.Database;
import server.database.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class RegisterService {
    private Database db;

    public RegisterService(Database db){
        this.db = db;
    }

    public User registerUser (PrintWriter out, BufferedReader in){
        String username = "";
        try {
            while (true) {
                IO.writeMessage(out, "Insert your username:", MessageType.REQUEST);
                username = in.readLine();
                if (db.findUserByName(username) != null)
                    IO.writeMessage(out, "Username already taken", MessageType.MSG);
                else break;
            }

            IO.writeMessage(out, "Insert your password:", MessageType.REQUEST);
            String pass = in.readLine();
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
