package com.raito.zf_demo.infrastructure.factory;

import com.raito.zf_demo.infrastructure.Validator;
import com.raito.zf_demo.infrastructure.exception.ValidateException;
import com.raito.zf_demo.infrastructure.util.SpringContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.LinkedList;
import java.util.List;

/**
 * @author raito
 * @since 2024/09/05
 */
public abstract class ChainFactory {
    protected static final JpaTransactionManager transactionManager = SpringContextUtils.getBean(JpaTransactionManager.class);

    public static Chain create() {
        return new Chain(new LinkedList<>());
    }

    @RequiredArgsConstructor
    @Slf4j
    public static class Chain {
        private final List<Object> executors;

        public Chain validator(Validator validator) {
            return validator(validator, null);
        }

        public Chain validator(Validator validator, String failMessage) {
            executors.add(new ValidatorMessage(validator, failMessage));
            return this;
        }

        public Chain executor(Executor executor) {
            executors.add(executor);
            return this;
        }

        public void execute() throws ValidateException{
            for (Object object : executors) {
                if (object == null) {
                    continue;
                }
                if (object instanceof ValidatorMessage message) {
                    if (!message.validator.validate()) {
                        if (message.failMessage != null) {
                            throw new ValidateException(message.failMessage);
                        }
                        break;
                    }
                } else if (object instanceof Executor executor) {
                    executor.execute();
                }
            }
        }

        public void executeTransaction() {
            // 使用 REQUIRED 传播行为加入外部事务
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            TransactionStatus status = ChainFactory.transactionManager.getTransaction(def);
            try {
                execute();
                ChainFactory.transactionManager.commit(status);
            } catch (Exception e) {
                log.error("executeTransaction error", e);
                ChainFactory.transactionManager.rollback(status);
                throw e;
            }
        }
    }

    @FunctionalInterface
    public interface Executor {
        void execute();
    }

    private record ValidatorMessage(Validator validator, String failMessage) {

    }
}
