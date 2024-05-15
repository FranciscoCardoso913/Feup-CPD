package server.services;

import java.util.List;

public interface Q<T> {

    void push(T el);

    List<T> popMultiple(int n);

    boolean isEmpty();

    boolean has(int n);
}
