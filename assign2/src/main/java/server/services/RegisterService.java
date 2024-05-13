package server.services;

import server.database.Database;
import server.database.models.User;

import java.util.LinkedList;
import java.util.Queue;

public class RegisterService {
    private Queue<User> requests;
    private Database db;
    private boolean process;
    public RegisterService(Database db){
        requests = new LinkedList<>();
        this.db = db;
        this.process = true;
        Thread thread = new Thread(() -> {
            processRequests();
        });

        // Start the thread
        thread.start();
    }
    public void addRequest(User user){
        requests.add(user);
    }
    public void finishProcess(){
        this.process = false;
    }
    public void processRequests(){
        while (this.process){
            if (!this.requests.isEmpty()){
                db.register(requests.poll());
            }
        }
    }
}
