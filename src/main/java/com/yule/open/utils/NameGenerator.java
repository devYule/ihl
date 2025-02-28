package com.yule.open.utils;

import static com.yule.open.utils.StringUtils.*;

public class NameGenerator {
    private final String prefix;
    private final String suffix;

    public NameGenerator(String prefix, String suffix) {

        this.prefix = prefix == null || prefix.isEmpty() ? "" : camelFromSnake(prefix, true);
        this.suffix = suffix == null || suffix.isEmpty() ? "" : camelFromSnake(suffix, true);
    }


    public String generateDatabaseName(String name) {
        return prefix + "_" + name.toLowerCase() + "_" + suffix;
    }

    public String generateEntityName(String name) {
        return prefix + camelFromSnake(name, true) + suffix;
    }



}
