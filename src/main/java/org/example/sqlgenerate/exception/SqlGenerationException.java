package org.example.sqlgenerate.exception;

import lombok.Getter;

/**
 * SQL生成异常类
 * 用于SQL生成过程中的各种错误场景
 */
@Getter
public class SqlGenerationException extends RuntimeException {

    /**
     * 错误编码
     */
    private final String errorCode;

    /**
     * 错误信息
     */
    private final String message;

    /**
     * SQL注入相关错误
     */
    public static final String SQL_INJECTION_ERROR = "SQL_INJECTION_ERROR";

    /**
     * 参数校验错误
     */
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";

    /**
     * 文件处理错误
     */
    public static final String FILE_PROCESSING_ERROR = "FILE_PROCESSING_ERROR";

    /**
     * 数据解析错误
     */
    public static final String DATA_PARSING_ERROR = "DATA_PARSING_ERROR";

    public SqlGenerationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public SqlGenerationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.message = message;
    }

    /**
     * 创建SQL注入异常
     */
    public static SqlGenerationException sqlInjection(String message) {
        return new SqlGenerationException(SQL_INJECTION_ERROR, "检测到潜在的SQL注入风险: " + message);
    }

    /**
     * 创建参数校验异常
     */
    public static SqlGenerationException validation(String message) {
        return new SqlGenerationException(VALIDATION_ERROR, "参数校验失败: " + message);
    }

    /**
     * 创建文件处理异常
     */
    public static SqlGenerationException fileProcessing(String message, Throwable cause) {
        return new SqlGenerationException(FILE_PROCESSING_ERROR, "文件处理失败: " + message, cause);
    }

    /**
     * 创建数据解析异常
     */
    public static SqlGenerationException dataParsing(String message) {
        return new SqlGenerationException(DATA_PARSING_ERROR, "数据解析失败: " + message);
    }
}
