package client;

import message.IO;
import message.Message;
import message.MessageType;

import java.io.*;
import java.net.*;


public class Client {

    private String host;
    private int port;
    private Socket socket;

    public Client (String host, int port){
        this.host = host;
        this.port = port;
    }

    public void main() throws IOException {
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
            }
            else if (response.isType(MessageType.MSG)) {
                System.out.println(serverMsg);
            }
            else if (response.isType(MessageType.REQUEST)){
                System.out.print(serverMsg);
                userInput = stdIn.readLine();
                out.println(userInput);
            } 
            else if (response.isType(MessageType.PING)) {
                out.println("ping");
            } 
        }
        System.out.println("Closing");
        out.close();
        in.close();
        stdIn.close();
        socket.close();
    }
}
