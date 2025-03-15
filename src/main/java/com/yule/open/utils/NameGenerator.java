package com.yule.open.utils;

import static com.yule.open.utils.StringUtils.*;

public class NameGenerator {
    private final String prefix;
    private final String suffix;

    public NameGenerator(String prefix, String suffix) {
        this.prefix = prefix == null || prefix.isEmpty() ? "" : prefix;
        this.suffix = suffix == null || suffix.isEmpty() ? "" : suffix;
    }


    public String generateDatabaseName(String name) {
        return prefix + "_" + name.toLowerCase() + "_" + suffix;
    }

    public String generateEntityName(String name) {
        return camelFromSnake(generateDatabaseName(name), true);
    }

    public String extractOriginalName(String name) {
        String r = name;
        if (r.startsWith(prefix)) r = r.replace(prefix, "");
        int suffixIdx = r.length() - suffix.length() + 1;
        if (suffixIdx < 0) return r;
        if (r.substring(suffixIdx).equalsIgnoreCase(suffix)) {
            r = r.substring(0, suffixIdx);
        }
        return r;
    }


}
