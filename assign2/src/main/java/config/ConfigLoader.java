package config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Utility class for loading configuration properties.
 */
public class ConfigLoader {

    private static final String CONFIG_FILE_PATH = "src/config.properties";

    private Properties config;

    /**
     * Constructs a new ConfigLoader object and loads the properties from the config file.
     */
    public ConfigLoader() {
        config = new Properties();
        loadProperties();
    }

    /**
     * Loads the properties from the config file.
     */
    private void loadProperties() {
        File file = new File(CONFIG_FILE_PATH);
        if (!file.exists()) {
            System.out.println("Failed to load config.properties");
            return;
        }
        try (InputStream input = new FileInputStream(file)) {
            config.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves the value associated with the specified key from the configuration.
     *
     * @param key The key whose associated value is to be retrieved.
     * @return The value associated with the specified key, or null if no value is found.
     */
    public String get(String key) {
        return config.getProperty(key);
    }
}
