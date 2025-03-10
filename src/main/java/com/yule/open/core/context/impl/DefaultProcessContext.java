package com.yule.open.core.context.impl;

import com.yule.open.core.context.ProcessContext;

import java.util.HashMap;
import java.util.Map;

public class DefaultProcessContext implements ProcessContext {
    private final Map<Class<?>, Object> context;

    {
        context = new HashMap<>();
    }

    public <T> T getContext(Class<T> key) {
        Object val = context.get(key);
        return key.isInstance(val) ? key.cast(val) : null;
    }

    public <T> Class<T> addContext(Class<T> key, Object context) {
        this.context.put(key, context);
        return key;
    }

    public <T> T overwriteContext(Class<T> key, Object context) {
        Object prev = this.context.get(key);
        T t = null;
        if (key.isInstance(prev)) t = key.cast(prev);
        addContext(key, context);
        return t;
    }

    public boolean has(Class<?> key) {
        return this.context.get(key) != null;
    }
}
