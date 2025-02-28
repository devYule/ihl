package com.yule.open.properties;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private static final Map<EnvironmentProperties, String> properties;

    static {
        properties = new HashMap<>();
    }

    public static String get(EnvironmentProperties key) {
        return properties.get(key);
    }

    public static String put(EnvironmentProperties key, String value) {
        return properties.put(key, value);
    }

    public static void putAll(Map<? extends EnvironmentProperties, ? extends String> maps) {
        properties.putAll(maps);
    }
}
