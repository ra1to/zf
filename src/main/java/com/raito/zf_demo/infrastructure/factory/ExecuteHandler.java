package com.raito.zf_demo.infrastructure.factory;

import lombok.RequiredArgsConstructor;

/**
 * @author raito
 * @since 2024/9/7
 */
@RequiredArgsConstructor
public class ExecuteHandler<T extends ChainContext> implements Handler<T> {
    private final HandlerFactory.Executor<T> executor;
    @Override
    public  void doHandler(T context, HandlerChain<T> chain) {
        executor.execute(context);
        chain.doHandler(context);
    }
}
