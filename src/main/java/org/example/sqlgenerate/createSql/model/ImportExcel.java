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
    @ExcelProperty(value = "字段名", index = 1)
    private String filedName;

    /**
     * 字段类型
     */
    @ExcelProperty(value = "类型", index = 2)
    private String dataType;

    /**
     * 字段长度
     */
    @ExcelProperty(value = "长度", index = 3)
    private String length;

    /**
     * 是否非空
     */
    @ExcelProperty(value = "非空", index = 4)
    private String notNull;

    /**
     * 主键
     */
    @ExcelProperty(value = "主键", index = 5)
    private String key;

    /**
     * 默认值
     */
    @ExcelProperty(value = "默认值", index = 6)
    private String defaultValue;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 7)
    private String remark;

    /**
     * 索引名称（可选）
     */
    @ExcelProperty(value = "索引名", index = 8)
    private String indexName;

    /**
     * 索引类型（可选）：NORMAL-普通索引, UNIQUE-唯一索引, FULLTEXT-全文索引
     */
    @ExcelProperty(value = "索引类型", index = 9)
    private String indexType;

    /**
     * 外键关联表名（可选）
     */
    @ExcelProperty(value = "外键表", index = 10)
    private String foreignTable;

    /**
     * 外键关联列名（可选）
     */
    @ExcelProperty(value = "外键列", index = 11)
    private String foreignColumn;

    /**
     * 是否自增（用于Oracle序列）
     */
    @ExcelProperty(value = "自增", index = 12)
    private String autoIncrement;

}

