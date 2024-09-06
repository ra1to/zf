package com.raito.zf_demo.infrastructure.exception;

/**
 * @author raito
 * @since 2024/09/05
 */
@SuppressWarnings("unused")
public class ValidateException extends BusinessException {
    public ValidateException(String message) {
        super(message);
    }

    public ValidateException(String message, Object... args) {
        super(message, args);
    }

    public ValidateException(Throwable cause) {
        super(cause);
    }

    public ValidateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidateException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
