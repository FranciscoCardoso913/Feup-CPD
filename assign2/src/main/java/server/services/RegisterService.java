package server.services;

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
            out.println("Insert your username:\0");
            username = in.readLine();
            if(db.findUserByName(username) != null) out.println("Username already taken");  //TODO: \0 ?
            else break;
        }

        out.println("Insert your password:\0");
        String pass = in.readLine();
        User tmpUser = new User(username, pass,0);
        boolean res = db.register(tmpUser);
        if(res) db.save();

        user.setName(tmpUser.getName());
        user.setPassword(tmpUser.getPassword());
        user.setScore(tmpUser.getScore());
        return "Register done with success\0";
    }
}
