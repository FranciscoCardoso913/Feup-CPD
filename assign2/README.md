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

