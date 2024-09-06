package com.raito.zf_demo.infrastructure.exception;

/**
 * @author raito
 * @since 2024/09/05
 */
@SuppressWarnings("unused")
public abstract class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Object... args) {
        super(String.format(message, args));
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause, String message, Object... args) {
        super(String.format(message, args), cause);
    }
}
