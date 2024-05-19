package client;

import message.IO;
import message.Message;
import message.MessageType;

import java.io.*;
import java.net.*;


public class Client {

    private String host;
    private int port;

    /**
     * Constructs a Client object with the specified host and port.
     *
     * @param host The host to connect to.
     * @param port The port to connect to.
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Clears the screen.
     */
    public void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Main method to run the client.
     *
     * @throws Exception if an error occurs during execution.
     */
    public void main() throws Exception {
        System.out.println("Connecting to server");
        Socket socket = new Socket(host, port);
        System.out.println("Connected");
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        String serverMsg;
        while (true) {
            Message response = IO.readServerMsg(in);
            serverMsg = response.getBody();
            if (response.isType(MessageType.QUIT)) {
                break;
            } else if (response.isType(MessageType.CMD)) {
                clearScreen();
            } else if (response.isType(MessageType.MSG)) {
                System.out.println(serverMsg);
            } else if (response.isType(MessageType.REQUEST)) {
                System.out.print(serverMsg);
                while (true) {
                    userInput = null;
                    if (stdIn.ready()) userInput = stdIn.readLine();
                    if (userInput != null || in.ready())
                        break;
                }
                if (userInput != null) out.println(userInput);
            } else if (response.isType(MessageType.PING)) {
                out.println("ping");
            }
        }
        out.close();
        in.close();
        stdIn.close();
        socket.close();
    }
}
