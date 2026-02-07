package org.example.sqlgenerate.common.constants;

/**
 * 错误码枚举
 */
public enum ErrorCode {

    // 通用错误码 (1xxx)
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // SQL 生成模块错误码 (2xxx)
    SQL_VALIDATION_ERROR(2001, "SQL 参数校验失败"),
    SQL_INJECTION_RISK(2002, "检测到 SQL 注入风险"),
    SQL_FILE_PARSE_ERROR(2003, "Excel 文件解析失败"),
    SQL_GENERATION_ERROR(2004, "SQL 生成失败"),

    // 代码生成模块错误码 (3xxx)
    CODEGEN_JSON_PARSE_ERROR(3001, "JSON 解析失败"),
    CODEGEN_TEMPLATE_ERROR(3002, "代码模板错误"),
    CODEGEN_GENERATION_ERROR(3003, "代码生成失败"),

    // 数据处理模块错误码 (4xxx)
    DATA_FILE_FORMAT_ERROR(4001, "文件格式错误"),
    DATA_CONVERSION_ERROR(4002, "数据转换失败"),
    DATA_ENCRYPTION_ERROR(4003, "加密解密失败"),

    // 开发辅助模块错误码 (5xxx)
    DEV_API_REQUEST_ERROR(5001, "API 请求失败"),
    DEV_MOCK_GENERATE_ERROR(5002, "Mock 数据生成失败"),
    DEV_DOC_GENERATE_ERROR(5003, "文档生成失败");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
