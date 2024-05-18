package utils;

import message.IO;
import message.MessageType;
import server.database.models.User;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class Wrapper {
    public static <T> T withTimeOut(Supplier<T> function, int Time, Runnable timeOutHandler) throws Exception {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Callable<T> callable = function::get; // Convert Supplier to Callable
        Future<T> future = scheduler.submit(callable);
        scheduler.schedule(timeOutHandler, Time, TimeUnit.SECONDS);

        try {
            T res= future.get(Time, TimeUnit.SECONDS); // Wait for result with timeout
            scheduler.shutdownNow();
            return  res;
        } catch (TimeoutException e) {
            timeOutHandler.run();
        } finally {
            System.out.println("Hello?");
            scheduler.shutdownNow();
        }
        return null;
    }

    public static void withLock(Runnable action, ReentrantLock actionLocker){
        actionLocker.lock();
        action.run();
        actionLocker.unlock();
    }

}
