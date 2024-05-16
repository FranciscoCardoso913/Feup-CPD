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

    public String authUser(PrintWriter out, BufferedReader in, User user) throws IOException {
        System.out.println("Authenticating");
        String username;
        String password;

        out.println("Insert your username:\0");
        username = in.readLine();
        out.println("Insert your password:\0");
        password = in.readLine();

        User tmpUser = db.findUserByName(username);

        if (tmpUser == null) {
            out.println();
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
