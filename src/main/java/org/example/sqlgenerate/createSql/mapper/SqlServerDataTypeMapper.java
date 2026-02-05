package org.example.sqlgenerate.createSql.mapper;

import org.apache.commons.lang.StringUtils;
import org.example.sqlgenerate.createSql.model.ImportExcel;

/**
 * SQL Server数据类型映射器
 */
public class SqlServerDataTypeMapper implements DataTypeMapper {

    @Override
    public String mapType(ImportExcel column) {
        String dataType = column.getDataType().toUpperCase().trim();
        String length = column.getLength();

        return switch (dataType) {
            // 整数类型
            case "TINYINT" -> "TINYINT";
            case "SMALLINT" -> "SMALLINT";
            case "INT", "INTEGER" -> "INT";
            case "BIGINT" -> "BIGINT";

            // 浮点类型
            case "FLOAT" -> mapWithLength("FLOAT", length, "53");
            case "DOUBLE" -> "FLOAT(53)";
            case "DECIMAL", "NUMERIC" -> mapDecimal(length);

            // 字符串类型
            case "CHAR" -> "CHAR(" + getDefaultLength(length, "10") + ")";
            case "VARCHAR" -> "VARCHAR(" + getDefaultLength(length, "50") + ")";
            case "TEXT", "MEDIUMTEXT", "LONGTEXT", "TINYTEXT" -> "VARCHAR(MAX)";

            // 二进制类型
            case "BINARY" -> "BINARY(" + getDefaultLength(length, "10") + ")";
            case "VARBINARY" -> "VARBINARY(" + getDefaultLength(length, "50") + ")";
            case "BLOB", "TINYBLOB", "MEDIUMBLOB", "LONGBLOB" -> "VARBINARY(MAX)";

            // 日期时间类型
            case "DATE" -> "DATE";
            case "TIME" -> "TIME";
            case "DATETIME" -> "DATETIME2";
            case "TIMESTAMP" -> "DATETIME2";
            case "YEAR" -> "INT";

            // 布尔类型
            case "BOOLEAN", "BOOL" -> "BIT";

            // JSON类型 - SQL Server使用NVARCHAR(MAX)
            case "JSON" -> "NVARCHAR(MAX)";

            // 枚举和集合
            case "ENUM" -> "VARCHAR(255)";
            case "SET" -> "VARCHAR(MAX)";

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
                "DATE|TIME|DATETIME|TIMESTAMP|YEAR|BOOLEAN|BOOL|JSON|ENUM|SET)");
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
            return "DECIMAL(10,2)";
        }
        if (!length.contains(",")) {
            return "DECIMAL(" + length + ",2)";
        }
        return "DECIMAL(" + length + ")";
    }

    /**
     * 获取默认长度
     */
    private String getDefaultLength(String length, String defaultValue) {
        return StringUtils.isNotEmpty(length) ? length : defaultValue;
    }
}
