import java.io.IOException;

import client.Client;
import config.ConfigLoader;
import server.Server;
// import org.json.simple.*;

/**
 * Main application entry point. This class handles command-line arguments to start different
 * components of the application such as the server or client.
 */
public class Main {

    private static final String SERVER_MODE_SIMPLE = "0";
    private static final String SERVER_MODE_RANKING = "1";
    private static final String COMMAND_SERVER = "-s";
    private static final String COMMAND_CLIENT = "-c";
    private static final String HELP_LONG = "--help";
    private static final String HELP_SHORT = "-h";

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
                args.length != 1 && !(args.length == 2 && args[0].equals(COMMAND_SERVER)) ||
                args.length == 1 && (args[0].equals(HELP_LONG) || args[0].equals(HELP_SHORT))
        ) {
            System.out.println(usage);
            return;
        }

        ConfigLoader config = new ConfigLoader();

        switch (args[0]) {
            case COMMAND_SERVER:
                if (args.length == 2 && (args[1].equals(SERVER_MODE_SIMPLE) || args[1].equals(SERVER_MODE_RANKING)))
                    startServer(config, args[1]);
                else
                    startServer(config, null);
                break;
            case COMMAND_CLIENT:
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
