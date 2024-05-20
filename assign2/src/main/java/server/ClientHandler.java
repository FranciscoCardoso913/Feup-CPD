package server;

import config.ConfigLoader;
import server.database.Database;
import server.database.models.User;
import server.services.AuthService;
import server.services.RegisterService;
import message.*;
import utils.Wrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private final RegisterService registerService;
    private final AuthService authService;
    private User user = null;
    private long sessionStartTime;

    private long lastSeen;
    private String reconnectionMSG;
    public volatile PrintWriter out;
    public volatile BufferedReader in;
    int INACTIVE_TIMEOUT;

    /**
     * Constructor for ClientHandler.
     *
     * @param clientSocket The socket connection to the client.
     * @param db           The database instance.
     * @param configLoader The configuration loader.
     * @throws IOException If an I/O error occurs.
     */
    public ClientHandler(Socket clientSocket, Database db, ConfigLoader configLoader) throws IOException {
        this.clientSocket = clientSocket;
        this.INACTIVE_TIMEOUT = Integer.parseInt(configLoader.get("INACTIVE_TIMEOUT"));
        this.registerService = new RegisterService(db, configLoader);
        this.authService = new AuthService(db, configLoader);
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.sessionStartTime = System.currentTimeMillis();
        this.lastSeen = System.currentTimeMillis();
        this.reconnectionMSG = "Reconnected, waiting in queue";
    }

    /**
     * Closes the client connection.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void close() throws IOException {
        out.close();
        in.close();
        clientSocket.close();
    }

    public long getSessionStartTime() {
        return sessionStartTime;
    }

    public void updateSessionStartTime() {
        sessionStartTime = System.currentTimeMillis();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public long getLastSeen() {
        return this.lastSeen;
    }

    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis();
    }

    /**
     * Sets a new socket connection for the client and updates the input/output streams.
     *
     * @param clientSocket The new socket connection.
     * @throws IOException If an I/O error occurs.
     */
    public void setSocket(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;

        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        IO.writeMessage(out, this.reconnectionMSG, MessageType.MSG);
    }

    public Socket getSocket() {
        return clientSocket;
    }

    /**
     * Reads a message from the client.
     *
     * @return The message read from the client.
     */
    public String readMessage() {
        String inputLine;
        try {
            while ((inputLine = in.readLine()) == null) {
            }

            System.out.println("Client: " + inputLine);
            return inputLine;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * The main method of the ClientHandler thread.
     * Handles client login, registration, and disconnection.
     */
    public void run() {
        try {
            boolean result = false;
            while (!result) {
                IO.writeMessage(out, "[1]Login\n[2]Register\n[3]Quit\nChoose an option:", MessageType.REQUEST);
                String inputLine = Wrapper.readWithTimeout(in, INACTIVE_TIMEOUT, this::timeoutHandler);
                result = switch (inputLine) {
                    case "1" -> authService();
                    case "2" -> registerService();
                    case "3" -> quit();
                    default -> invalidOption();
                };
            }
        } catch (Exception e) {
            try {
                this.quit();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }

    /**
     * Handles user authentication.
     *
     * @return true if authentication is successful, false otherwise.
     * @throws Exception If an error occurs during authentication.
     */
    public boolean authService() throws Exception {
        User res = this.authService.authUser(out, in);
        this.setUser(res);
        return res != null;
    }

    /**
     * Handles user registration.
     *
     * @return true if registration is successful, false otherwise.
     * @throws Exception If an error occurs during registration.
     */
    public boolean registerService() throws Exception {
        User res = this.registerService.registerUser(out, in);
        this.setUser(res);
        return res != null;
    }

    /**
     * Handles client disconnection.
     *
     * @return true if the client disconnects successfully.
     * @throws IOException If an I/O error occurs.
     */
    public boolean quit() throws IOException {
        IO.writeMessage(this.out, "QUIT", MessageType.QUIT);
        this.close();
        return true;
    }

    /**
     * Handles invalid menu options selected by the client.
     *
     * @return false indicating an invalid option was selected.
     */
    public boolean invalidOption() {
        IO.writeMessage(this.out, "Invalid Option", MessageType.MSG);
        return false;
    }

    /**
     * Checks the connection to the client.
     *
     * @return true if the client is still connected, false otherwise.
     */
    public boolean checkConnection() {
        IO.writeMessage(this.out, "PING", MessageType.PING);
        String res;

        try {
            res = this.in.readLine();

            if (res != null) {
                updateLastSeen();
                return true;
            }
        } catch (IOException e) {
            System.out.println("Client not connected");
            return false;
        }

        return false;
    }

    public void setReconnectionMSG(String msg) {
        this.reconnectionMSG = msg;
    }

    /**
     * Handles client timeout by disconnecting them.
     *
     * @return "3" to indicate disconnection.
     */
    public String timeoutHandler() {
        System.out.println("Client Disconnected");
        IO.writeMessage(out, "Time Out!\nDisconnecting", MessageType.MSG);
        return "3";
    }
}
