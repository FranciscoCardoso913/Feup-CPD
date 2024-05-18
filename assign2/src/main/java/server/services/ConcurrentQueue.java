package server.services;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import server.ClientHandler;


public abstract class ConcurrentQueue<T> {
    protected final ReentrantLock queueLock = new ReentrantLock();
    protected int PLAYER_PER_GAME = 0;

    ConcurrentQueue(int n) {
        this.PLAYER_PER_GAME = n;
    }

    public abstract void push(T el);

    public abstract List<T> popMultiple(int n);

    public abstract boolean isEmpty();

    public abstract boolean has(int n);

    public abstract void forEach(Consumer<ClientHandler> action);

    public abstract int size();

    public abstract boolean remove(ClientHandler ch);

    public abstract void removeIf(Predicate<ClientHandler> condition);

}
