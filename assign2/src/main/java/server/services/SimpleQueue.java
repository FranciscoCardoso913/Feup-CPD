package server.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
        if (!this.has(n)) return null;
        List<ClientHandler> list = new ArrayList<>();

        try {
            this.queueLock.lock();
            int i = 0;
            for (ClientHandler ch: this.queue) {
                if (ch.checkConnection()) {
                    list.add(ch);
                    i++;
                }

                if (i >= n) break;
            }
            this.queue.removeAll(list);

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
        try {
            int connected = 0;
            this.queueLock.lock();
            for (ClientHandler ch : this.queue) {
                if (ch.checkConnection()) {
                    connected++;
                }
                if (connected >= n)
                    break;
            }
            this.queueLock.unlock();

            return connected >= n;
        }catch (Exception e){
            throw e;
        }
    }

    @Override
    public void forEach(Consumer<ClientHandler> action) {
        try {
            queueLock.lock();
            queue.forEach(action);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            queueLock.unlock();
        }
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean remove(ClientHandler ch) {
        return this.queue.remove(ch);
    }

    public int getConnected() {
        int connected = 0;

        try {
            this.queueLock.lock();
            for (ClientHandler ch: this.queue) {
                if (ch.checkConnection())
                    connected++;
            }
        } finally {
            this.queueLock.unlock();
        }


        return connected;
    }

    @Override
    public void removeIf(Predicate<ClientHandler> condition) {
        try {
            queueLock.lock();
            
            Iterator<ClientHandler> iterator = queue.iterator();
            while (iterator.hasNext()) {
                ClientHandler clientHandler = iterator.next();
                if (condition.test(clientHandler)) {
                    iterator.remove();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            queueLock.unlock();
        }
    }
    
    public long getHeadWaitTime() {
        return System.currentTimeMillis() - this.queue.peek().getSessionStartTime();
    }
}
