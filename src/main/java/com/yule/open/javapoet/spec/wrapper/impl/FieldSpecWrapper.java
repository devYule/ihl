package com.yule.open.javapoet.spec.wrapper.impl;

import com.squareup.javapoet.FieldSpec;
import com.yule.open.javapoet.spec.wrapper.SpecWrapper;

public class FieldSpecWrapper extends SpecWrapper {

    private final int parent;
    private final FieldSpec.Builder builder;
    private boolean isFK;
    private String fieldNm;
    private final String refTb;

    public FieldSpecWrapper(int parent, FieldSpec.Builder builder, String fieldNm, String refTb) {
        this.parent = parent;
        this.builder = builder;
        this.fieldNm = fieldNm;
        this.refTb = refTb;
    }

    public void setFieldNm(String fieldNm) {
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

    public String getRefTb() {
        return refTb;
    }

    public int getParent() {
        return parent;
    }

    public FieldSpec.Builder getBuilder() {
        return builder;
    }


}
