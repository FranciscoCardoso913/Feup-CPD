package server.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.LinkedList;
import java.util.Queue;
import server.ClientHandler;

public class SimpleQueue extends ConcurrentQueue<ClientHandler> {

    private final Queue<ClientHandler> queue = new LinkedList<>();

    public SimpleQueue(int n) {
        super(n);
    }

    @Override
    public void push(ClientHandler element) {
        String username = element.getUser().getName();
        try{
            this.queueLock.lock();
            Optional<ClientHandler> existingElement = queue.stream()
                    .filter(e -> e.getUser().getName().equals(username))
                    .findFirst();

            if (existingElement.isPresent()) {
                existingElement.get().setSocket(element.getSocket());
            } else {
                queue.add(element);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            this.queueLock.unlock();
        }
    }

    @Override
    public List<ClientHandler> popMultiple(int n) {
        // TODO: Check this
        if (!this.has(this.numberPlayers)) return null;
        List<ClientHandler> list = new ArrayList<>();

        try {
            this.queueLock.lock();

            for (int i = 0; i < n; i++) {
                ClientHandler item = queue.poll();

                if (item == null) return null;

                list.add(item);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            this.queueLock.unlock();
        }

        return list;
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean has(int n) {
        return queue.size() > n;
    }
}
