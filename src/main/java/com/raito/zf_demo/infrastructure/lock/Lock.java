package com.raito.zf_demo.infrastructure.lock;

/**
 * @author raito
 * @since 2024/9/22
 */
public interface Lock {
    void lock(String name);

    boolean tryLock(String name);

    void unlock(String name);
}
