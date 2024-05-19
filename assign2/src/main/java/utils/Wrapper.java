package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class Wrapper {

    public static String readWithTimeout(BufferedReader in, int Time, Supplier<String> timeOutHandler ) throws IOException {
        String userInput;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < Time*1000L){
            if(in.ready()){
                userInput = in.readLine();
                return userInput;
            }
        }
        userInput=timeOutHandler.get();
        return userInput;
    }
    public static String readWithTimeout(BufferedReader in, int Time, Runnable timeOutHandler ) throws IOException {
        String userInput;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < Time* 1000L){
            if(in.ready()){
                userInput = in.readLine();
                return userInput;
            }
        }
        timeOutHandler.run();
        return null;
    }
    public static void withLock(Runnable action, ReentrantLock actionLocker){
        try {
            actionLocker.lock();
            action.run();
        }finally {
            actionLocker.unlock();
        }
    }

}
