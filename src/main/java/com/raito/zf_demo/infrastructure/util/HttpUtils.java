package com.raito.zf_demo.infrastructure.util;

import com.raito.zf_demo.infrastructure.exception.IoReaderException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author raito
 * @since 2024/09/06
 */
@Slf4j
public class HttpUtils {
    public static String read(HttpServletRequest request) {
        try (BufferedReader br = request.getReader()) {
            StringBuilder result = new StringBuilder();
            for (String line; (line = br.readLine()) != null; ) {
                if (!result.isEmpty()) {
                    result.append("\n");
                }
                result.append(line);
            }
            String body = result.toString();
            log.debug("request:{}, 请求体:{}", request, body);
            return body;
        } catch (IOException e) {
            throw new IoReaderException(e);
        }
    }
}
