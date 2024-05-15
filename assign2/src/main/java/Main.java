import client.Client;
import server.Server;

import java.io.IOException;
// import org.json.simple.*;

public class Main {

    static String usage =
            """
            Usage: java Main [-s port mode] | [-c host port]
               mode:                                       \s
                   0 - Simple                              \s
                   1 - Ranking                             \s
            """;

    public static void main(String[] args) throws IOException {
        if (args.length != 3 || args[0].equals("--help") || args[0].equals("-h")) {
            System.out.println(usage);
            return;
        }

        switch (args[0]) {
            case "-s":
                startServer(args);
                break;
            case "-c":
                startClient(args);
                break;
            default:
                System.out.println(usage);
                break;
        }
    }

    private static void startServer(String[] args) throws IOException {
        try {
            int port = Integer.parseInt(args[1]);
            int mode = Integer.parseInt(args[2]);
            if (mode != 0 && mode != 1) {
                System.out.println(usage);
                return;
            }
            new Server(port, mode).main();
        } catch (NumberFormatException e) {
            System.out.println(usage);
        }
    }

    private static void startClient(String[] args) throws IOException {
        try {
            String host = args[1];
            int port = Integer.parseInt(args[2]);
            new Client(host, port).main();
        } catch (NumberFormatException e) {
            System.out.println(usage);
        }
    }
}
