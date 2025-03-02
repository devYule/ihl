package com.yule.open.database.info.enums;

public enum ConstraintType {
    FK("fk")
    ;

    private final String type;

    ConstraintType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
