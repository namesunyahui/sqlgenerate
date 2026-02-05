package org.example.sqlgenerate.createSql.util;

import org.example.sqlgenerate.exception.SqlGenerationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SqlValidator测试 - SQL注入检测
 */
@DisplayName("SQL注入检测测试")
class SqlValidatorTest {

    @Test
    @DisplayName("正常表名应该通过验证")
    void testValidTableName() {
        assertDoesNotThrow(() -> SqlValidator.validateTableName("user"));
        assertDoesNotThrow(() -> SqlValidator.validateTableName("account"));
        assertDoesNotThrow(() -> SqlValidator.validateTableName("t_user_2024"));
        assertDoesNotThrow(() -> SqlValidator.validateTableName("tbl_member"));
    }

    @Test
    @DisplayName("SQL关键字表名应该抛出异常")
    void testSqlKeywordTableName() {
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("select"));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("DROP"));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("InSeRT"));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("union"));
    }

    @Test
    @DisplayName("SQL注入模式应该被检测到")
    void testSqlInjectionPatterns() {
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("users' OR '1'='1"));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("users; DROP TABLE"));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("users--"));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("users#"));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("users/*"));
    }

    @Test
    @DisplayName("特殊字符应该被拒绝")
    void testSpecialCharacters() {
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("users;"));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("users'"));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("users\""));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("users|"));
    }

    @Test
    @DisplayName("空值应该抛出异常")
    void testNullTableName() {
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName(null));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName(""));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("   "));
    }

    @Test
    @DisplayName("正常列名应该通过验证")
    void testValidColumnName() {
        assertDoesNotThrow(() -> SqlValidator.validateColumnName("id"));
        assertDoesNotThrow(() -> SqlValidator.validateColumnName("username"));
        assertDoesNotThrow(() -> SqlValidator.validateColumnName("userName"));
        assertDoesNotThrow(() -> SqlValidator.validateColumnName("reg_ts"));
    }

    @Test
    @DisplayName("SQL关键字列名应该抛出异常")
    void testSqlKeywordColumnName() {
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateColumnName("select"));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateColumnName("order"));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateColumnName("group"));
    }

    @Test
    @DisplayName("字符串转义应该正确处理")
    void testEscapeStringLiteral() {
        assertEquals("test", SqlValidator.escapeStringLiteral("test"));
        assertEquals("test''s", SqlValidator.escapeStringLiteral("test's"));
        assertEquals("test''''s", SqlValidator.escapeStringLiteral("test''s"));
        // 转义换行符
        String result = SqlValidator.escapeStringLiteral("a\nb");
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
    }

    @Test
    @DisplayName("数据库类型验证")
    void testValidateDatabaseType() {
        // 支持的数据库类型
        assertDoesNotThrow(() -> SqlValidator.validateDatabaseType("mysql"));
        assertDoesNotThrow(() -> SqlValidator.validateDatabaseType("oracle"));
        assertDoesNotThrow(() -> SqlValidator.validateDatabaseType("postgresql"));
        assertDoesNotThrow(() -> SqlValidator.validateDatabaseType("sqlserver"));

        // 不支持的数据库类型
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateDatabaseType("mongodb"));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateDatabaseType(""));
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateDatabaseType(null));
    }

    @Test
    @DisplayName("包含关键字的表名应该被拒绝")
    void testTableNameWithKeywords() {
        // user_info 包含 INFO
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("user_info"));
        // create_time 包含 CREATE, TIME
        assertThrows(SqlGenerationException.class, () -> SqlValidator.validateTableName("create_time"));
    }
}
