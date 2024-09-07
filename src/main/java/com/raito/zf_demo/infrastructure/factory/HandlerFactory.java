package com.raito.zf_demo.infrastructure.factory;

import com.raito.zf_demo.infrastructure.exception.ValidateException;
import com.raito.zf_demo.infrastructure.util.SpringContextUtils;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author raito
 * @since 2024/9/7
 */
public abstract class HandlerFactory {
    protected static final JpaTransactionManager transactionManager = SpringContextUtils.getBean(JpaTransactionManager.class);

    public static Chain create() {
        return new Chain();
    }

    @Slf4j
    public static class Chain {
        private final LinkedList<Handler> handlers = new LinkedList<>();
        public Chain executor(Executor executor) {
            handlers.add(new Handler() {
                @Override
                public <T extends ChainContext> void doHandler(T context, HandlerChain chain) {
                    executor.execute(context);
                    chain.doHandler(context);
                }
            });
            return this;
        }
        public Chain validator(DefaultValidator validator) {
            return validator(validator, null);
        }
        public Chain validator(DefaultValidator validator, Message message) {
            handlers.add(new Handler() {
                @Override
                public <T extends ChainContext> void doHandler(T context, HandlerChain chain) {
                    if (validator.validate(context)) {
                        chain.doHandler(context);
                    }
                    if (message != null) {
                        throw new ValidateException(message.get(context));
                    }
                }
            });
            return this;
        }
        public void execute() {
            DefaultHandlerChain chain = new DefaultHandlerChain(handlers);
            chain.doHandler(new ChainContext());
        }
        public void executeTransaction() {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus status = HandlerFactory.transactionManager.getTransaction(def);
            try {
                execute();
                HandlerFactory.transactionManager.commit(status);
            } catch (Exception e) {
                log.error("executeTransaction error", e);
                HandlerFactory.transactionManager.rollback(status);
                throw e;
            }
        }
    }
    @FunctionalInterface
    public interface Executor {
        <T extends ChainContext> void execute(T context);
    }
    @FunctionalInterface
    public interface Message {
        <T extends ChainContext> String get(T context);
    }
    @FunctionalInterface
    public interface DefaultValidator  {
        <T extends ChainContext> boolean validate(T context);
    }
    private static class DefaultHandlerChain implements HandlerChain {
        private final List<Handler> handlers;
        @Nullable
        private Iterator<Handler> iterator;

        public DefaultHandlerChain(List<Handler> handlers) {
            this.handlers = handlers;
        }

        @Override
        public <T extends ChainContext> void doHandler(T context) {
            if (this.iterator == null) {
                this.iterator = this.handlers.iterator();
            }
            if (this.iterator.hasNext()) {
                Handler handler = this.iterator.next();
                handler.doHandler(context, this);
            }
        }
    }
}
