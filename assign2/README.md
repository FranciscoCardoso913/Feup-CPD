## How to run

### Configuration
You can set configuration settings in `src/config.properties`

### Building the Project
To build the project, run the following command from the root of the project:
```bash
./gradlew build
```

### Running the Project

To start the server, use the following command, specifying the mode by replacing `[mode]` with either `0` or `1`. If a mode is not provided on the command line, the server will retrieve the mode from the configuration file.

```bash
java -cp build/classes/java/main/ Main -s [mode]
```

* **`0`**: Simple Queue
* **`1`**: Ranked Queue

To start the client:
```bash
java -cp build/classes/java/main/ Main -c
```

---

## Configuration

You can customize environment variables by modifying the `src/config.properties` file.

### Game:

- **PLAYER_PER_GAME:** Specifies the number of players allowed per game.
- **COINS_PER_GAME:** Defines the number of coins allocated per game.

### Server:

- **PING_PERIOD:** Interval between ping requests, measured in milliseconds.
- **CLIENT_TIMEOUT:** Duration a client can remain disconnected before being removed from the queue, measured in milliseconds.
- **SERVER_PORT:** Port on which the server operates.
- **MODE:** Queue mode configuration; 0 for a simple queue, 1 for a Ranked queue.
- **DB_PATH:** Path to the database directory.
- **INACTIVE_TIMEOUT:** Maximum time a client can remain inactive before disconnection, measured in seconds.
- **AUTH_TIMEOUT:** Maximum duration a client can spend on login/register actions before encountering a timeout, measured in seconds.
- **LOGIN_MAX_ATTEMPTS:** Maximum number of login attempts permitted.

### Client:

- **HOSTNAME:** Hostname of the server.
- **CLIENT_PORT:** Port used for client-server communication.

