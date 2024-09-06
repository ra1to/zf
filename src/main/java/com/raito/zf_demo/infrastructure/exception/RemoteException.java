package com.raito.zf_demo.infrastructure.exception;

/**
 * @author raito
 * @since 2024/09/05
 */
@SuppressWarnings("unused")
public class RemoteException extends BusinessException {
    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String message, Object... args) {
        super(message, args);
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteException(Throwable cause, String message, Object... args) {
        super(cause, message, args);
    }
}
