package server.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

import server.ClientHandler;

public class RankedQueue extends ConcurrentQueue<ClientHandler> {

    private final Queue<ClientHandler> queue = new LinkedList<>();

    public RankedQueue(int n) {
        super(n);
    }

    @Override
    public void push(ClientHandler element) {}

    @Override
    public List<ClientHandler> popMultiple(int n) {
        return new ArrayList<>();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean has(int n) {
        return false;
    }

    @Override
    public void forEach(Consumer<ClientHandler> action){
        
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public void remove(ClientHandler ch) {
        
    }
}
