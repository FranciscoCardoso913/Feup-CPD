package client;

import server.Server;

import java.io.*;
import java.net.*;


public class Client {

    public String readServerMsg(BufferedReader in) throws IOException {
        String serverMsg ="";
        String serverOut;
        while (true) {
            serverOut = in.readLine();
            if(serverOut.equals("\0")) break;
            serverMsg += '\n' + serverOut;
        }
        return serverMsg;
    }
    public void main() throws IOException {
        System.out.println("Connecting to sever");
        Socket socket = new Socket("localhost", 1234);
        System.out.println("Connected");
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        String userInput;
        String serverMsg;
        while (true) {
            serverMsg = readServerMsg(in);
            System.out.println(serverMsg);
            userInput = stdIn.readLine();
            out.println(userInput);
            if( userInput.equals("quit")) break;
        }
        out.close();
        in.close();
        stdIn.close();
        socket.close();
    }
}

