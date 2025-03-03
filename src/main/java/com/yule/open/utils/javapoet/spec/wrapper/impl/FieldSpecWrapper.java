package com.yule.open.utils.javapoet.spec.wrapper.impl;

import com.squareup.javapoet.FieldSpec;
import com.yule.open.utils.javapoet.spec.wrapper.Spec;

public class FieldSpecWrapper extends Spec {

    private final int parent;
    private final FieldSpec.Builder builder;
    private int pkCnt;
    private boolean isFK;
    private final String fieldNm;



    public FieldSpecWrapper(int parent, FieldSpec.Builder builder, String fieldNm) {
        this.parent = parent;
        this.builder = builder;
        this.pkCnt = 0;
        this.fieldNm = fieldNm;
    }

    public String getFieldNm() {
        return fieldNm;
    }

    public boolean isFK() {
        return isFK;
    }

    public void setFK(boolean FK) {
        isFK = FK;
    }

    public int getParent() {
        return parent;
    }

    public FieldSpec.Builder getBuilder() {
        return builder;
    }



}
