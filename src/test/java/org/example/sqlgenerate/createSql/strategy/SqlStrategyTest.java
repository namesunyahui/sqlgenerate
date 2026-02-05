package org.example.sqlgenerate.createSql.strategy;

import org.example.sqlgenerate.createSql.model.CreateFrom;
import org.example.sqlgenerate.createSql.model.ImportExcel;
import org.example.sqlgenerate.createSql.strategy.Impl.MysqlSqlStrategy;
import org.example.sqlgenerate.createSql.strategy.Impl.OracleSqlStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SQL策略测试 - MySQL和Oracle
 */
@DisplayName("SQL策略测试")
class SqlStrategyTest {

    private CreateFrom createFrom;
    private List<ImportExcel> columns;

    @BeforeEach
    void setUp() {
        createFrom = new CreateFrom();
        createFrom.setTableName("t_user");
        createFrom.setTableRemark("用户表");

        columns = new ArrayList<>();

        // ID
        ImportExcel id = new ImportExcel();
        id.setFiledName("id");
        id.setDataType("BIGINT");
        id.setNotNull("Y");
        id.setKey("Y");
        id.setRemark("主键ID");
        columns.add(id);

        // 用户名
        ImportExcel userName = new ImportExcel();
        userName.setFiledName("username");
        userName.setDataType("VARCHAR");
        userName.setLength("50");
        userName.setNotNull("Y");
        userName.setRemark("用户名");
        columns.add(userName);

        // 余额
        ImportExcel balance = new ImportExcel();
        balance.setFiledName("balance");
        balance.setDataType("DECIMAL");
        balance.setLength("10,2");
        balance.setNotNull("Y");
        balance.setDefaultValue("0.00");
        balance.setRemark("账户余额");
        columns.add(balance);

        // 创建时间
        ImportExcel createTime = new ImportExcel();
        createTime.setFiledName("reg_date");
        createTime.setDataType("DATETIME");
        createTime.setRemark("注册日期");
        columns.add(createTime);

        // 描述
        ImportExcel description = new ImportExcel();
        description.setFiledName("description");
        description.setDataType("TEXT");
        description.setRemark("详细描述");
        columns.add(description);
    }

    @Test
    @DisplayName("MySQL CREATE TABLE语句生成")
    void testMysqlCreateTable() {
        MysqlSqlStrategy strategy = new MysqlSqlStrategy();
        String sql = strategy.generateCreateTableSql(createFrom, columns);

        // 打印SQL以便调试
        System.out.println("=== Generated MySQL SQL ===");
        System.out.println(sql);

        assertNotNull(sql);
        assertTrue(sql.contains("DROP TABLE IF EXISTS `t_user`"));
        assertTrue(sql.contains("CREATE TABLE `t_user`"));
        assertTrue(sql.contains("PRIMARY KEY (`id`)"));
        assertTrue(sql.contains("`id` BIGINT NOT NULL COMMENT '主键ID'"));
        assertTrue(sql.contains("`username` VARCHAR(50) NOT NULL COMMENT '用户名'"));
        assertTrue(sql.contains("`balance` DECIMAL(10,2) NOT NULL DEFAULT '0.00' COMMENT '账户余额'"));
        assertTrue(sql.contains("`reg_date` DATETIME COMMENT '注册日期'"));
        assertTrue(sql.contains("`description` TEXT COMMENT '详细描述'"));
        assertTrue(sql.contains("DEFAULT CHARSET=utf8mb4"));
        assertTrue(sql.contains("COLLATE=utf8mb4_unicode_ci"));
    }

    @Test
    @DisplayName("MySQL表注释生成")
    void testMysqlTableComment() {
        MysqlSqlStrategy strategy = new MysqlSqlStrategy();
        String sql = strategy.generateTableCommentSql(createFrom);

        assertNotNull(sql);
        assertTrue(sql.contains("ALTER TABLE `t_user` COMMENT '用户表'"));
    }

    @Test
    @DisplayName("MySQL列注释在CREATE TABLE中")
    void testMysqlColumnComments() {
        MysqlSqlStrategy strategy = new MysqlSqlStrategy();
        // MySQL列注释包含在CREATE TABLE中
        String sql = strategy.generateColumnCommentsSql(createFrom, columns);
        assertEquals("", sql); // 返回空字符串，因为注释已在CREATE TABLE中
    }

    @Test
    @DisplayName("MySQL标识符引号")
    void testMysqlQuoteIdentifier() {
        MysqlSqlStrategy strategy = new MysqlSqlStrategy();
        assertEquals("`table_name`", strategy.quoteIdentifier("table_name"));
        assertEquals("`column`", strategy.quoteIdentifier("column"));
    }

    @Test
    @DisplayName("Oracle CREATE TABLE语句生成")
    void testOracleCreateTable() {
        OracleSqlStrategy strategy = new OracleSqlStrategy();
        String sql = strategy.generateCreateTableSql(createFrom, columns);

        // 打印SQL以便调试
        System.out.println("=== Generated Oracle SQL ===");
        System.out.println(sql);

        assertNotNull(sql);
        assertTrue(sql.contains("CREATE TABLE \"t_user\""));
        assertTrue(sql.contains("PRIMARY KEY (\"id\")"));
        assertTrue(sql.contains("\"id\" INTEGER NOT NULL"));
        assertTrue(sql.contains("\"username\" VARCHAR2(50) NOT NULL"));
        assertTrue(sql.contains("\"balance\" NUMBER(10,2) NOT NULL DEFAULT '0.00'"));
        assertTrue(sql.contains("\"reg_date\" DATE"));
        assertTrue(sql.contains("\"description\" TEXT"));
    }

    @Test
    @DisplayName("Oracle TEXT保持原样")
    void testOracleTextKeptAsIs() {
        OracleSqlStrategy strategy = new OracleSqlStrategy();

        List<ImportExcel> textColumn = new ArrayList<>();
        ImportExcel col = new ImportExcel();
        col.setFiledName("content");
        col.setDataType("TEXT");
        textColumn.add(col);

        String sql = strategy.generateCreateTableSql(createFrom, textColumn);
        // Oracle策略保持TEXT类型，实际映射可在后续处理
        assertTrue(sql.contains("TEXT"));
    }

    @Test
    @DisplayName("Oracle表注释生成")
    void testOracleTableComment() {
        OracleSqlStrategy strategy = new OracleSqlStrategy();
        String sql = strategy.generateTableCommentSql(createFrom);

        assertNotNull(sql);
        assertTrue(sql.contains("COMMENT ON TABLE \"t_user\" IS '用户表'"));
    }

    @Test
    @DisplayName("Oracle列注释生成")
    void testOracleColumnComments() {
        OracleSqlStrategy strategy = new OracleSqlStrategy();
        String sql = strategy.generateColumnCommentsSql(createFrom, columns);

        assertNotNull(sql);
        assertTrue(sql.contains("COMMENT ON COLUMN \"t_user\".\"id\" IS '主键ID'"));
        assertTrue(sql.contains("COMMENT ON COLUMN \"t_user\".\"username\" IS '用户名'"));
    }

    @Test
    @DisplayName("Oracle标识符引号")
    void testOracleQuoteIdentifier() {
        OracleSqlStrategy strategy = new OracleSqlStrategy();
        assertEquals("\"table_name\"", strategy.quoteIdentifier("table_name"));
        assertEquals("\"column\"", strategy.quoteIdentifier("column"));
    }

    @Test
    @DisplayName("无主键表生成")
    void testTableWithoutPrimaryKey() {
        List<ImportExcel> noPkColumns = new ArrayList<>();

        ImportExcel col = new ImportExcel();
        col.setFiledName("name");
        col.setDataType("VARCHAR");
        col.setLength("100");
        noPkColumns.add(col);

        MysqlSqlStrategy strategy = new MysqlSqlStrategy();
        String sql = strategy.generateCreateTableSql(createFrom, noPkColumns);

        assertNotNull(sql);
        assertFalse(sql.contains("PRIMARY KEY"));
    }

    @Test
    @DisplayName("多列主键生成")
    void testMultiColumnPrimaryKey() {
        List<ImportExcel> multiPkColumns = new ArrayList<>();

        ImportExcel col1 = new ImportExcel();
        col1.setFiledName("id1");
        col1.setDataType("BIGINT");
        col1.setKey("Y");
        multiPkColumns.add(col1);

        ImportExcel col2 = new ImportExcel();
        col2.setFiledName("id2");
        col2.setDataType("BIGINT");
        col2.setKey("Y");
        multiPkColumns.add(col2);

        MysqlSqlStrategy strategy = new MysqlSqlStrategy();
        String sql = strategy.generateCreateTableSql(createFrom, multiPkColumns);

        assertNotNull(sql);
        assertTrue(sql.contains("PRIMARY KEY (`id1`, `id2`)"));
    }

    @Test
    @DisplayName("策略支持数据库类型")
    void testSupportedDatabaseType() {
        MysqlSqlStrategy mysqlStrategy = new MysqlSqlStrategy();
        OracleSqlStrategy oracleStrategy = new OracleSqlStrategy();

        assertEquals("mysql", mysqlStrategy.getSupportedDatabaseType());
        assertEquals("oracle", oracleStrategy.getSupportedDatabaseType());
    }

    @Test
    @DisplayName("MySQL BOOLEAN类型")
    void testMysqlBooleanType() {
        MysqlSqlStrategy strategy = new MysqlSqlStrategy();

        List<ImportExcel> boolColumn = new ArrayList<>();
        ImportExcel col = new ImportExcel();
        col.setFiledName("is_active");
        col.setDataType("BOOLEAN");
        boolColumn.add(col);

        String sql = strategy.generateCreateTableSql(createFrom, boolColumn);
        assertTrue(sql.contains("TINYINT(1)"));
    }

    @Test
    @DisplayName("MySQL JSON类型")
    void testMysqlJsonType() {
        MysqlSqlStrategy strategy = new MysqlSqlStrategy();

        List<ImportExcel> jsonColumn = new ArrayList<>();
        ImportExcel col = new ImportExcel();
        col.setFiledName("config");
        col.setDataType("JSON");
        jsonColumn.add(col);

        String sql = strategy.generateCreateTableSql(createFrom, jsonColumn);
        assertTrue(sql.contains("JSON"));
    }

    @Test
    @DisplayName("Oracle BLOB类型")
    void testOracleBlobType() {
        OracleSqlStrategy strategy = new OracleSqlStrategy();

        List<ImportExcel> blobColumn = new ArrayList<>();
        ImportExcel col = new ImportExcel();
        col.setFiledName("avatar");
        col.setDataType("BLOB");
        blobColumn.add(col);

        String sql = strategy.generateCreateTableSql(createFrom, blobColumn);
        assertTrue(sql.contains("BLOB"));
    }
}
