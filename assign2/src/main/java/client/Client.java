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
            boolean end = false;
            if(serverOut!=null ){
                char[] charArray = serverOut.toCharArray();
                for (char c : charArray) {
                    if (c == '\0') {
                        end= true;
                        break;
                    }
                }
            }
            serverMsg += '\n' + serverOut;
            if(end) break;

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
            if(userInput!=null && userInput.equals("quit")) break;
            out.println(userInput);

        }
        System.out.println("Closing");
        out.close();
        in.close();
        stdIn.close();
        socket.close();
    }
}

