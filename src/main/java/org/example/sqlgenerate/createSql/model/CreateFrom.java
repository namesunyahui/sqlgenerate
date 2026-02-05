package org.example.sqlgenerate.createSql.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * SQL生成请求参数
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
     * 上传的Excel文件
     */
    private MultipartFile file;

    /**
     * 数据库类型 mysql/oracle
     */
    private String database;

    /**
     * 兼容旧版本：MongoDB的文件ID（已废弃）
     * @deprecated 使用file代替
     */
    @Deprecated
    private String fileId;
}
