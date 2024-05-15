package server.services;

import java.util.ArrayList;
import java.util.List;

import java.util.LinkedList;
import java.util.Queue;

public class SimpleQueue<T> implements Q<T> {

    private final Queue<T> queue = new LinkedList<>();

    @Override
    public void push(T element) {
        queue.add(element);
    }

    @Override
    public List<T> popMultiple(int n) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            T item = queue.poll();  // poll() returns null if the queue is empty
            if (item != null) {
                list.add(item);
            } else {
                break;  // Break the loop if there are no more items in the queue
            }
        }
        return list;
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean has(int n) {
        return queue.contains(n);
    }
}
