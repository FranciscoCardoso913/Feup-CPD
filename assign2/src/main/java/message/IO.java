package message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Utility class for input/output operations related to messaging.
 */
public class IO {

    private static final char MESSAGE_DELIMITER = '\0';
    private static final String LINE_SEPARATOR = "\n";

    /**
     * Writes a message to the PrintWriter.
     *
     * @param out  The PrintWriter object to write the message to.
     * @param msg  The message content.
     * @param type The type of the message (MessageType enum).
     */
    public static void writeMessage(PrintWriter out, String msg, MessageType type) {
        out.println(type.getType() + LINE_SEPARATOR + msg + MESSAGE_DELIMITER);
    }

    /**
     * Reads a message from the BufferedReader.
     *
     * @param in The BufferedReader object to read the message from.
     * @return A Message object representing the read message.
     * @throws IOException If an I/O error occurs.
     */
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
                        if (c == MESSAGE_DELIMITER) {
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
