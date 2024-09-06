package com.raito.zf_demo.infrastructure.exception;

/**
 * @author raito
 * @since 2024/09/05
 */
public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Object... args) {
        super(message, args);
    }
}
