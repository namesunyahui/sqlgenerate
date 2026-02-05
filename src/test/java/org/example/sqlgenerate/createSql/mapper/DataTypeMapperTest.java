package org.example.sqlgenerate.createSql.mapper;

import org.example.sqlgenerate.createSql.model.ImportExcel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DataTypeMapper测试 - 数据类型映射
 */
@DisplayName("数据类型映射测试")
class DataTypeMapperTest {

    @Test
    @DisplayName("MySQL整数类型映射")
    void testMysqlIntegerTypes() {
        MysqlDataTypeMapper mapper = new MysqlDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("TINYINT");
        assertEquals("TINYINT", mapper.mapType(column));

        column.setDataType("INT");
        assertEquals("INT", mapper.mapType(column));

        column.setDataType("BIGINT");
        assertEquals("BIGINT", mapper.mapType(column));
    }

    @Test
    @DisplayName("MySQL VARCHAR类型带长度")
    void testMysqlVarcharWithLength() {
        MysqlDataTypeMapper mapper = new MysqlDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("VARCHAR");
        column.setLength("50");
        assertEquals("VARCHAR(50)", mapper.mapType(column));

        column.setLength("255");
        assertEquals("VARCHAR(255)", mapper.mapType(column));
    }

    @Test
    @DisplayName("MySQL VARCHAR类型默认长度")
    void testMysqlVarcharDefaultLength() {
        MysqlDataTypeMapper mapper = new MysqlDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("VARCHAR");
        assertEquals("VARCHAR(255)", mapper.mapType(column));
    }

    @Test
    @DisplayName("MySQL DECIMAL精度映射")
    void testMysqlDecimalPrecision() {
        MysqlDataTypeMapper mapper = new MysqlDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("DECIMAL");

        // DECIMAL(10,2) - 带精度和小数位
        column.setLength("10,2");
        assertEquals("DECIMAL(10,2)", mapper.mapType(column));

        // DECIMAL(10) - 只有精度，默认2位小数
        column.setLength("10");
        assertEquals("DECIMAL(10,2)", mapper.mapType(column));

        // DECIMAL - 默认(10,2)
        column.setLength("");
        assertEquals("DECIMAL(10,2)", mapper.mapType(column));
    }

    @Test
    @DisplayName("MySQL TEXT类型映射")
    void testMysqlTextTypes() {
        MysqlDataTypeMapper mapper = new MysqlDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("TEXT");
        assertEquals("TEXT", mapper.mapType(column));

        column.setDataType("LONGTEXT");
        assertEquals("LONGTEXT", mapper.mapType(column));
    }

    @Test
    @DisplayName("MySQL BLOB类型映射")
    void testMysqlBlobTypes() {
        MysqlDataTypeMapper mapper = new MysqlDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("BLOB");
        assertEquals("BLOB", mapper.mapType(column));

        column.setDataType("LONGBLOB");
        assertEquals("LONGBLOB", mapper.mapType(column));
    }

    @Test
    @DisplayName("MySQL日期时间类型映射")
    void testMysqlDateTimeTypes() {
        MysqlDataTypeMapper mapper = new MysqlDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("DATE");
        assertEquals("DATE", mapper.mapType(column));

        column.setDataType("DATETIME");
        assertEquals("DATETIME", mapper.mapType(column));

        column.setDataType("TIMESTAMP");
        assertEquals("TIMESTAMP", mapper.mapType(column));
    }

    @Test
    @DisplayName("MySQL JSON类型映射")
    void testMysqlJsonType() {
        MysqlDataTypeMapper mapper = new MysqlDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("JSON");
        assertEquals("JSON", mapper.mapType(column));
    }

    @Test
    @DisplayName("MySQL BOOLEAN类型")
    void testMysqlBooleanType() {
        MysqlDataTypeMapper mapper = new MysqlDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("BOOLEAN");
        assertEquals("TINYINT(1)", mapper.mapType(column));

        column.setDataType("BOOL");
        assertEquals("TINYINT(1)", mapper.mapType(column));
    }

    @Test
    @DisplayName("Oracle VARCHAR2类型映射")
    void testOracleVarchar2Mapping() {
        OracleDataTypeMapper mapper = new OracleDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("VARCHAR");
        column.setLength("50");
        assertEquals("VARCHAR2(50)", mapper.mapType(column));

        column.setDataType("VARCHAR2");
        column.setLength("100");
        assertEquals("VARCHAR2(100)", mapper.mapType(column));
    }

    @Test
    @DisplayName("Oracle NUMBER类型映射")
    void testOracleNumberMapping() {
        OracleDataTypeMapper mapper = new OracleDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("NUMBER");

        // NUMBER(10,2)
        column.setLength("10,2");
        assertEquals("NUMBER(10,2)", mapper.mapType(column));

        // NUMBER(10)
        column.setLength("10");
        assertEquals("NUMBER(10)", mapper.mapType(column));

        // NUMBER 默认
        column.setLength("");
        assertEquals("NUMBER", mapper.mapType(column));
    }

    @Test
    @DisplayName("Oracle TEXT类型映射")
    void testOracleTextMapping() {
        OracleDataTypeMapper mapper = new OracleDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("TEXT");
        // 注意：实际实现TEXT返回TEXT，策略类负责映射到CLOB
        assertEquals("TEXT", mapper.mapType(column));
    }

    @Test
    @DisplayName("Oracle CLOB类型映射")
    void testOracleClobMapping() {
        OracleDataTypeMapper mapper = new OracleDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("CLOB");
        assertEquals("CLOB", mapper.mapType(column));
    }

    @Test
    @DisplayName("Oracle NVARCHAR类型映射")
    void testOracleNvarcharMapping() {
        OracleDataTypeMapper mapper = new OracleDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("NVARCHAR");
        column.setLength("255");
        assertEquals("NVARCHAR2(255)", mapper.mapType(column));
    }

    @Test
    @DisplayName("Oracle NTEXT类型保持原样")
    void testOracleNtextMapping() {
        OracleDataTypeMapper mapper = new OracleDataTypeMapper();

        ImportExcel column = new ImportExcel();
        column.setDataType("NTEXT");
        // NTEXT不在switch中，使用default处理保持原样
        assertEquals("NTEXT", mapper.mapType(column));
    }

    @Test
    @DisplayName("MySQL默认字符集")
    void testMysqlDefaultCharset() {
        MysqlDataTypeMapper mapper = new MysqlDataTypeMapper();
        assertEquals("utf8mb4", mapper.getDefaultCharset());
        assertEquals("utf8mb4_unicode_ci", mapper.getDefaultCollation());
    }

    @Test
    @DisplayName("Oracle默认字符集为null")
    void testOracleNoCharset() {
        OracleDataTypeMapper mapper = new OracleDataTypeMapper();
        // Oracle不需要字符集设置，使用接口默认值null
        assertNull(mapper.getDefaultCharset());
        assertNull(mapper.getDefaultCollation());
    }

    @Test
    @DisplayName("支持类型检测")
    void testSupportsType() {
        MysqlDataTypeMapper mysqlMapper = new MysqlDataTypeMapper();
        OracleDataTypeMapper oracleMapper = new OracleDataTypeMapper();

        assertTrue(mysqlMapper.supportsType("VARCHAR"));
        assertTrue(mysqlMapper.supportsType("DECIMAL"));
        assertTrue(mysqlMapper.supportsType("JSON"));

        assertTrue(oracleMapper.supportsType("VARCHAR"));
        assertTrue(oracleMapper.supportsType("NUMBER"));
        assertTrue(oracleMapper.supportsType("CLOB"));
    }
}
