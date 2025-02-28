package com.yule.open.database.enums;

import java.util.Arrays;

public enum DatabaseKind {
    ORACLE("oracle"),
    MYSQL("mysql"),
    MARIADB("mariadb"),
    ;
    private final String kind;

    DatabaseKind(String k) {
        this.kind = k;
    }

    public static DatabaseKind getByValue(String kind) throws ClassNotFoundException {
        return Arrays.stream(DatabaseKind.values()).filter(n -> kind.contains(n.kind)).findFirst().orElseThrow(RuntimeException::new);
    }

    public String getKind() {
        return kind;
    }
}
