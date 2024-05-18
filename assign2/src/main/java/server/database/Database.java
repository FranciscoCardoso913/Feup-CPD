package server.database;

import server.database.models.User;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class Database {
    private final String dbPath;
    private Set<User> users;
    private final ReentrantLock lock = new ReentrantLock();

    public Database(String dbPath){

        this.dbPath = dbPath;

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
                User user = new User(fields[0], fields[1], Integer.parseInt(fields[2]));
                this.users.add(user);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the CSV file.");
            e.printStackTrace();
        }
    }

    public boolean register(User user) {
        Integer size = this.users.size();
        lock.lock();
        try {
            System.out.println("Entrouuu registroooo");
            Thread.sleep(3000);
            System.out.println("Vai saiiiir registroooo");
            this.users.add(user);
        } catch (InterruptedException e) {
            System.out.println("Oh nao");
        }
        finally {
            lock.unlock();
        }
        return !size.equals(this.users.size());
    }

    public void save(){
        lock.lock();
        try (FileWriter writer = new FileWriter(this.dbPath)) {
            for(User user : this.users){
                writer.append(user.getName())
                        .append(",")
                        .append(user.getPassword())
                        .append(",")
                        .append(Integer.toString(user.getScore()))
                        .append("\n");
            }
            System.out.println("Data written to CSV successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the CSV file.");
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public User findUserByName(String name){
        return users.stream()
                .filter(u -> u.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}