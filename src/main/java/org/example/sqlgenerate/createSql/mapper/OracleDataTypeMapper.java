package org.example.sqlgenerate.createSql.mapper;

import org.apache.commons.lang.StringUtils;
import org.example.sqlgenerate.createSql.model.ImportExcel;

/**
 * Oracle数据类型映射器
 */
public class OracleDataTypeMapper implements DataTypeMapper {

    @Override
    public String mapType(ImportExcel column) {
        String dataType = column.getDataType().toUpperCase().trim();
        String length = column.getLength();

        return switch (dataType) {
            // 数值类型
            case "NUMBER" -> mapNumber(length);
            case "DECIMAL", "NUMERIC" -> mapNumber(length);
            case "INTEGER", "INT", "BIGINT" -> "INTEGER";
            case "BINARY_FLOAT" -> "BINARY_FLOAT";
            case "BINARY_DOUBLE" -> "BINARY_DOUBLE";

            // 字符串类型
            case "CHAR" -> "CHAR(" + getDefaultLength(length, "1") + ")";
            case "VARCHAR", "VARCHAR2" -> "VARCHAR2(" + getDefaultLength(length, "255") + ")";
            case "NCHAR" -> "NCHAR(" + getDefaultLength(length, "1") + ")";
            case "NVARCHAR", "NVARCHAR2" -> "NVARCHAR2(" + getDefaultLength(length, "255") + ")";
            case "CLOB" -> "CLOB";
            case "NCLOB" -> "NCLOB";
            case "LONG" -> "LONG";

            // 二进制类型
            case "RAW" -> "RAW(" + getDefaultLength(length, "1") + ")";
            case "BLOB" -> "BLOB";
            case "LONG RAW" -> "LONG RAW";

            // 日期时间类型
            case "DATE", "DATETIME" -> "DATE";
            case "TIMESTAMP" -> mapTimestamp(length);
            case "INTERVAL YEAR TO MONTH" -> "INTERVAL YEAR TO MONTH";
            case "INTERVAL DAY TO SECOND" -> "INTERVAL DAY TO SECOND";

            // ROWID类型
            case "ROWID" -> "ROWID";
            case "UROWID" -> "UROWID";

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
        return upper.matches("(NUMBER|DECIMAL|NUMERIC|INTEGER|INT|BIGINT|BINARY_FLOAT|BINARY_DOUBLE|" +
                "CHAR|VARCHAR|VARCHAR2|NCHAR|NVARCHAR|NVARCHAR2|" +
                "CLOB|NCLOB|LONG|RAW|BLOB|LONG RAW|" +
                "DATE|DATETIME|TIMESTAMP|TIME|" +
                "INTERVAL|ROWID|UROWID)");
    }

    /**
     * 映射NUMBER类型
     * 格式：NUMBER(p,s) 或 NUMBER(p)
     */
    private String mapNumber(String length) {
        if (StringUtils.isEmpty(length)) {
            return "NUMBER";
        }
        return "NUMBER(" + length + ")";
    }

    /**
     * 映射TIMESTAMP类型
     * 支持时区：TIMESTAMP WITH TIME ZONE
     */
    private String mapTimestamp(String length) {
        if (StringUtils.isNotEmpty(length) && length.toUpperCase().contains("TIMEZONE")) {
            return "TIMESTAMP WITH TIME ZONE";
        }
        return "TIMESTAMP";
    }

    /**
     * 获取默认长度
     */
    private String getDefaultLength(String length, String defaultValue) {
        return StringUtils.isNotEmpty(length) ? length : defaultValue;
    }
}
