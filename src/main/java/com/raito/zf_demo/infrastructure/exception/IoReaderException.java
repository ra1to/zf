package com.raito.zf_demo.infrastructure.exception;

/**
 * @author raito
 * @since 2024/09/06
 */
@SuppressWarnings("unused")
public class IoReaderException extends BusinessException {
    public IoReaderException(String message) {
        super(message);
    }

    public IoReaderException(String message, Object... args) {
        super(message, args);
    }

    public IoReaderException(Throwable cause) {
        super(cause);
    }

    public IoReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public IoReaderException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
