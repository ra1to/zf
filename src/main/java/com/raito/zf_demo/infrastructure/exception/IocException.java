package com.raito.zf_demo.infrastructure.exception;

/**
 * @author raito
 * @since 2024/09/06
 */
@SuppressWarnings("unused")
public class IocException extends BusinessException {
    public IocException(String message) {
        super(message);
    }

    public IocException(String message, Object... args) {
        super(message, args);
    }

    public IocException(Throwable cause) {
        super(cause);
    }

    public IocException(String message, Throwable cause) {
        super(message, cause);
    }

    public IocException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
