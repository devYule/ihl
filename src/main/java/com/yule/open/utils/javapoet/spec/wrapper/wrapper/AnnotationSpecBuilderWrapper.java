package com.yule.open.utils.javapoet.spec.wrapper.wrapper;

import com.yule.open.utils.javapoet.spec.wrapper.impl.AnnotationSpecWrapper;

public class AnnotationSpecBuilderWrapper {
    private final AnnotationSpecWrapper.AnnotationKindIndex kind;
    private final com.squareup.javapoet.AnnotationSpec.Builder builder;

    public AnnotationSpecBuilderWrapper(AnnotationSpecWrapper.AnnotationKindIndex kind, com.squareup.javapoet.AnnotationSpec.Builder builder) {
        this.kind = kind;
        this.builder = builder;
    }

    public AnnotationSpecWrapper.AnnotationKindIndex getKind() {
        return kind;
    }

    public com.squareup.javapoet.AnnotationSpec.Builder getBuilder() {
        return builder;
    }
}
