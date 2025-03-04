package com.yule.open.javapoet.spec.wrapper;

public abstract class SpecWrapper {

    private int pkCnt;

    public SpecWrapper() {
        this.pkCnt = 0;
    }

    public final int addPKCnt() {
        return ++this.pkCnt;
    }

    public final int addPKCnt(int cnt) {
        return this.pkCnt = cnt;
    }

    public final int getPkCnt() {
        return pkCnt;
    }
}
