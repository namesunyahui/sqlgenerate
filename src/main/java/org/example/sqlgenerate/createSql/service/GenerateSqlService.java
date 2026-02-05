package org.example.sqlgenerate.createSql.service;

import org.example.sqlgenerate.createSql.model.CreateFrom;

/**
 * SQL生成服务接口
 */
public interface GenerateSqlService {

    /**
     * 根据上传的Excel文件生成SQL
     *
     * @param createFrom 包含表名、表注释、数据库类型、上传文件
     * @return 生成的SQL语句
     */
    String generateSqlFromFile(CreateFrom createFrom);

    /**
     * 生成Oracle创建表SQL
     * @deprecated 使用generateSqlFromFile代替
     */
    @Deprecated
    String generateOracleSql(CreateFrom createFrom);

    /**
     * 生成MySQL创建表SQL
     * @deprecated 使用generateSqlFromFile代替
     */
    @Deprecated
    String generateMysqlSql(CreateFrom createFrom);

    /**
     * 生成SQL
     * @deprecated 使用generateSqlFromFile代替
     */
    @Deprecated
    String generateSql(CreateFrom createFrom);
}
