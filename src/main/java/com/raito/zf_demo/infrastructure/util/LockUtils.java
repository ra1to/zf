package com.raito.zf_demo.infrastructure.util;

import com.raito.zf_demo.infrastructure.lock.Lock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author raito
 * @since 2024/9/22
 */
@Component
public class LockUtils {
    private static Lock lock;

    public static void lock(String name, Executor executor) {
        try {
            lock.lock(name);
            executor.execute();
        } finally {
            lock.unlock(name);
        }
    }

    public static void tryLock(String name, Executor executor) {
        if (lock.tryLock(name)) {
            try {
                executor.execute();
            } finally {
                lock.unlock(name);
            }
        }
    }

    @FunctionalInterface
    public interface Executor {
        void execute();
    }

    @Autowired
    public void setLock(Lock lock) {
        LockUtils.lock = lock;
    }
}
