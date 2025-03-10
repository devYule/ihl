package com.yule.open.core.context;

public interface ProcessContext {

    <T> T getContext(Class<?> key);

    Class<?> addContext(Object context);

    Class<?> addContext(Class<?> key, Object context);

    Object overwriteContext(Object context);

    boolean has(Class<?> key);
}
