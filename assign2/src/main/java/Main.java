import client.Client;
import server.Server;

import java.io.IOException;
// import org.json.simple.*;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("Hello, Gradle!");
        if(args[1].equals( "server")) new Server().main();
        else new Client().main();
    }
}
