package org.example.sqlgenerate.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 定义一个自定义异常处理器类
 */
@RestControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(MyExceptionRequest.class)
    public Map<String, Object> requestException(MyExceptionRequest e){
        Map<String, Object> result = new HashMap<>();
        result.put("code",e.getErrorCode());
        result.put("message",e.getMessage());
        return result;
    }

    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException(HttpServletRequest request, Exception ex) {
        return ex.getMessage();
    }

}
