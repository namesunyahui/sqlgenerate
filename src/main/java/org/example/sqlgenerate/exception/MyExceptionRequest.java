package org.example.sqlgenerate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 自定义异常类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MyExceptionRequest  extends RuntimeException {

    /**
     * 错误编码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String message;


}
