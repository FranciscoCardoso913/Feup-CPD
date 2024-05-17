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

    public String registerUser (PrintWriter out, BufferedReader in, User user) throws IOException {
        String username = "";

        while (true) {
            IO.writeMessage(out, "Insert your username:", MessageType.REQUEST);
            username = in.readLine();
            if(db.findUserByName(username) != null) IO.writeMessage(out, "Username already taken", MessageType.MSG);
            else break;
        }

        IO.writeMessage(out, "Insert your password:", MessageType.REQUEST);
        String pass = in.readLine();
        User tmpUser = new User(username, pass,0);
        boolean res = db.register(tmpUser);
        if(res) db.save();

        user.setName(tmpUser.getName());
        user.setPassword(tmpUser.getPassword());
        user.setScore(tmpUser.getScore());
        return "Register done with success";
    }
}
