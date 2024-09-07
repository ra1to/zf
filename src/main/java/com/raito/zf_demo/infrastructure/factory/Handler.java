package com.raito.zf_demo.infrastructure.factory;

/**
 * @author raito
 * @since 2024/9/7
 */
public interface Handler {
    <T extends ChainContext> void doHandler(T context, HandlerChain chain);
}
