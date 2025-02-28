package com.yule.open.info.enums;

import java.util.Arrays;

public enum ConstraintsType {
    NONE("NONE"),
    PRIMARY_KEY("P"),
    FOREIGN_KEY("R"),
    UNIQUE("U"),
    CHECK("C"),
    EMBEDDABLE("EA"),
    EMBEDDED_ID("EI"),
    ;
    private final String token;

    ConstraintsType(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public static ConstraintsType getByValue(String token) {
        return Arrays.stream(ConstraintsType.values()).filter(t -> t.token.equalsIgnoreCase(token)).findFirst().orElse(NONE);
    }
}
