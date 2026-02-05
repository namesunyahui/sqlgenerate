package org.example.sqlgenerate.createSql.mapper;

import org.apache.commons.lang.StringUtils;
import org.example.sqlgenerate.createSql.model.ImportExcel;

/**
 * PostgreSQL数据类型映射器
 */
public class PostgreSqlDataTypeMapper implements DataTypeMapper {

    @Override
    public String mapType(ImportExcel column) {
        String dataType = column.getDataType().toUpperCase().trim();
        String length = column.getLength();

        return switch (dataType) {
            // 整数类型
            case "TINYINT", "SMALLINT" -> "SMALLINT";
            case "INT", "INTEGER" -> "INTEGER";
            case "BIGINT" -> "BIGINT";
            case "TINYINT UNSIGNED" -> "SMALLINT";
            case "INT UNSIGNED", "INTEGER UNSIGNED" -> "BIGINT";
            case "BIGINT UNSIGNED" -> "NUMERIC(20)";

            // 浮点类型
            case "FLOAT" -> mapWithLength("REAL", length, "");
            case "DOUBLE" -> "DOUBLE PRECISION";
            case "DECIMAL", "NUMERIC" -> mapDecimal(length);

            // 字符串类型
            case "CHAR" -> "CHAR(" + getDefaultLength(length, "1") + ")";
            case "VARCHAR" -> "VARCHAR(" + getDefaultLength(length, "255") + ")";
            case "TEXT" -> "TEXT";
            case "MEDIUMTEXT", "LONGTEXT", "TINYTEXT" -> "TEXT";

            // 二进制类型
            case "BINARY", "VARBINARY" -> "BYTEA";
            case "BLOB", "TINYBLOB", "MEDIUMBLOB", "LONGBLOB" -> "BYTEA";

            // 日期时间类型
            case "DATE" -> "DATE";
            case "TIME" -> "TIME";
            case "DATETIME", "TIMESTAMP" -> "TIMESTAMP";
            case "YEAR" -> "INTEGER";

            // 布尔类型
            case "BOOLEAN", "BOOL" -> "BOOLEAN";

            // JSON类型
            case "JSON" -> "JSONB";

            // 枚举和集合 - PostgreSQL使用ENUM或TEXT
            case "ENUM" -> "VARCHAR(255)";
            case "SET" -> "TEXT";

            // 默认处理
            default -> {
                if (StringUtils.isNotEmpty(length)) {
                    yield dataType + "(" + length + ")";
                }
                yield dataType;
            }
        };
    }

    @Override
    public boolean supportsType(String dataType) {
        if (dataType == null) return false;
        String upper = dataType.toUpperCase();
        return upper.matches("(TINYINT|SMALLINT|INT|INTEGER|BIGINT|FLOAT|DOUBLE|" +
                "DECIMAL|NUMERIC|CHAR|VARCHAR|TEXT|MEDIUMTEXT|LONGTEXT|TINYTEXT|" +
                "BINARY|VARBINARY|BLOB|TINYBLOB|MEDIUMBLOB|LONGBLOB|" +
                "DATE|TIME|DATETIME|TIMESTAMP|YEAR|BOOLEAN|BOOL|JSON|ENUM|SET|" +
                "UNSIGNED|REAL)");
    }

    /**
     * 映射带长度的类型
     */
    private String mapWithLength(String type, String length, String defaultLength) {
        if (StringUtils.isNotEmpty(length)) {
            return type + "(" + length + ")";
        }
        if (StringUtils.isNotEmpty(defaultLength)) {
            return type + "(" + defaultLength + ")";
        }
        return type;
    }

    /**
     * 映射DECIMAL类型，支持精度和小数位
     */
    private String mapDecimal(String length) {
        if (StringUtils.isEmpty(length)) {
            return "NUMERIC(10,2)";
        }
        if (!length.contains(",")) {
            return "NUMERIC(" + length + ",2)";
        }
        return "NUMERIC(" + length + ")";
    }

    /**
     * 获取默认长度
     */
    private String getDefaultLength(String length, String defaultValue) {
        return StringUtils.isNotEmpty(length) ? length : defaultValue;
    }
}
