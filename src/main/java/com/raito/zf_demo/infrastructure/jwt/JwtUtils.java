package com.raito.zf_demo.infrastructure.jwt;

import com.raito.zf_demo.domain.user.entity.User;
import com.raito.zf_demo.infrastructure.exception.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author raito
 * @since 2024/09/06
 */
@Component
public class JwtUtils {
    private static final String PREFIX = "Bearer ";
    private static JwtConfig config;

    public static String createToken(User user) {
        try {
            String header = JwtHeader.create();
            String payload = JwtPayload.createPayload(user, config.getTtl());
            String signature = JwtSignature.create(header, payload);
            return PREFIX + header + "." + payload + "." + signature;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new JwtException(e);
        }
    }

    @Autowired
    public void setConfig(JwtConfig config) {
        JwtUtils.config = config;
    }
}
