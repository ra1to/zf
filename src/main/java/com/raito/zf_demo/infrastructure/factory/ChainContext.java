package com.raito.zf_demo.infrastructure.factory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author raito
 * @since 2024/9/7
 */
public class ChainContext {
    protected volatile ConcurrentHashMap<String, Object> context = new ConcurrentHashMap<>();

    public <T> T get(String key, Class<T> clazz) {
        Object value = context.get(key);
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return null;
    }

    public Object get(String key) {
        try {
            return context.get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public <T> T get(Class<T> clazz) {
        return clazz.cast(context.values().stream().filter(clazz::isInstance).findFirst().orElse(null));
    }

    public ChainContext set(String key, Object value) {
        if (value != null) {
            context.put(key, value);
        }
        return this;
    }

    public ChainContext set(Object value) {
        if (value != null) {
            context.put(value.getClass().getName(), value);
        }
        return this;
    }
}
