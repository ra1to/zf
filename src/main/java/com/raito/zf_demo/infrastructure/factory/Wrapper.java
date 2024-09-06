package com.raito.zf_demo.infrastructure.factory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author raito
 * @since 2024/09/05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wrapper<T> {
    private T data;
    public static <U> Wrapper<U> create(U data) {
        return new Wrapper<>(data);
    }
}
