package com.example.springbootdemo.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author LTJ
 * @version 1.0
 * @date 2021/6/4 16:12
 */
@ControllerAdvice
@Slf4j
public class GlobalHttpExceptionHandler {

    @ExceptionHandler(value=Exception.class)
    public HttpResult exceptionHandle(Exception e){
        log.warn("统一异常处理");
        HttpResult httpResult = HttpResult.builder().code(400).description("统一异常处理").build();
        return httpResult;
    }
}
