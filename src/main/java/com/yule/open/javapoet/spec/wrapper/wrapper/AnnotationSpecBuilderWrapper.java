package com.yule.open.javapoet.spec.wrapper.wrapper;

import com.yule.open.javapoet.spec.wrapper.impl.ConstraintsAnnotationSpecWrapper;

public class AnnotationSpecBuilderWrapper {
    private final ConstraintsAnnotationSpecWrapper.AnnotationKindIndex kind;
    private final com.squareup.javapoet.AnnotationSpec.Builder builder;

    public AnnotationSpecBuilderWrapper(ConstraintsAnnotationSpecWrapper.AnnotationKindIndex kind, com.squareup.javapoet.AnnotationSpec.Builder builder) {
        this.kind = kind;
        this.builder = builder;
    }

    public ConstraintsAnnotationSpecWrapper.AnnotationKindIndex getKind() {
        return kind;
    }

    public com.squareup.javapoet.AnnotationSpec.Builder getBuilder() {
        return builder;
    }
}
