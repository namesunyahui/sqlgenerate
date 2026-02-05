package org.example.sqlgenerate.createSql.codm;

import lombok.Getter;

/**
 * 数据库类型枚举
 */
@Getter
public enum DatabaseType {

    /**
     * MySQL数据库
     */
    MYSQL("mysql", "MySQL"),

    /**
     * Oracle数据库
     */
    ORACLE("oracle", "Oracle"),

    /**
     * PostgreSQL数据库
     */
    POSTGRESQL("postgresql", "PostgreSQL"),

    /**
     * SQL Server数据库
     */
    SQLSERVER("sqlserver", "SQL Server");

    private final String code;
    private final String displayName;

    DatabaseType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * 根据代码获取数据库类型
     */
    public static DatabaseType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (DatabaseType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 是否是有效的数据库类型
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}
