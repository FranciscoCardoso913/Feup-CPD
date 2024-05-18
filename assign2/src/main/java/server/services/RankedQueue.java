package server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        if (!has(n)) return null;

        List<ClientHandler> players = new ArrayList<ClientHandler>();
        Map<Integer, Integer> guaranteedPlayerBins = new HashMap<Integer, Integer>();
        int guaranteedPlayers = 0, connectedPlayers;

        try {
            queueLock.lock();

            // TODO: Check correct bin for correct number of players
            Integer currBin = bins.get(currBinIdx);
            SimpleQueue currBinQueue = this.queue.get(bins.get(currBinIdx));

            // If 

            connectedPlayers = currBinQueue.getConnected();

            // Find number of players of correct queue that are connected
            // Don't delete from queue immediately if they aren't enough to start a game (to preserve order in case a game can't be started)
            if (connectedPlayers >= n) {
                return currBinQueue.popMultiple(n);
            } else {
                guaranteedPlayers += connectedPlayers;
                guaranteedPlayerBins.put(currBin, connectedPlayers);
            }

            // Look at bins that are next to the left and right until limit is surpassed. 
            do {
                
            } while (true);

            // TODO: Check an amount of bins left/right depending on time spent waiting
        } finally {
            queueLock.unlock();
        }
        
        //TODO: descomentar
        //return players;
    }

    @Override
    public boolean isEmpty() {
        for (SimpleQueue sq: queue.values()) {
            if (!sq.isEmpty())
                return false;
        }

        return true;
    }

    @Override
    public boolean has(int n) {
        int clients = 0;

        for (SimpleQueue sq: queue.values()) {
            clients += sq.getConnected();

            if (clients >= n)
                return true;
        }

        return false;
    }

    @Override
    public void forEach(Consumer<ClientHandler> action){
        // TODO: Check if this needs a lock
        for (SimpleQueue sq: queue.values()) {
            sq.forEach(action);
        }
    }

    @Override
    public int size() {
        int clients = 0;

        for (SimpleQueue sq: queue.values()) {
            clients += sq.size();
        }

        return clients;
    }

    @Override
    public boolean remove(ClientHandler ch) {
        for (SimpleQueue sq: queue.values()) {
            if (sq.remove(ch))
                return true;
        }

        return false;
    }

    @Override
    public void removeIf(Predicate<ClientHandler> condition) {

    }
}
