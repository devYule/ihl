package com.yule.open.database.data.enums;

import com.yule.open.javapoet.spec.wrapper.impl.ConstraintsAnnotationSpecWrapper;

import java.util.Arrays;

import static com.yule.open.utils.Logger.error;

public enum ConstraintsType {
    // Analyse database
    PRIMARY_KEY("P", null),
    FOREIGN_KEY("R", ConstraintsAnnotationSpecWrapper.AnnotationKindIndex.MANY_TO_ONE),
    UNIQUE("U", ConstraintsAnnotationSpecWrapper.AnnotationKindIndex.COLUMN),
    CHECK("C", ConstraintsAnnotationSpecWrapper.AnnotationKindIndex.CHECK),
    // internal
    COLUMN("IC", ConstraintsAnnotationSpecWrapper.AnnotationKindIndex.COLUMN),
    JOIN_COLUMN("IJC", ConstraintsAnnotationSpecWrapper.AnnotationKindIndex.JOIN_COLUMN),
    ;
    private final String token;
    private final ConstraintsAnnotationSpecWrapper.AnnotationKindIndex anno;

    ConstraintsType(String token, ConstraintsAnnotationSpecWrapper.AnnotationKindIndex anno) {
        this.token = token;
        this.anno = anno;
    }

    public ConstraintsAnnotationSpecWrapper.AnnotationKindIndex getAnno() {
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
