import java.io.IOException;

import client.Client;
import config.ConfigLoader;
import server.Server;
// import org.json.simple.*;

public class Main {

    static String usage = """
            Usage: java Main [-s [mode]] | [-c]
               mode:                                       \s
                   0 - Simple                              \s
                   1 - Ranking                             \s
            """;

    /**
     * Main method to start the server or client based on the command line arguments.
     *
     * @param args Command line arguments.
     * @throws IOException if an I/O error occurs.
     */
    public static void main(String[] args) throws IOException {
        if (
                args.length != 1 && !(args.length == 2 && args[0].equals("-s")) ||
                        args.length == 1 && (args[0].equals("--help") || args[0].equals("-h"))
        ) {
            System.out.println(usage);
            return;
        }

        ConfigLoader config = new ConfigLoader();

        switch (args[0]) {
            case "-s":
                if (args.length == 2 && (args[1].equals("1") || args[1].equals("2")))
                    startServer(config, args[1]);
                else
                    startServer(config, null);
                break;
            case "-c":
                startClient(config);
                break;
            default:
                System.out.println(usage);
                break;
        }
    }

    /**
     * Starts the server based on the configuration.
     *
     * @param config   The configuration loader object.
     * @param arg_mode The mode of the server.
     * @throws IOException if an I/O error occurs.
     */
    private static void startServer(ConfigLoader config, String arg_mode) throws IOException {
        try {
            String mode = arg_mode != null ? arg_mode : config.get("MODE");
            int m = Integer.parseInt(mode);
            new Server(config, m).main();
        } catch (NumberFormatException | InterruptedException e) {
            e.printStackTrace();
            System.out.println(usage);
        }
    }

    /**
     * Starts the client based on the configuration.
     *
     * @param config The configuration loader object.
     * @throws IOException if an I/O error occurs.
     */
    private static void startClient(ConfigLoader config) throws IOException {
        try {
            String host = config.get("HOSTNAME");
            int port = Integer.parseInt(config.get("CLIENT_PORT"));

            new Client(host, port).main();
        } catch (NumberFormatException e) {
            System.out.println(usage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
