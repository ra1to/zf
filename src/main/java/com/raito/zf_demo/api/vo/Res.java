package com.raito.zf_demo.api.vo;

/**
 * @author raito
 * @since 2024/09/05
 */
public record Res<T>(Integer code, String msg, T data) {

    public static <T> Res<T> ok() {
        return new Res<>(200, "ok", null);
    }

    public static <T> Res<T> ok(T data) {
        return new Res<>(200, "ok", data);
    }

    public static <T> Res<T> ok(String message, T data) {
        return new Res<>(200, message, data);
    }

    public static Res<Void> message(String msg) {
        return new Res<>(200, msg, null);
    }



    public static <T> Res<T> fail(String msg) {
        return new Res<>(500, msg, null);
    }

    public static <T> Res<T> fail(Integer code, String msg) {
        return new Res<>(code, msg, null);
    }


    public static <T> Res<T> fail(Integer code, String msg, T data) {
        return new Res<>(code, msg, data);
    }

}