package com.raito.zf_demo.infrastructure.context;

import com.raito.zf_demo.infrastructure.exception.NotFoundException;
import com.raito.zf_demo.infrastructure.jwt.LoginInfo;

/**
 * @author raito
 * @since 2024/09/05
 */
public class LoginContext {

    private static final ThreadLocal<LoginInfo> local = new ThreadLocal<>();

    public static LoginInfo get() {
        try {
            return local.get();
        } catch (Exception e) {
            throw new NotFoundException("请先登录！");
        }
    }

    public static void set(LoginInfo info) {
        local.set(info);
    }

    public static void clear() {
        local.remove();
    }

    public static Long getUserId() {
        return get().getUserId();
    }

    public static String getUserName() {
        return get().getUsername();
    }

}