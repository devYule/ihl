package com.yule.open.database.data.enums;

import com.yule.open.utils.javapoet.spec.wrapper.AnnotationSpecWrapper;

import java.util.Arrays;

import static com.yule.open.utils.Logger.error;

public enum ConstraintsType {
    PRIMARY_KEY("P", null),
    FOREIGN_KEY("R", AnnotationSpecWrapper.AnnotationKind.MANY_TO_ONE),
    UNIQUE("U", AnnotationSpecWrapper.AnnotationKind.COLUMN),
    CHECK("C", AnnotationSpecWrapper.AnnotationKind.CHECK),

    ;
    private final String token;
    private final AnnotationSpecWrapper.AnnotationKind anno;

    ConstraintsType(String token, AnnotationSpecWrapper.AnnotationKind anno) {
        this.token = token;
        this.anno = anno;
    }

    public AnnotationSpecWrapper.AnnotationKind getAnno() {
        return anno;
    }

    public String getToken() {
        return token;
    }

    public static ConstraintsType getByValue(String token) {
        return Arrays.stream(ConstraintsType.values()).filter(t -> t.token.equalsIgnoreCase(token)).findFirst().orElseThrow(() -> {
            error("Can not find element in ConstraintsType!");
            return new RuntimeException("Can not find element in ConstraintsType!");
        });
    }
}
