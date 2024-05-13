package server.services;

import server.database.Database;
import server.database.models.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class AuthService {
    private Database db;

    public AuthService(Database db){
        this.db = db;
    }

    public User authUser(PrintWriter out, BufferedReader in) throws IOException {
        String username = "";
        User user = null;
        while (true) {
            out.println("Insert your username:");
            username = in.readLine();
            user = db.findUserByName(username);
            if (user == null) out.println("Username doesn't exist");
            else break;
            //TODO:password.
        }
        return user;
    }
    
}
