package com.raito.zf_demo.infrastructure.exception;

/**
 * @author raito
 * @since 2024/09/06
 */
@SuppressWarnings("unused")
public class KeyException extends BusinessException {
    public KeyException(String message) {
        super(message);
    }

    public KeyException(String message, Object... args) {
        super(message, args);
    }

    public KeyException(Throwable cause) {
        super(cause);
    }

    public KeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
