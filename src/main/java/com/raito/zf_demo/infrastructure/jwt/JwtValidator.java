package com.raito.zf_demo.infrastructure.jwt;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.raito.zf_demo.infrastructure.validator.Validator;
import com.raito.zf_demo.infrastructure.context.LoginContext;
import com.raito.zf_demo.infrastructure.exception.ValidateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author raito
 * @since 2024/09/06
 */
@RequiredArgsConstructor
@Slf4j
public class JwtValidator implements Validator {
    private final String jwt;

    @Override
    public boolean validate() {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length != 3) {
                throw new ValidateException("JWT does not have 3 parts");
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
            String signature = parts[2];
            JSONObject object = JSONUtil.parseObj(payload);
            // 校验时间是否过期
            Long ttl = object.get("ttl", Long.class);
            if (ttl != -1) {
                Long iat = object.get("iat", Long.class);
                if (System.currentTimeMillis() - iat > ttl * 1000) {
                    throw new ValidateException("JWT is expired");
                }
            }
            // 校验签名
            String sign = JwtSignature.create(parts[0], parts[1]);
            boolean result = signature.equals(sign);
            if (result) {
                LoginContext.set(object.get("login", LoginInfo.class));
            }
            return result;
        } catch (NoSuchAlgorithmException | InvalidKeyException | ValidateException e) {
            log.error("JWT校验失败", e);
            return false;
        }
    }
}
