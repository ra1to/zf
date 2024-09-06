package com.raito.zf_demo.infrastructure.jwt;

import cn.hutool.json.JSONObject;
import lombok.RequiredArgsConstructor;

import java.util.Base64;

/**
 * @author raito
 * @since 2024/09/06
 */
@RequiredArgsConstructor
public class JwtHeader {
    public static volatile String instance = null;
    public static String get() {
        if (instance == null) {
            synchronized (JwtHeader.class) {
                if (instance == null) {
                    instance = create();
                }
            }
        }
        return instance;
    }
    public static String create() {
        JSONObject header = new JSONObject();
        header.set("alg", "HS256");
        header.set("typ", "JWT");
        return Base64.getUrlEncoder().encodeToString(header.toString().getBytes());
    }
}
