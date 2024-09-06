package com.raito.zf_demo.infrastructure.exception;

/**
 * @author raito
 * @since 2024/09/06
 */
@SuppressWarnings("unused")
public class ConcurrentException extends BusinessException {
    public ConcurrentException(String message) {
        super(message);
    }

    public ConcurrentException(String message, Object... args) {
        super(message, args);
    }

    public ConcurrentException(Throwable cause) {
        super(cause);
    }

    public ConcurrentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConcurrentException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
