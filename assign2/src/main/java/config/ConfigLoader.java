package config;

import java.util.Properties;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.io.FileInputStream;


public class ConfigLoader {

    private Properties config;

    public ConfigLoader() {
        config = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        String resourceName = "/main/config.properties";
        //Path path = Paths.get(getClass().getClassLoader().getResource("config.properties").toURI());
        File file = new File("src/main/resources/config.properties");
        try (InputStream input = new FileInputStream("/src/main/resources/config.properties")) {
            if (input == null) {
                System.out.println("Failed to load " + resourceName);
                printDebugInfo();
                return;
            }

            config.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

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

    public String get(String key) {
        return config.getProperty(key);
    }
}
