package com.raito.zf_demo.infrastructure.util;

import java.util.Arrays;

/**
 * @author raito
 * @since 2024/09/05
 */
public interface EnumUtils {

    static <T extends Enum<T> & EnumUtils> T getEnumByType(String type, Class<T> clazz) {
        return Arrays.stream(clazz.getEnumConstants()).filter(t -> t.getType().equals(type)).findFirst().orElse(null);
    }

    static <T extends Enum<T> & EnumUtils> T getEnumByName(String name, Class<T> clazz) {
        return Arrays.stream(clazz.getEnumConstants()).filter(t -> t.name().equals(name)).findFirst().orElse(null);
    }


    default String getType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
