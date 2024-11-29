package org.taskmanager;

import java.net.URL;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class ConfigLoader {
    private static Configuration config;

    public static Configuration loadConfig(String resourcePath) throws ConfigurationException {
        if (config != null) return config; // Используем кэшированную конфигурацию

        Parameters params = new Parameters();
        URL resourceUrl = ConfigLoader.class.getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            throw new IllegalArgumentException("Configuration file not found: " + resourcePath);
        }

        FileBasedConfigurationBuilder<JSONConfiguration> builder =
                new FileBasedConfigurationBuilder<>(JSONConfiguration.class)
                        .configure(params.fileBased().setURL(resourceUrl));

        config = builder.getConfiguration();
        return config;
    }

    public static String getProperty(String key, String defaultValue) {
        return System.getProperty(key, config != null ? config.getString(key, defaultValue) : defaultValue);
    }

    public static int getIntProperty(String key, int defaultValue) {
        return Integer.parseInt(System.getProperty(key, config != null ? config.getString(key, String.valueOf(defaultValue)) : String.valueOf(defaultValue)));
    }
}