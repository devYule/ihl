package com.yule.open.utils;

import java.util.ArrayList;
import java.util.List;

public abstract class SourceHolder<T> {

    protected List<T> batchTables;

    protected SourceHolder() {
        batchTables = new ArrayList<>();
    }

    protected void addToBatchTable(T t) {
        batchTables.add(t);
    }

    protected List<T> getBatchTables() {
        return batchTables;
    }
}
