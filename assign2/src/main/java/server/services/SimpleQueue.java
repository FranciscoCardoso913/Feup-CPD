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

    /**
     * Constructor for SimpleQueue.
     *
     * @param n The initial capacity of the queue.
     */
    public SimpleQueue(int n) {
        super(n);
    }

    /**
     * Pushes a new client handler into the queue.
     * If a client with the same username exists, updates the socket instead.
     *
     * @param element The client handler to be added.
     */
    @Override
    public void push(ClientHandler element) {
        String username = element.getUser().getName();
        try {
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

    /**
     * Pops multiple client handlers from the queue.
     *
     * @param n The number of client handlers to pop.
     * @return A list of client handlers if there are enough connected clients, otherwise null.
     */
    @Override
    public List<ClientHandler> popMultiple(int n) {
        if (!this.has(n)) return null;
        List<ClientHandler> list = new ArrayList<>();

        try {
            this.queueLock.lock();
            int i = 0;
            for (ClientHandler ch : this.queue) {
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

    /**
     * Checks if the queue is empty.
     *
     * @return true if the queue is empty, false otherwise.
     */
    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Checks if there are at least 'n' connected client handlers in the queue.
     *
     * @param n The number of connected client handlers to check for.
     * @return true if there are at least 'n' connected client handlers, false otherwise.
     */
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
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Applies an action to each client handler in the queue.
     *
     * @param action The action to be applied.
     */
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

    /**
     * Gets the size of the queue.
     *
     * @return The number of client handlers in the queue.
     */
    @Override
    public int size() {
        return queue.size();
    }

    /**
     * Removes a specific client handler from the queue.
     *
     * @param ch The client handler to be removed.
     * @return true if the client handler was removed, false otherwise.
     */
    @Override
    public boolean remove(ClientHandler ch) {
        return this.queue.remove(ch);
    }

    /**
     * Gets the number of connected client handlers in the queue.
     *
     * @return The number of connected client handlers.
     */
    public int getConnected() {
        int connected = 0;

        try {
            this.queueLock.lock();
            for (ClientHandler ch : this.queue) {
                if (ch.checkConnection())
                    connected++;
            }
        } finally {
            this.queueLock.unlock();
        }


        return connected;
    }

    /**
     * Removes client handlers from the queue based on a condition.
     *
     * @param condition The condition to be tested.
     */
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

    /**
     * Gets the wait time of the client handler at the head of the queue.
     *
     * @return The wait time in milliseconds.
     */
    public long getHeadWaitTime() {
        return System.currentTimeMillis() - this.queue.peek().getSessionStartTime();
    }
}
