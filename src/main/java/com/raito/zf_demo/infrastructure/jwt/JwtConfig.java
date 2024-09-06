package com.raito.zf_demo.infrastructure.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author raito
 * @since 2024/09/06
 */
@ConfigurationProperties("jwt")
@Configuration
@Data
public class JwtConfig {
    private String key;

    private Long ttl = -1L;

    private String header;

    public void setKey(Resource resource) throws IOException {
        try (FileInputStream fs = new FileInputStream(resource.getFile())) {
            ByteArrayOutputStream os = new ByteArrayOutputStream(2048);
            byte[] buffer = new byte[1024];
            for (int length; (length = fs.read(buffer)) != -1; ) {
                os.write(buffer, 0, length);
            }
            key = os.toString(StandardCharsets.UTF_8);
        }
    }
}
