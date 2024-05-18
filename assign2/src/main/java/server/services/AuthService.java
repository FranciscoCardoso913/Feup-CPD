package server.services;

import message.IO;
import message.MessageType;
import server.ClientHandler;
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

    public String authUser(PrintWriter out, BufferedReader in, ClientHandler ch) throws IOException {
        System.out.println("Authenticating");
        String username;
        String password;

        while (true) {
            IO.writeMessage(out, "Insert your username:", MessageType.REQUEST);
            username = in.readLine();
            IO.writeMessage(out, "Insert your password:", MessageType.REQUEST);
            password = in.readLine();

            User tmpUser = db.findUserByName(username);

            if (tmpUser == null) {
                IO.writeMessage(out, "Username doesn't exist", MessageType.MSG);
                continue;
                // return "Username doesn't exist";
            }

            if (!tmpUser.login(password)) {
                IO.writeMessage(out, "Incorrect username or password", MessageType.MSG);
                continue;
                //return "Login failed: incorrect username or password";
            }

            ch.setUser(db.findUserByName(username));

            return "Login successful!\n";
        }
    }
}
