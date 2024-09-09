package com.raito.zf_demo.infrastructure.exception;

/**
 * @author raito
 * @since 2024/09/09
 */
@SuppressWarnings("unused")
public class JsonException extends BusinessException {
    public JsonException(String message) {
        super(message);
    }

    public JsonException(String message, Object... args) {
        super(message, args);
    }

    public JsonException(Throwable cause) {
        super(cause);
    }

    public JsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
