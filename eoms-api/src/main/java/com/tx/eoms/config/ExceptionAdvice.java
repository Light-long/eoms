package com.tx.eoms.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.json.JSONObject;
import com.tx.eoms.exception.EomsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    /**
     * 500错误
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e) {
        JSONObject json = new JSONObject();
        // 处理后端验证失败产生的异常
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException exception = (MethodArgumentNotValidException) e;
            json.set("error", Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage());
            log.error("执行异常", e);
        }
        // 处理业务异常
        else if (e instanceof EomsException) {
            log.error("执行异常", e);
            EomsException exception = (EomsException) e;
            json.set("error", exception.getMsg());
        }
        // 处理其余的异常
        else {
            log.error("后端执行异常", e);
            json.set("error", "后端执行异常");
        }
        return json.toString();
    }

    /**
     * 未授权异常
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotLoginException.class)
    public String unLoginHandler(Exception e) {
        JSONObject json = new JSONObject();
        json.set("error", e.getMessage());
        return json.toString();
    }
}
