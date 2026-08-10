package com.kasirpintar.automation.config;

import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties PROPS = new Properties();

    static {
        load("config/config.properties", true);
        String env = System.getProperty("env");
        if (env != null && !env.isBlank()) {
            load("config/config-" + env.trim() + ".properties", true);
        }
        load("config/config.local.properties", false);
    }

    private static void load(String resource, boolean required) {
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                if (required) {
                    throw new IllegalStateException(resource + " not found on classpath");
                }
                return;
            }
            PROPS.load(in);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Failed to load " + resource + ": " + e.getMessage());
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        return System.getProperty(key, PROPS.getProperty(key));
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        return (value == null || value.isBlank()) ? defaultValue : Integer.parseInt(value.trim());
    }
}
