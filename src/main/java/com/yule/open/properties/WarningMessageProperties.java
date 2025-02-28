package com.yule.open.properties;

public enum WarningMessageProperties {
    CAN_NOT_GENERATE_SOURCE("Can not generate source: ");

    private final String message;

    WarningMessageProperties(String message) {
        this.message = message;
    }

    public String getMessage(String name) {
        return this.message + "`" + name + "`";
    }
}
