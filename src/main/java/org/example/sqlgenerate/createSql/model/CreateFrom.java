package org.example.sqlgenerate.createSql.model;

import lombok.Data;

/**
 * 创建sql 需要的表单
 */
@Data
public class CreateFrom {

    /**
     * 表名
     */
    private String tableName;
    /**
     * 表名备注
     */
    private String tableRemark;
    /**
     * mongoDB的文件ID
     */
    private String fileId;

    /**
     * 选择的数据库  mysql oracle
     */
    private String database;

}
