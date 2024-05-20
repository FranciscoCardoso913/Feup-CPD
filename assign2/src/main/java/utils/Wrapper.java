package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class Wrapper {

    private static final long MILLIS_IN_SECOND = 1000L;

    /**
     * Reads a line from the provided BufferedReader with a timeout.
     * If the read operation does not complete within the specified time,
     * it calls the provided timeOutHandler and returns its result.
     *
     * @param in             The BufferedReader to read from.
     * @param time           The timeout period in seconds.
     * @param timeOutHandler A Supplier that provides a default value when a timeout occurs.
     * @return The line read from the BufferedReader, or the result from timeOutHandler if timed out.
     * @throws IOException If an I/O error occurs.
     */
    public static String readWithTimeout(BufferedReader in, int time, Supplier<String> timeOutHandler) throws IOException {
        if (waitForReady(in, time)) {
            return in.readLine();
        }
        return timeOutHandler.get();
    }

    /**
     * Reads a line from the provided BufferedReader with a timeout.
     * If the read operation does not complete within the specified time,
     * it calls the provided timeOutHandler Runnable.
     *
     * @param in             The BufferedReader to read from.
     * @param time           The timeout period in seconds.
     * @param timeOutHandler A Runnable that handles timeout events.
     * @return The line read from the BufferedReader, or null if timed out.
     * @throws IOException If an I/O error occurs.
     */
    public static String readWithTimeout(BufferedReader in, int time, Runnable timeOutHandler) throws IOException {
        if (waitForReady(in, time)) {
            return in.readLine();
        }
        timeOutHandler.run();
        return null;
    }

    /**
     * Waits until the BufferedReader is ready to be read or the timeout expires.
     *
     * @param in   The BufferedReader to monitor.
     * @param time The timeout in seconds.
     * @return true if the BufferedReader is ready, false if timed out.
     */
    private static boolean waitForReady(BufferedReader in, int time) throws IOException {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < time * MILLIS_IN_SECOND) {
            if (in.ready()) {
                return true;
            }
        }
        return false;
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
