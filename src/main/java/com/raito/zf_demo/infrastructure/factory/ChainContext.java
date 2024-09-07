package com.raito.zf_demo.infrastructure.factory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author raito
 * @since 2024/9/7
 */
public class ChainContext {
    protected volatile ConcurrentHashMap<String, Object> context = new ConcurrentHashMap<>();
    public <T> T get(String key, Class<T> clazz) {
        return clazz.cast(context.get(key));
    }
    public void set(String key, Object value) {
        context.put(key, value);
    }
}
