package com.raito.zf_demo.infrastructure.task;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author raito
 * @since 2024/9/22
 */
@Slf4j
public abstract class SchedulerTask {
    protected final ThreadPoolTaskScheduler scheduler;

    protected SchedulerTask() {
        scheduler = builder();
    }

    protected abstract void runnable();

    protected abstract ThreadPoolTaskScheduler builder();

    public final void start() {
        try {
            this.scheduler.execute(this::runnable);
        } catch (Exception e) {
            log.error("thread pool task scheduler:{} execute error, reason:\n", Thread.currentThread().getName(), e);
        }
    }

    @PreDestroy
    protected void shutdown() {
        if (scheduler != null && scheduler.isRunning()) {
            scheduler.shutdown();
        }
    }
}
