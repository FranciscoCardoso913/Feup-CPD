package server.services;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;


import server.ClientHandler;

public class RankedQueue extends ConcurrentQueue<ClientHandler> {

    private final Map<Integer, SimpleQueue> queue = new HashMap<Integer, SimpleQueue>();
    private final int BIN_SIZE = 10;
    private final long BIN_TIME = 10000;
    private List<Integer> bins;
    private int currBinIdx;


    public RankedQueue(int n) {
        super(n);
        this.bins = new ArrayList<>();
        this.currBinIdx = -1;
    }

    @Override
    public void push(ClientHandler element) {
        Integer bin = element.getUser().getScore() / BIN_SIZE;

        try {
            // This operation needs a lock so that two different threads don't add a user from a new bin at the same time
            // which could result in the loss of a user in the queue
            queueLock.lock();
            if (!this.queue.containsKey(bin)) {
                this.queue.put(bin, new SimpleQueue(this.PLAYER_PER_GAME));

                if (this.currBinIdx < 0 || this.bins.get(this.currBinIdx) > bin)
                    this.currBinIdx++;

                this.bins.clear();
                this.bins.addAll(this.queue.keySet());
            }
        } finally {
            queueLock.unlock();
        }


        this.queue.get(bin).push(element);
    }

    @Override
    public List<ClientHandler> popMultiple(int n) {
        if (this.queue.isEmpty()) return null;

        List<ClientHandler> players = new ArrayList<>();
        Map<Integer, Integer> guaranteedPlayerBins = new HashMap<>();
        int guaranteedPlayers = 0, connectedPlayers;

        try {
            queueLock.lock();

            Integer currBin = bins.get(currBinIdx);
            SimpleQueue currBinQueue = this.queue.get(bins.get(currBinIdx));

            // If current queue is empty, skip
            if (currBinQueue.isEmpty()) return null;

            // Get number of connected players
            connectedPlayers = currBinQueue.getConnected();

            // Find number of players of correct queue that are connected
            // Don't delete from queue immediately if they aren't enough to start a game (to preserve order in case a game can't be started)
            if (connectedPlayers >= n) {
                return currBinQueue.popMultiple(n);
            } else {
                guaranteedPlayers += connectedPlayers;
                guaranteedPlayerBins.put(currBin, connectedPlayers);
            }

            // Get waiting time from player who waited the most from current queue
            // TODO: Maybe get shortest wait and not longest?
            long longestWait = currBinQueue.getHeadWaitTime();
            int binsExplored = 1;

            Integer leftBin = currBinIdx - binsExplored >= 0 ? this.bins.get(this.currBinIdx - binsExplored) : null;
            Integer rightBin = currBinIdx + binsExplored <= this.bins.size() - 1 ? this.bins.get(this.currBinIdx + binsExplored) : null;

            while ((leftBin != null && longestWait >= (currBin - leftBin) * BIN_TIME) || (rightBin != null && longestWait >= (rightBin - currBin) * BIN_TIME)) {
                if (leftBin != null) {
                    SimpleQueue leftBinQueue = this.queue.get(leftBin);
                    connectedPlayers = leftBinQueue.getConnected();

                    if (connectedPlayers + guaranteedPlayers >= n) {
                        guaranteedPlayerBins.forEach((k, v) -> {
                            players.addAll(this.queue.get(k).popMultiple(v));
                        });

                        players.addAll(leftBinQueue.popMultiple(n - guaranteedPlayers));

                        return players;
                    }

                    guaranteedPlayers += connectedPlayers;
                    guaranteedPlayerBins.put(leftBin, connectedPlayers);
                }

                if (rightBin != null) {
                    SimpleQueue rightBinQueue = this.queue.get(rightBin);
                    connectedPlayers = rightBinQueue.getConnected();

                    if (connectedPlayers + guaranteedPlayers >= n) {
                        guaranteedPlayerBins.forEach((k, v) -> {
                            players.addAll(this.queue.get(k).popMultiple(v));
                        });

                        players.addAll(rightBinQueue.popMultiple(n - guaranteedPlayers));

                        return players;
                    }

                    guaranteedPlayers += connectedPlayers;
                    guaranteedPlayerBins.put(rightBin, connectedPlayers);
                }

                binsExplored++;
                leftBin = currBinIdx - binsExplored >= 0 ? this.bins.get(this.currBinIdx - binsExplored) : null;
                rightBin = currBinIdx + binsExplored <= this.bins.size() - 1 ? this.bins.get(this.currBinIdx + binsExplored) : null;
            }
        } finally {
            currBinIdx = (currBinIdx + 1) % bins.size();
            queueLock.unlock();
        }

        return null;
    }

    @Override
    public boolean isEmpty() {
        for (SimpleQueue sq : queue.values()) {
            if (!sq.isEmpty())
                return false;
        }

        return true;
    }

    @Override
    public boolean has(int n) {
        int clients = 0;

        try {
            queueLock.lock();
            for (SimpleQueue sq : queue.values()) {
                clients += sq.getConnected();

                if (clients >= n)
                    return true;
            }
        } finally {
            queueLock.unlock();
        }


        return false;
    }

    @Override
    public void forEach(Consumer<ClientHandler> action) {
        // TODO: Check if this needs a lock
        for (SimpleQueue sq : queue.values()) {
            sq.forEach(action);
        }
    }

    @Override
    public int size() {
        int clients = 0;

        for (SimpleQueue sq : queue.values()) {
            clients += sq.size();
        }

        return clients;
    }

    @Override
    public boolean remove(ClientHandler ch) {
        for (SimpleQueue sq : queue.values()) {
            if (sq.remove(ch))
                return true;
        }

        return false;
    }

    @Override
    public void removeIf(Predicate<ClientHandler> condition) {
        try {
            queueLock.lock();

            for (SimpleQueue sq : queue.values()) {
                sq.removeIf(condition);
            }
        } finally {
            queueLock.unlock();
        }
    }
}
