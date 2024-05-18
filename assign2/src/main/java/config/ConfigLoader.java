package config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;


public class ConfigLoader {

    private Properties config;

    public ConfigLoader() {
        config = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        File file = new File("src/config.properties");
        if(!file.exists())  {
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
