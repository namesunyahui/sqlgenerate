package org.example.sqlgenerate.createSql.mapper;

import org.apache.commons.lang.StringUtils;
import org.example.sqlgenerate.createSql.model.ImportExcel;

/**
 * MySQL数据类型映射器
 */
public class MysqlDataTypeMapper implements DataTypeMapper {

    @Override
    public String mapType(ImportExcel column) {
        String dataType = column.getDataType().toUpperCase().trim();
        String length = column.getLength();

        return switch (dataType) {
            // 整数类型
            case "TINYINT" -> mapWithLength(dataType, length, "");
            case "SMALLINT" -> mapWithLength(dataType, length, "");
            case "INT", "INTEGER" -> mapWithLength("INT", length, "");
            case "BIGINT" -> mapWithLength("BIGINT", length, "");

            // 浮点类型
            case "FLOAT" -> mapWithLength("FLOAT", length, "");
            case "DOUBLE" -> mapWithLength("DOUBLE", length, "");
            case "DECIMAL", "NUMERIC" -> mapDecimal(length);

            // 字符串类型
            case "CHAR" -> "CHAR(" + getDefaultLength(length, "1") + ")";
            case "VARCHAR" -> "VARCHAR(" + getDefaultLength(length, "255") + ")";
            case "TEXT" -> "TEXT";
            case "MEDIUMTEXT" -> "MEDIUMTEXT";
            case "LONGTEXT" -> "LONGTEXT";
            case "TINYTEXT" -> "TINYTEXT";

            // 二进制类型
            case "BINARY" -> "BINARY(" + getDefaultLength(length, "1") + ")";
            case "VARBINARY" -> "VARBINARY(" + getDefaultLength(length, "255") + ")";
            case "BLOB" -> "BLOB";
            case "TINYBLOB" -> "TINYBLOB";
            case "MEDIUMBLOB" -> "MEDIUMBLOB";
            case "LONGBLOB" -> "LONGBLOB";

            // 日期时间类型
            case "DATE" -> "DATE";
            case "TIME" -> "TIME";
            case "DATETIME" -> "DATETIME";
            case "TIMESTAMP" -> "TIMESTAMP";
            case "YEAR" -> "YEAR";

            // 布尔类型
            case "BOOLEAN", "BOOL" -> "TINYINT(1)";

            // JSON类型
            case "JSON" -> "JSON";

            // 枚举和集合
            case "ENUM" -> "ENUM(" + length + ")";
            case "SET" -> "SET(" + length + ")";

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

    @Override
    public String getDefaultCharset() {
        return "utf8mb4";
    }

    @Override
    public String getDefaultCollation() {
        return "utf8mb4_unicode_ci";
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
     * 格式：DECIMAL(M,D) 或 DECIMAL(M)
     */
    private String mapDecimal(String length) {
        if (StringUtils.isEmpty(length)) {
            return "DECIMAL(10,2)";
        }
        // 如果只传了M，默认D为2
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
