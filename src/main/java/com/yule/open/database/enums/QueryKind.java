package com.yule.open.database.enums;

public enum QueryKind {
    GET_TABLE("get_table-"),
    ALL("all-"),
    ;

    private final String prefix;

    QueryKind(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
