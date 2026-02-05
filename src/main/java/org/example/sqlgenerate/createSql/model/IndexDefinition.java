package org.example.sqlgenerate.createSql.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 索引定义模型
 */
@Data
public class IndexDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 索引名称
     */
    private String indexName;

    /**
     * 索引类型：NORMAL-普通索引, UNIQUE-唯一索引, FULLTEXT-全文索引
     */
    private String indexType;

    /**
     * 索引列名列表
     */
    private List<String> columnNames = new ArrayList<>();

    /**
     * 是否唯一索引
     */
    private boolean unique;

    /**
     * 索引注释
     */
    private String comment;

    /**
     * 构造方法
     */
    public IndexDefinition() {
    }

    /**
     * 构造方法
     */
    public IndexDefinition(String indexName, String indexType, List<String> columnNames) {
        this.indexName = indexName;
        this.indexType = indexType;
        this.columnNames = columnNames;
        this.unique = "UNIQUE".equalsIgnoreCase(indexType);
    }

    /**
     * 添加列名
     */
    public void addColumnName(String columnName) {
        if (this.columnNames == null) {
            this.columnNames = new ArrayList<>();
        }
        this.columnNames.add(columnName);
    }

    /**
     * 是否为普通索引
     */
    public boolean isNormal() {
        return "NORMAL".equalsIgnoreCase(indexType) || indexType == null || indexType.isEmpty();
    }

    /**
     * 是否为唯一索引
     */
    public boolean isUniqueIndex() {
        return "UNIQUE".equalsIgnoreCase(indexType);
    }

    /**
     * 是否为全文索引
     */
    public boolean isFullText() {
        return "FULLTEXT".equalsIgnoreCase(indexType) || "FULL_TEXT".equalsIgnoreCase(indexType);
    }

    /**
     * 获取索引类型SQL关键字
     */
    public String getIndexTypeSql() {
        if (unique) {
            return "UNIQUE";
        }
        if (isFullText()) {
            return "FULLTEXT";
        }
        return "";
    }
}
