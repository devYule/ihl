package com.yule.open.database.data.enums;

import com.yule.open.utils.javapoet.spec.wrapper.impl.AnnotationSpecWrapper;

import java.util.Arrays;

import static com.yule.open.utils.Logger.error;

public enum ConstraintsType {
    // Analyse database
    PRIMARY_KEY("P", null),
    FOREIGN_KEY("R", AnnotationSpecWrapper.AnnotationKindIndex.MANY_TO_ONE),
    UNIQUE("U", AnnotationSpecWrapper.AnnotationKindIndex.COLUMN),
    CHECK("C", AnnotationSpecWrapper.AnnotationKindIndex.CHECK),
    // internal
    COLUMN("IC", AnnotationSpecWrapper.AnnotationKindIndex.COLUMN),
    JOIN_COLUMN("IJC", AnnotationSpecWrapper.AnnotationKindIndex.JOIN_COLUMN),
    ;
    private final String token;
    private final AnnotationSpecWrapper.AnnotationKindIndex anno;

    ConstraintsType(String token, AnnotationSpecWrapper.AnnotationKindIndex anno) {
        this.token = token;
        this.anno = anno;
    }

    public AnnotationSpecWrapper.AnnotationKindIndex getAnno() {
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
