package com.raito.zf_demo.infrastructure.factory;

import com.raito.zf_demo.infrastructure.exception.ValidateException;
import lombok.RequiredArgsConstructor;

/**
 * @author raito
 * @since 2024/9/7
 */
@RequiredArgsConstructor
public class ValidatorHandler<T extends ChainContext> implements Handler<T> {
    private final HandlerFactory.DefaultValidator<T> validator;
    private final HandlerFactory.Message<T> message;

    @Override
    public void doHandler(T context, HandlerChain<T> chain) {
        if (validator.validate(context)) {
            chain.doHandler(context);
        }
        if (message != null) {
            throw new ValidateException(message.get(context));
        }
    }
}
