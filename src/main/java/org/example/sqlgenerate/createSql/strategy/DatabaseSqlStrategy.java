package org.example.sqlgenerate.createSql.strategy;

import org.example.sqlgenerate.createSql.model.CreateFrom;
import org.example.sqlgenerate.createSql.model.ImportExcel;
import org.example.sqlgenerate.createSql.model.IndexDefinition;

import java.util.List;

/**
 * 数据库SQL生成策略接口
 * 不同数据库的SQL生成逻辑通过此接口抽象
 */
public interface DatabaseSqlStrategy {

    /**
     * 生成CREATE TABLE语句
     *
     * @param createFrom 生成参数
     * @param columns    Excel中的列定义
     * @return SQL语句
     */
    String generateCreateTableSql(CreateFrom createFrom, List<ImportExcel> columns);

    /**
     * 生成表注释SQL
     *
     * @param createFrom 生成参数
     * @return 注释SQL语句
     */
    String generateTableCommentSql(CreateFrom createFrom);

    /**
     * 生成列注释SQL（可选，某些数据库在CREATE TABLE中已包含）
     *
     * @param createFrom 生成参数
     * @param columns    列定义
     * @return 注释SQL语句，可能返回空字符串
     */
    String generateColumnCommentsSql(CreateFrom createFrom, List<ImportExcel> columns);

    /**
     * 生成索引SQL
     *
     * @param createFrom 生成参数
     * @param columns    列定义
     * @return 索引SQL语句
     */
    String generateIndexSql(CreateFrom createFrom, List<ImportExcel> columns);

    /**
     * 生成外键约束SQL
     *
     * @param createFrom 生成参数
     * @param columns    列定义
     * @return 外键约束SQL语句
     */
    String generateForeignKeySql(CreateFrom createFrom, List<ImportExcel> columns);

    /**
     * 生成自增序列SQL（Oracle等需要）
     *
     * @param createFrom 生成参数
     * @param columns    列定义
     * @return 序列SQL语句
     */
    String generateSequenceSql(CreateFrom createFrom, List<ImportExcel> columns);

    /**
     * 包裹标识符（表名、列名等）
     * MySQL使用反引号，Oracle使用双引号
     *
     * @param identifier 标识符
     * @return 包裹后的标识符
     */
    String quoteIdentifier(String identifier);

    /**
     * 构建列定义SQL
     *
     * @param column 列信息
     * @return 列定义SQL
     */
    String buildColumnDefinition(ImportExcel column);

    /**
     * 获取支持的数据库类型
     *
     * @return 数据库类型
     */
    String getSupportedDatabaseType();
}
