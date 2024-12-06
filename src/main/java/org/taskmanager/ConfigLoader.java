package org.taskmanager;

import java.net.URL;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ConfigLoader {
    private static Configuration config;

    private ConfigLoader() {
    }

    public static void loadConfig(String resourcePath) throws ConfigurationException {
        if (config != null) return;

        Parameters params = new Parameters();
        URL resourceUrl = ConfigLoader.class.getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            throw new IllegalArgumentException("Configuration file not found: " + resourcePath);
        }

        FileBasedConfigurationBuilder<JSONConfiguration> builder =
                new FileBasedConfigurationBuilder<>(JSONConfiguration.class)
                        .configure(params.fileBased().setURL(resourceUrl));

        config = builder.getConfiguration();
    }

    public static String getProperty(String key) {
        if (config == null) {
            throw new IllegalStateException("Configuration is not initialized. Please load the configuration first.");
        }
        return System.getProperty(key, config.getString(key));
    }

    public static int getIntProperty(String key) {
        if (config == null) {
            throw new IllegalStateException("Configuration is not initialized. Please load the configuration first.");
        }
        return Integer.parseInt(System.getProperty(key, config.getString(key)));
    }
}