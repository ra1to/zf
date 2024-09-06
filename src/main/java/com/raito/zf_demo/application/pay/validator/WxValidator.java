package com.raito.zf_demo.application.pay.validator;

import cn.hutool.json.JSONObject;
import com.raito.zf_demo.infrastructure.Validator;
import com.raito.zf_demo.infrastructure.exception.ValidateException;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.Instant;

import static com.wechat.pay.contrib.apache.httpclient.constant.WechatPayHttpHeaders.*;

/**
 * @author raito
 * @since 2024/09/06
 */
@RequiredArgsConstructor
public class WxValidator implements Validator {

    protected final JSONObject obj;
    protected final HttpServletRequest request;
    protected final Verifier verifier;
    protected static final long RESPONSE_EXPIRED_MINUTES = 5;

    @Override
    public boolean validate() throws ValidateException {
        validateParameters(request);
        String message = buildMessage(request);
        String serial = request.getHeader(WECHAT_PAY_SERIAL);
        String signature = request.getHeader(WECHAT_PAY_SIGNATURE);
        if (!verifier.verify(serial, message.getBytes(StandardCharsets.UTF_8), signature)) {
            throw new ValidateException("serial=[%s] message=[%s] sign=[%s], request-id=[%s]",
                    serial, message, signature, obj.get("id"));
        }
        return true;
    }

    protected final String buildMessage(HttpServletRequest request) {
        String timestamp = request.getHeader(WECHAT_PAY_TIMESTAMP);
        String nonce = request.getHeader(WECHAT_PAY_NONCE);
        return timestamp + "\n" +
                nonce + "\n" +
                obj + "\n";
    }

    protected final void validateParameters(HttpServletRequest request) {
        // NOTE: ensure HEADER_WECHAT_PAY_TIMESTAMP at last
        String[] headers = {WECHAT_PAY_SERIAL, WECHAT_PAY_SIGNATURE, WECHAT_PAY_NONCE, WECHAT_PAY_TIMESTAMP};

        String header = null;
        for (String headerName : headers) {
            header = request.getHeader(headerName);
            if (header == null) {
                throw new ValidateException("empty [%s], request-id=[%s]", headerName, obj.get("id"));
            }
        }
        //判断请求是否过期
        String timestampStr = header;
        try {
            Instant responseTime = Instant.ofEpochSecond(Long.parseLong(timestampStr));
            // 拒绝过期请求
            if (Duration.between(responseTime, Instant.now()).abs().toMinutes() >= RESPONSE_EXPIRED_MINUTES) {
                throw new ValidateException("timestamp=[%s] expires, request-id=[%s]", timestampStr, obj.get("id"));
            }
        } catch (DateTimeException | NumberFormatException e) {
            throw new ValidateException("invalid timestamp=[%s], request-id=[%s]", timestampStr, obj.get("id"));
        }
    }
}
