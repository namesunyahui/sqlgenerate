package org.example.sqlgenerate.createSql.service;

import org.example.sqlgenerate.createSql.model.CreateFrom;

/**
 * 生成sql
 */
public interface GenerateSqlService {


    /**
     * 生成Oracle创建表sql
     */
    String generateOracleSql(CreateFrom createFrom);

    String generateMysqlSql(CreateFrom createFrom);


    String generateSql(CreateFrom createFrom);
}
