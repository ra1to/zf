package com.raito.zf_demo.application.user.validator;

import com.raito.zf_demo.infrastructure.factory.ChainContext;
import com.raito.zf_demo.infrastructure.factory.HandlerFactory;

/**
 * @author raito
 * @since 2024/09/06
 */
public abstract class UserValidatorFactory {

    public static <T extends ChainContext> HandlerFactory.DefaultValidator<T> create(String username, String password) {
        return (context) -> username != null && password != null && password.length() >= 6;
    }

    public static <T extends ChainContext> HandlerFactory.DefaultValidator<T> create(String username, String password, String email) {
        return (context) -> {
            boolean validate = create(username, password).validate(context);
            return validate && email != null && email.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
        };
    }
}
