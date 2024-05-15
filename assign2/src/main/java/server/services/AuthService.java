package server.services;

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

    public User authUser(PrintWriter out, BufferedReader in) throws IOException {
        String username;
        String password;

        out.println("Insert your username:");   // TODO: class 'InputProvider'
        username = in.readLine();
        out.println("Insert your password:");
        password = in.readLine();

        User user = db.findUserByName(username);

        if (user == null) {
            out.println("Username doesn't exist");
            return null;
        }

        if (!user.login(password)) {
            out.println("Login failed: incorrect username or password");
            return null;
        }
        return user;
    }
}
