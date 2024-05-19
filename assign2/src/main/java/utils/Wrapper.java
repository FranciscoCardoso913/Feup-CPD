package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
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
            T res =future.get(Time, TimeUnit.SECONDS);
            scheduler.shutdownNow();
            return  res;
        } catch (TimeoutException e) {
            future.cancel(true);// Wait for result with timeout
            scheduler.shutdownNow();
            timeOutHandler.run();
            throw e;
        }
    }
    public static String readWithTimeout(BufferedReader in, int Time, Supplier<String> timeOutHandler ) throws IOException {
        String userInput;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < Time){
            if(in.ready()){
                userInput = in.readLine();
                return userInput;
            }
        }
        userInput=timeOutHandler.get();
        return userInput;
    }
    public static void withLock(Runnable action, ReentrantLock actionLocker){
        actionLocker.lock();
        action.run();
        actionLocker.unlock();
    }

}
