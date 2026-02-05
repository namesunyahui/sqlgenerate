package org.example.sqlgenerate.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 定义一个自定义异常处理器类
 */
@RestControllerAdvice
public class MyExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(MyExceptionHandler.class);

    @ExceptionHandler(MyExceptionRequest.class)
    public Map<String, Object> requestException(MyExceptionRequest e){
        Map<String, Object> result = new HashMap<>();
        result.put("code",e.getErrorCode());
        result.put("message",e.getMessage());
        return result;
    }

    /**
     * 处理SQL生成异常
     */
    @ExceptionHandler(SqlGenerationException.class)
    public Map<String, Object> handleSqlGenerationException(SqlGenerationException e, HttpServletRequest request) {
        logger.error("SQL生成异常: {} - URI: {}", e.getMessage(), request.getRequestURI(), e);

        Map<String, Object> result = new HashMap<>();
        result.put("code", e.getErrorCode());
        result.put("message", e.getMessage());
        result.put("success", false);
        return result;
    }

    @ExceptionHandler(NullPointerException.class)
    public String handleNullPointerException(HttpServletRequest request, Exception ex) {
        logger.error("空指针异常: URI: {}", request.getRequestURI(), ex);
        return ex.getMessage();
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleUnhandledException(Exception e, HttpServletRequest request) {
        logger.error("未处理的异常: {} - URI: {}", e.getMessage(), request.getRequestURI(), e);

        Map<String, Object> result = new HashMap<>();
        result.put("code", "INTERNAL_ERROR");
        result.put("message", "系统内部错误，请稍后重试");
        result.put("success", false);
        return result;
    }

}
