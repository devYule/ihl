package com.yule.open.core.context;

public interface ProcessContext {

    <T> T getContext(Class<T> key);

    <T> Class<T> addContext(Class<T> key, Object context);

    <T> Object overwriteContext(Class<T> key, Object context);

    boolean has(Class<?> key);
}
