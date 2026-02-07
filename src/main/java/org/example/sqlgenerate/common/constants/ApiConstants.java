package org.example.sqlgenerate.common.constants;

/**
 * API 常量定义
 */
public class ApiConstants {

    /**
     * API 路径前缀
     */
    public static final String API_PREFIX = "/api";

    /**
     * SQL 生成模块前缀
     */
    public static final String SQL_PREFIX = API_PREFIX + "/sql";

    /**
     * 代码生成模块前缀
     */
    public static final String CODEGEN_PREFIX = API_PREFIX + "/codegen";

    /**
     * 数据处理模块前缀
     */
    public static final String DATA_PREFIX = API_PREFIX + "/data";

    /**
     * 开发辅助模块前缀
     */
    public static final String DEV_PREFIX = API_PREFIX + "/dev";

    /**
     * 公共模块前缀
     */
    public static final String COMMON_PREFIX = API_PREFIX + "/common";

    private ApiConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
