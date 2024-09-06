package com.raito.zf_demo.infrastructure.exception;

/**
 * @author raito
 * @since 2024/09/06
 */
@SuppressWarnings("unused")
public class ExistException extends BusinessException {
    public ExistException(String message) {
        super(message);
    }

    public ExistException(String message, Object... args) {
        super(message, args);
    }

    public ExistException(Throwable cause) {
        super(cause);
    }

    public ExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExistException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
