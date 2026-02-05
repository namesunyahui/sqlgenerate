package org.example.sqlgenerate.createSql.util;

import org.apache.commons.lang.StringUtils;
import org.example.sqlgenerate.createSql.codm.DatabaseType;
import org.example.sqlgenerate.exception.SqlGenerationException;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * SQL安全校验工具类
 * 用于防止SQL注入和验证SQL标识符的合法性
 */
public class SqlValidator {

    /**
     * SQL关键字列表（不区分大小写）
     */
    private static final List<String> SQL_KEYWORDS = Arrays.asList(
            "SELECT", "INSERT", "UPDATE", "DELETE", "DROP", "CREATE", "ALTER",
            "TRUNCATE", "EXEC", "EXECUTE", "UNION", "SCRIPT", "JAVASCRIPT",
            "DECLARE", "CAST", "CONVERT", "RETURN", "SET", "WHERE", "FROM",
            "JOIN", "INNER", "OUTER", "LEFT", "RIGHT", "ON", "AS", "OR", "AND",
            "NOT", "IN", "LIKE", "BETWEEN", "IS", "NULL", "ORDER", "BY", "GROUP",
            "HAVING", "LIMIT", "OFFSET", "DISTINCT", "EXISTS", "CASE", "WHEN",
            "THEN", "ELSE", "END", "IF", "WHILE", "BEGIN", "COMMIT", "ROLLBACK",
            "TRANSACTION", "GRANT", "REVOKE", "INDEX", "VIEW", "TABLE", "DATABASE",
            "SCHEMA", "FUNCTION", "PROCEDURE", "TRIGGER", "CONSTRAINT", "PRIMARY",
            "FOREIGN", "KEY", "REFERENCES", "UNIQUE", "CHECK", "DEFAULT", "CASCADE",
            "RESTRICT", "NO", "ACTION", "SET", "CURRENT", "TIME", "DATE", "VALUES"
    );

    /**
     * SQL标识符合法字符模式（字母、数字、下划线，必须以字母或下划线开头）
     */
    private static final Pattern VALID_IDENTIFIER_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

    /**
     * 表名最大长度限制
     */
    private static final int MAX_TABLE_NAME_LENGTH = 128;

    /**
     * 字段名最大长度限制
     */
    private static final int MAX_COLUMN_NAME_LENGTH = 128;

    /**
     * 注释最大长度限制
     */
    private static final int MAX_COMMENT_LENGTH = 2000;

    /**
     * 校验表名
     *
     * @param tableName 表名
     * @throws SqlGenerationException 如果表名不合法
     */
    public static void validateTableName(String tableName) {
        if (StringUtils.isEmpty(tableName)) {
            throw SqlGenerationException.validation("表名不能为空");
        }

        if (tableName.length() > MAX_TABLE_NAME_LENGTH) {
            throw SqlGenerationException.validation("表名长度不能超过 " + MAX_TABLE_NAME_LENGTH + " 个字符");
        }

        // 检查是否包含SQL关键字
        String upperTableName = tableName.toUpperCase();
        for (String keyword : SQL_KEYWORDS) {
            if (upperTableName.contains(keyword)) {
                throw SqlGenerationException.sqlInjection("表名包含SQL关键字: " + tableName);
            }
        }

        // 检查特殊字符模式
        if (containsSqlInjectionPatterns(tableName)) {
            throw SqlGenerationException.sqlInjection("表名包含非法字符: " + tableName);
        }

        // 检查标识符格式
        if (!VALID_IDENTIFIER_PATTERN.matcher(tableName).matches()) {
            throw SqlGenerationException.validation("表名只能包含字母、数字和下划线，且必须以字母或下划线开头");
        }
    }

    /**
     * 校验字段名
     *
     * @param columnName 字段名
     * @throws SqlGenerationException 如果字段名不合法
     */
    public static void validateColumnName(String columnName) {
        if (StringUtils.isEmpty(columnName)) {
            throw SqlGenerationException.validation("字段名不能为空");
        }

        if (columnName.length() > MAX_COLUMN_NAME_LENGTH) {
            throw SqlGenerationException.validation("字段名长度不能超过 " + MAX_COLUMN_NAME_LENGTH + " 个字符");
        }

        // 检查是否包含SQL关键字
        String upperColumnName = columnName.toUpperCase();
        for (String keyword : SQL_KEYWORDS) {
            if (upperColumnName.contains(keyword)) {
                throw SqlGenerationException.sqlInjection("字段名包含SQL关键字: " + columnName);
            }
        }

        // 检查特殊字符模式
        if (containsSqlInjectionPatterns(columnName)) {
            throw SqlGenerationException.sqlInjection("字段名包含非法字符: " + columnName);
        }

        // 检查标识符格式
        if (!VALID_IDENTIFIER_PATTERN.matcher(columnName).matches()) {
            throw SqlGenerationException.validation("字段名只能包含字母、数字和下划线，且必须以字母或下划线开头");
        }
    }

    /**
     * 校验注释内容
     *
     * @param comment 注释内容
     * @throws SqlGenerationException 如果注释不合法
     */
    public static void validateComment(String comment) {
        if (StringUtils.isEmpty(comment)) {
            return;
        }

        if (comment.length() > MAX_COMMENT_LENGTH) {
            throw SqlGenerationException.validation("注释长度不能超过 " + MAX_COMMENT_LENGTH + " 个字符");
        }

        // 检查SQL注入模式
        if (containsSqlInjectionPatterns(comment)) {
            throw SqlGenerationException.sqlInjection("注释包含非法字符: " + comment);
        }

        // 转义单引号
        comment = comment.replace("'", "''");
    }

    /**
     * 转义SQL标识符（为安全起见，虽然我们已做了校验）
     * 对于MySQL和Oracle，使用双引号包围标识符
     *
     * @param identifier 标识符
     * @return 转义后的标识符
     */
    public static String escapeIdentifier(String identifier) {
        // 转义内部的双引号
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    /**
     * 转义SQL字符串字面量
     *
     * @param value 字符串值
     * @return 转义后的字符串
     */
    public static String escapeStringLiteral(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        // 转义单引号
        return value.replace("'", "''");
    }

    /**
     * 检测字符串是否包含SQL注入模式
     *
     * @param input 输入字符串
     * @return 如果包含注入模式返回true
     */
    private static boolean containsSqlInjectionPatterns(String input) {
        if (StringUtils.isEmpty(input)) {
            return false;
        }

        String lowerInput = input.toLowerCase();

        // 检测常见的SQL注入模式
        String[] injectionPatterns = {
                "--",           // SQL注释
                "/*",           // 多行注释开始
                "*/",           // 多行注释结束
                ";",            // 语句分隔符
                "xp_",          // SQL Server扩展存储过程
                "sp_",          // SQL Server系统存储过程
                "exec(",        // 执行命令
                "execute(",     // 执行命令
                "script>",      // 脚本标签
                "<script",      // 脚本标签
                "javascript:",  // JavaScript协议
                "onerror=",     // 错误事件
                "onload=",      // 加载事件
                "onclick=",     // 点击事件
                "' or '1'='1",  // 经典的注入
                "' or 1=1--",   // 经典的注入
                "' union ",     // 联合查询注入
                "' drop ",      // 删除表注入
                "1=1",          // 永真条件
                "waitfor delay", // 时间延迟注入
                "sleep(",       // 时间延迟注入
                "benchmark(",   // MySQL时间注入
                "dbms_pipe",    // Oracle注入
                "utl_http",     // Oracle注入
                "ctxsys"        // Oracle注入
        };

        for (String pattern : injectionPatterns) {
            if (lowerInput.contains(pattern)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 校验数据库类型
     *
     * @param databaseType 数据库类型代码
     * @throws SqlGenerationException 如果数据库类型不支持
     */
    public static void validateDatabaseType(String databaseType) {
        if (StringUtils.isEmpty(databaseType)) {
            throw SqlGenerationException.validation("数据库类型不能为空");
        }

        if (!DatabaseType.isValid(databaseType)) {
            throw SqlGenerationException.validation("不支持的数据库类型: " + databaseType);
        }
    }

    /**
     * 校验文件ID
     *
     * @param fileId 文件ID
     * @throws SqlGenerationException 如果文件ID无效
     */
    public static void validateFileId(String fileId) {
        if (StringUtils.isEmpty(fileId)) {
            throw SqlGenerationException.validation("文件ID不能为空");
        }
    }

    /**
     * 批量校验字段列表
     *
     * @param columns 字段列表
     * @throws SqlGenerationException 如果有字段不合法
     */
    public static void validateColumns(java.util.List<?> columns) {
        if (columns == null || columns.isEmpty()) {
            throw SqlGenerationException.dataParsing("Excel中没有找到列定义数据");
        }
    }
}
