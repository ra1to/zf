package com.raito.zf_demo.infrastructure.factory;

import com.raito.zf_demo.infrastructure.exception.NotFoundException;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author raito
 * @since 2024/9/7
 */
public class ChainContext {
    protected volatile ConcurrentHashMap<String, Object> context = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = context.get(key);
        if (value == null) {
            return null;
        }
        if (clazz.isInstance(value)) {
            return (T) value;
        }
        throw new NotFoundException("key: " + key + " not found");
    }

    public Object get(String key) {
        return context.get(key);
    }

    public ChainContext set(String key, Object value) {
        if(value != null) {
            context.put(key, value);
        }
        return this;
    }
}
