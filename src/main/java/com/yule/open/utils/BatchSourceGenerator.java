package com.yule.open.utils;

import java.util.List;

public abstract class BatchSourceGenerator<D, T> extends SourceHolder<D> implements SourceGenerator<T> {
    abstract protected List<T> batch(T[] tables);
}
