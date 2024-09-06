package com.raito.zf_demo.infrastructure.exception;

/**
 * @author raito
 * @since 2024/09/06
 */
@SuppressWarnings("unused")
public class JwtException extends BusinessException {
    public JwtException(String message) {
        super(message);
    }

    public JwtException(String message, Object... args) {
        super(message, args);
    }

    public JwtException(Throwable cause) {
        super(cause);
    }

    public JwtException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
