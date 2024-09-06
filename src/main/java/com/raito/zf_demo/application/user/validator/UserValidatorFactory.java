package com.raito.zf_demo.application.user.validator;

import com.raito.zf_demo.infrastructure.Validator;

/**
 * @author raito
 * @since 2024/09/06
 */
public abstract class UserValidatorFactory {

    public static Validator create(String username, String password) {
        return () -> username != null && password != null && password.length() >= 6;
    }

    public static Validator create(String username, String password, String email) {
        return () -> {
            boolean validate = create(username, password).validate();
            return validate && email != null && email.matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
        };
    }
}
