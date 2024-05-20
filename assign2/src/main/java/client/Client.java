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
        boolean sentMesage = false;

        System.out.println("Connecting to server");

        Socket socket = null;

        while (socket == null) {

            try {
                socket = new Socket(host, port);
            } catch (ConnectException e) {
                if (!sentMesage) {
                    System.out.println("Server is not currently up");
                    sentMesage = true;
                }
            }
        }

        System.out.println("Connected");
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            Message response = IO.readServerMsg(in);
            if (response.isType(MessageType.QUIT)) {
                break;
            }
            processMessage(response, out, stdIn, in);
        }

        out.close();
        in.close();
        stdIn.close();
        socket.close();
    }

    private void processMessage(Message response, PrintWriter out, BufferedReader stdIn, BufferedReader in) throws IOException {
        String serverMsg = response.getBody();

        switch (response.getType()) {
            case QUIT:
                break;
            case CMD:
                clearScreen();
                break;
            case MSG:
                System.out.println(serverMsg);
                break;
            case REQUEST:
                System.out.print(serverMsg);
                handleUserInput(out, stdIn, in);
                break;
            case PING:
                out.println("ping");
                break;
            default:
                break;
        }
    }

    private void handleUserInput(PrintWriter out, BufferedReader stdIn, BufferedReader in) throws IOException {
        String userInput = null;

        while (true) {
            userInput = null;

            if (stdIn.ready()) {
                userInput = stdIn.readLine();
            }

            if (in.ready() || userInput != null) {
                break;
            }
        }
        if (userInput != null) out.println(userInput);
    }
}
