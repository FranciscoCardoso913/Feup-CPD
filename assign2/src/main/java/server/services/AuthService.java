package server.services;

import server.database.Database;
import server.database.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthService {
    private final Database db;
    private static final int MAX_ATTEMPTS = 3;

    public AuthService(Database db){
        this.db = db;
    }

    public User authUser(PrintWriter out, BufferedReader in) throws IOException {
        String username;
        String password;
        int attempts = 0;

        while (attempts < MAX_ATTEMPTS) {
            attempts++;

            out.println("Insert your username:");   // TODO: class 'InputProvider'
            username = in.readLine();
            out.println("Insert your password:");
            password = in.readLine();

            User user = db.findUserByName(username);
            if (user == null) {
                out.println("Username doesn't exist");
                continue;
            }

            if (!user.login(password)) {
                out.println("Login failed: incorrect username or password");
                continue;
            }
            return user;
        }
        return null;
    }
}
