package message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class IO {

    public static void writeMessage(PrintWriter out, String msg, MessageType type) {
        out.println( type.getType() + "\n" + msg + "\0");
    }


    public static Message readServerMsg(BufferedReader in) throws IOException {
        String serverMsg = "";
        MessageType header = null;
        String serverOut;
        while (true) {
            serverOut = in.readLine();
            if (header == null) header = MessageType.fromString(serverOut);
            else {
                boolean end = false;
                if (serverOut != null) {
                    char[] charArray = serverOut.toCharArray();
                    for (char c : charArray) {
                        if (c == '\0') {
                            end = true;
                            break;
                        }
                    }
                }
                serverMsg += '\n' + serverOut;
                if (end) break;

            }
        }
        return new Message(header, serverMsg);
    }
}

