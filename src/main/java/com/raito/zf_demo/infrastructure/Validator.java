package com.raito.zf_demo.infrastructure;

import com.raito.zf_demo.infrastructure.exception.ValidateException;

/**
 * @author raito
 * @since 2024/09/05
 */
@FunctionalInterface
public interface Validator {
    boolean validate() throws ValidateException;
}
