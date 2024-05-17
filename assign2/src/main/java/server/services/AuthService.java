package server.services;

import message.IO;
import message.MessageType;
import server.database.Database;
import server.database.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthService {
    private final Database db;

    public AuthService(Database db){
        this.db = db;
    }

    public String authUser(PrintWriter out, BufferedReader in, User user) throws IOException {
        System.out.println("Authenticating");
        String username;
        String password;

        IO.writeMessage(out, "Insert your username:", MessageType.REQUEST);
        username = in.readLine();
        IO.writeMessage(out, "Insert your password:", MessageType.REQUEST);
        password = in.readLine();

        User tmpUser = db.findUserByName(username);

        if (tmpUser == null) {
            return "Username doesn't exist";
        }

        if (!tmpUser.login(password)) {
            return "Login failed: incorrect username or password";
        }
        user.setName(tmpUser.getName());
        user.setPassword(tmpUser.getPassword());
        user.setScore(tmpUser.getScore());
        return "Login successful!\n";
    }
}
