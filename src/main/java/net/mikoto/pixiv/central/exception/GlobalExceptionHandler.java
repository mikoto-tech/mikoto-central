package net.mikoto.pixiv.central.exception;

import cn.dev33.satoken.util.SaResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author mikoto
 * Create for pixiv-central
 * Create at 2022/7/27
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public SaResult handlerException(@NotNull Exception e) {
        e.printStackTrace();
        return SaResult.error(e.getMessage());
    }
}
