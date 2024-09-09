package com.raito.zf_demo;

import com.raito.zf_demo.application.pay.handler.PayHandler;
import com.raito.zf_demo.domain.pay.enums.PayType;
import com.raito.zf_demo.domain.user.entity.User;
import com.raito.zf_demo.infrastructure.context.LoginContext;
import com.raito.zf_demo.infrastructure.jwt.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SpringBootTest
@Slf4j
class ZfDemoApplicationTests {

    @Autowired
    private PayHandler handler;
    @Autowired
    private JwtConfig config;
    @Test
    void contextLoads() {
        LoginContext.set(new LoginInfo(1L, "raito", "raito@qq.com"));
        String code = handler.getQRCode(1L, PayType.WX_PAY.name());
    }


    @Test
    void login() throws NoSuchAlgorithmException, InvalidKeyException {
        User user = new User();
        user.setId(1L);
        user.setUsername("raito");
        user.setPassword("123456");
        user.setEmail("raito@qq.com");

        String header = JwtHeader.create();
        String payload = JwtPayload.createPayload(user, config.getTtl());
        String signature = JwtSignature.create(header, payload);
        String jwt = header + "." + payload + "." + signature;
        log.info("jwt = " + jwt);
        encoding(jwt);
        boolean validate = new JwtValidator(jwt).validate();
        log.info("validate = " + validate);
    }

    void encoding(String jwt) {
        // Split JWT into its three parts
        String[] jwtParts = jwt.split("\\.");
        if (jwtParts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        // Decode each part
        String header = new String(Base64.getUrlDecoder().decode(jwtParts[0]), StandardCharsets.UTF_8);
        String payload = new String(Base64.getUrlDecoder().decode(jwtParts[1]), StandardCharsets.UTF_8);
        String signature = jwtParts[2]; // Signature is not base64-decoded as it is binary data

        log.info("Header: " + header);
        log.info("Payload: " + payload);
        log.info("Signature: " + signature);
    }

}
