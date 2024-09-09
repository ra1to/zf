package com.raito.zf_demo.infrastructure.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raito.zf_demo.infrastructure.exception.JsonException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author raito
 * @since 2024/09/09
 */
@Slf4j
public class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("json转换异常", e);
            throw new JsonException(e);
        }
    }
}
