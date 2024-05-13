package server.database;

import server.database.models.User;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Database {
    private final String dbPath;
    private Set<User> users;
    public Database(){

        this.dbPath = "src/main/java/server/database/db.csv";

        this.users = new HashSet<User>();

        File file = new File(this.dbPath);

        if(file.exists()){
            this.loadUsers();
        }
        else{
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("An error occurred while creating the db file.");
                e.printStackTrace();
            }
        }
    }

    public void loadUsers(){
        try (BufferedReader reader = new BufferedReader(new FileReader(this.dbPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                User user = new User(fields[0], fields[1]);
                this.users.add(user);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the CSV file.");
            e.printStackTrace();
        }
    }

    public boolean register(User user) {
        Integer size = this.users.size();
        this.users.add(user);
        return !size.equals(this.users.size());
    }

    public void save(){
        try (FileWriter writer = new FileWriter(this.dbPath)) {
            for(User user : this.users){
                writer.append(user.getName());
                writer.append(",");
                writer.append(user.getPassword());
                writer.append("\n");
            }
            System.out.println("Data written to CSV successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the CSV file.");
            e.printStackTrace();
        }

    }
}