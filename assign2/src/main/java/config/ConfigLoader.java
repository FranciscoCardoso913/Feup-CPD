package config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

/**
 * Utility class for loading configuration properties.
 */
public class ConfigLoader {

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
        File file = new File("src/config.properties");
        if (!file.exists()) {
            System.out.println("Failed to load config.properties");
            printDebugInfo();
            return;
        }
        try (InputStream input = new FileInputStream(file)) {
            config.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Prints debug information.
     */
    private void printDebugInfo() {
        System.out.println("Current directory: " + new File(".").getAbsolutePath());
        System.out.println("Classpath: " + System.getProperty("java.class.path"));
        // Attempt to list all resources seen by the class loader
        try {
            java.util.Enumeration<URL> urls = ConfigLoader.class.getClassLoader().getResources("");
            while (urls.hasMoreElements()) {
                System.out.println(urls.nextElement());
            }
        } catch (IOException e) {
            e.printStackTrace();
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
