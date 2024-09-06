package com.raito.zf_demo.api.comment;

import com.raito.zf_demo.api.vo.Res;
import com.raito.zf_demo.infrastructure.exception.NotFoundException;
import com.raito.zf_demo.infrastructure.exception.RemoteException;
import com.raito.zf_demo.infrastructure.exception.ValidateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author raito
 * @since 2024/09/06
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionProcessor {
    @ExceptionHandler(Exception.class)
    public Res<Void> exception(Exception e) {
        log.error("系统异常", e);
        return Res.fail(500, "系统异常");
    }

    @ExceptionHandler({ValidateException.class, IllegalArgumentException.class})
    public Res<Void> validateException(Exception e) {
        log.error("校验异常", e);
        return Res.fail(500, e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public Res<Void> notFound(NotFoundException e) {
        log.error("未找到资源", e);
        return Res.fail(404, e.getMessage());
    }

    @ExceptionHandler(RemoteException.class)
    public Res<Void> remote(RemoteException e) {
        log.error("远程调用异常", e);
        return Res.fail(500, e.getMessage());
    }
}
