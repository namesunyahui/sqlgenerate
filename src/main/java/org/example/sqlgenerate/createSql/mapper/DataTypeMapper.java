package org.example.sqlgenerate.createSql.mapper;

import org.example.sqlgenerate.createSql.model.ImportExcel;

/**
 * 数据类型映射器接口
 * 负责将Excel中的数据类型映射到具体数据库的SQL类型
 */
public interface DataTypeMapper {

    /**
     * 映射数据类型
     *
     * @param column Excel列定义
     * @return 数据库SQL类型定义（包含长度和精度）
     */
    String mapType(ImportExcel column);

    /**
     * 是否支持该类型
     *
     * @param dataType 数据类型名称
     * @return 是否支持
     */
    boolean supportsType(String dataType);

    /**
     * 获取默认的字符集（仅MySQL）
     *
     * @return 字符集名称，如"utf8mb4"
     */
    default String getDefaultCharset() {
        return null;
    }

    /**
     * 获取默认的排序规则（仅MySQL）
     *
     * @return 排序规则名称，如"utf8mb4_unicode_ci"
     */
    default String getDefaultCollation() {
        return null;
    }
}
