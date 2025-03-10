package com.yule.open.core.context.impl;

import com.yule.open.core.context.ProcessContext;

import java.util.HashMap;
import java.util.Map;

public class DefaultProcessContext implements ProcessContext {
    private final Map<Class<?>, Object> context;

    {
        context = new HashMap<>();
    }

    public <T> T getContext(Class<?> key) {
        Object val = context.get(key);
        return val == null ? null : (T) val;
    }

    public Class<?> addContext(Object context) {
        return addContext(context.getClass(), context);
    }

    public Class<?> addContext(Class<?> key, Object context) {
        this.context.put(key, context);
        return key;
    }

    public Object overwriteContext(Object context) {
        Object prev = this.context.get(context.getClass());
        addContext(context);
        return prev;
    }

    public boolean has(Class<?> key) {
        return this.context.get(key) != null;
    }
}
