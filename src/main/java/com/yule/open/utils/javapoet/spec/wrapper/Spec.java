package com.yule.open.utils.javapoet.spec.wrapper;

public abstract class Spec {

    private int pkCnt;

    public Spec() {
        this.pkCnt = 0;
    }

    public final int addPKCnt() {
        return ++this.pkCnt;
    }

    public final int getPkCnt() {
        return pkCnt;
    }
}
