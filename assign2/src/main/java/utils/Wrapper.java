package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class Wrapper {
    /**
     * Reads a line from the provided BufferedReader with a timeout.
     * If the read operation does not complete within the specified time,
     * it calls the provided timeOutHandler and returns its result.
     *
     * @param in             The BufferedReader to read from.
     * @param Time           The timeout period in seconds.
     * @param timeOutHandler A Supplier that provides a default value when a timeout occurs.
     * @return The line read from the BufferedReader, or the result from timeOutHandler if timed out.
     * @throws IOException If an I/O error occurs.
     */
    public static String readWithTimeout(BufferedReader in, int Time, Supplier<String> timeOutHandler) throws IOException {
        String userInput;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < Time * 1000L) {
            if (in.ready()) {
                userInput = in.readLine();
                return userInput;
            }
        }
        userInput = timeOutHandler.get();
        return userInput;
    }

    /**
     * Reads a line from the provided BufferedReader with a timeout.
     * If the read operation does not complete within the specified time,
     * it calls the provided timeOutHandler Runnable.
     *
     * @param in             The BufferedReader to read from.
     * @param Time           The timeout period in seconds.
     * @param timeOutHandler A Runnable that handles timeout events.
     * @return The line read from the BufferedReader, or null if timed out.
     * @throws IOException If an I/O error occurs.
     */
    public static String readWithTimeout(BufferedReader in, int Time, Runnable timeOutHandler) throws IOException {
        String userInput;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < Time * 1000L) {
            if (in.ready()) {
                userInput = in.readLine();
                return userInput;
            }
        }
        timeOutHandler.run();
        return null;
    }

    /**
     * Executes a given action while holding a lock.
     * Ensures that the lock is always released after the action is performed.
     *
     * @param action       The action to perform while holding the lock.
     * @param actionLocker The lock to hold during the action.
     */
    public static void withLock(Runnable action, ReentrantLock actionLocker) {
        try {
            actionLocker.lock();
            action.run();
        } finally {
            actionLocker.unlock();
        }
    }

}
