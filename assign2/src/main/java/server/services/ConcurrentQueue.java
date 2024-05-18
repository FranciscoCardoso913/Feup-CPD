package server.services;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import server.ClientHandler;

public abstract class ConcurrentQueue<T> {

    protected final ReentrantLock queueLock = new ReentrantLock();
    protected int numberPlayers = 0;

    ConcurrentQueue(int n) {
        this.numberPlayers = n;
    }

    public abstract void push(T el);

    public abstract List<T> popMultiple(int n);

    public abstract boolean isEmpty();

    public abstract boolean has(int n);

    public abstract void forEach(Consumer<ClientHandler> action);

    public abstract int size();
}
