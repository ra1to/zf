package com.raito.zf_demo.infrastructure.factory;

import com.raito.zf_demo.infrastructure.util.SpringContextUtils;
import jakarta.annotation.Nullable;
import lombok.Getter;
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

    public static ChainBuilder<ChainContext> create() {
        return new ChainBuilder<>(new ChainContext());
    }

    public static <T extends ChainContext> ChainBuilder<T> create(T context) {
        return new ChainBuilder<>(context);
    }

    @Slf4j
    public static class ChainBuilder<T extends ChainContext> {
        private final LinkedList<Handler<T>> handlers = new LinkedList<>();
        @Getter
        private final T context;

        public ChainBuilder(T context) {
            this.context = context;
        }

        public ChainBuilder<T> executor(Executor<T> executor) {
            handlers.add(new ExecuteHandler<>(executor));
            return this;
        }

        public ChainBuilder<T> addContext(String key, Object value) {
            context.set(key, value);
            return this;
        }

        public ChainBuilder<T> addContext(Object... objects) {
            for (var obj : objects) {
                context.set(obj);
            }
            return this;
        }

        public ChainBuilder<T> validator(DefaultValidator<T> validator) {
            return validator(validator, null);
        }

        public ChainBuilder<T> validator(DefaultValidator<T> validator, Message<T> message) {
            handlers.add(new ValidatorHandler<>(validator, message));
            return this;
        }

        public T execute() {
            DefaultHandlerChain<T> chain = new DefaultHandlerChain<>(handlers);
            chain.doHandler(context);
            return context;
        }

        public T executeTransaction() {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus status = HandlerFactory.transactionManager.getTransaction(def);
            try {
                execute();
                HandlerFactory.transactionManager.commit(status);
                return context;
            } catch (Exception e) {
                log.error("executeTransaction error", e);
                HandlerFactory.transactionManager.rollback(status);
                throw e;
            }
        }
    }

    @FunctionalInterface
    public interface Executor<T extends ChainContext> {
        void execute(T context);
    }

    @FunctionalInterface
    public interface Message<T extends ChainContext> {
        String get(T context);
    }

    @FunctionalInterface
    public interface DefaultValidator<T extends ChainContext> {
        boolean validate(T context);
    }

    private static class DefaultHandlerChain<T extends ChainContext> implements HandlerChain<T> {
        private final List<Handler<T>> handlers;
        @Nullable
        private Iterator<Handler<T>> iterator;

        public DefaultHandlerChain(List<Handler<T>> handlers) {
            this.handlers = handlers;
        }

        @Override
        public void doHandler(T context) {
            if (this.iterator == null) {
                this.iterator = this.handlers.iterator();
            }
            if (this.iterator.hasNext()) {
                Handler<T> handler = this.iterator.next();
                handler.doHandler(context, this);
            }
        }
    }
}
