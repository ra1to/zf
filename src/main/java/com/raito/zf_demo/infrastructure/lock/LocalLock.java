package com.raito.zf_demo.infrastructure.lock;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.raito.zf_demo.infrastructure.exception.ConcurrentException;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author raito
 * @since 2024/9/22
 */
@Component
public class LocalLock implements Lock {
    private final static Cache<String, ReentrantLock> locks = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build();

    @Override
    public void lock(String name) {
        try {
            ReentrantLock lock = locks.get(name, ReentrantLock::new);
            lock.lock();
        } catch (ExecutionException e) {
            throw new ConcurrentException(e);
        }
    }

    @Override
    public boolean tryLock(String name) {
        try {
            ReentrantLock lock = locks.get(name, ReentrantLock::new);
            return lock.tryLock();
        } catch (ExecutionException e) {
            throw new ConcurrentException(e);
        }
    }

    @Override
    public void unlock(String name) {
        ReentrantLock lock = locks.getIfPresent(name);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        } else {
            throw new ConcurrentException("Current thread does not hold the lock: " + name);
        }
    }
}
