package com.raito.zf_demo.infrastructure.jwt;

import cn.hutool.json.JSONObject;
import com.raito.zf_demo.domain.user.entity.User;

import java.util.Base64;

/**
 * @author raito
 * @since 2024/09/06
 */
public class JwtPayload {
    public static String createPayload(User user, Long ttl) {
        LoginInfo login = new LoginInfo(user.getId(), user.getUsername(), user.getEmail());
        JSONObject payload = new JSONObject();
        payload.set("login", login)
                .set("iat", System.currentTimeMillis())
                .set("ttl", ttl);
        return Base64.getUrlEncoder().encodeToString(payload.toString().getBytes());
    }
}
