package org.example.sqlgenerate.createSql.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName: ImportExcel
 * @Description:
 * @Author: DELL
 * @Date: 2022/9/13 20:58
 */
@Getter
@Setter
public class ImportExcel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 字段名
     */
    @ExcelProperty(index = 1)
    private String filedName;

    /**
     * 字段类型
     */
    @ExcelProperty(index = 2)
    private String dataType;

    /**
     * 字段长度
     */
    @ExcelProperty(index = 3)
    private String length;

    /**
     * 是否非空
     */
    @ExcelProperty(index = 4)
    private String notNull;

    /**
     * 主键
     */
    @ExcelProperty(index = 5)
    private String key;

    /**
     * 默认值
     */
    @ExcelProperty(index = 6)
    private String defaultValue;

    /**
     * 备注
     */
    @ExcelProperty(index = 7)
    private String remark;

    /**
     * 索引名称（可选）
     */
    @ExcelProperty(index = 8)
    private String indexName;

    /**
     * 索引类型（可选）：NORMAL-普通索引, UNIQUE-唯一索引, FULLTEXT-全文索引
     */
    @ExcelProperty(index = 9)
    private String indexType;

    /**
     * 外键关联表名（可选）
     */
    @ExcelProperty(index = 10)
    private String foreignTable;

    /**
     * 外键关联列名（可选）
     */
    @ExcelProperty(index = 11)
    private String foreignColumn;

    /**
     * 是否自增（用于Oracle序列）
     */
    @ExcelProperty(index = 12)
    private String autoIncrement;

}

