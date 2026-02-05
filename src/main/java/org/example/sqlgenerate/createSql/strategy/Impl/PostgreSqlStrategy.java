package org.example.sqlgenerate.createSql.strategy.Impl;

import org.apache.commons.lang.StringUtils;
import org.example.sqlgenerate.createSql.codm.YesOrNoFlag;
import org.example.sqlgenerate.createSql.mapper.DataTypeMapper;
import org.example.sqlgenerate.createSql.mapper.PostgreSqlDataTypeMapper;
import org.example.sqlgenerate.createSql.model.CreateFrom;
import org.example.sqlgenerate.createSql.model.ImportExcel;
import org.example.sqlgenerate.createSql.model.IndexDefinition;
import org.example.sqlgenerate.createSql.strategy.DatabaseSqlStrategy;
import org.example.sqlgenerate.createSql.util.SqlValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PostgreSQL SQL生成策略实现
 */
public class PostgreSqlStrategy implements DatabaseSqlStrategy {

    private final DataTypeMapper dataTypeMapper = new PostgreSqlDataTypeMapper();

    @Override
    public String generateCreateTableSql(CreateFrom createFrom, List<ImportExcel> columns) {
        StringBuilder sqlBuilder = new StringBuilder();

        // DROP TABLE
        sqlBuilder.append("DROP TABLE IF EXISTS ")
                .append(quoteIdentifier(createFrom.getTableName()))
                .append(";\n");

        // CREATE TABLE
        sqlBuilder.append("CREATE TABLE ")
                .append(quoteIdentifier(createFrom.getTableName()))
                .append(" (\n");

        // 收集主键列
        List<String> primaryKeyColumns = new ArrayList<>();

        // 构建列定义
        for (ImportExcel column : columns) {
            sqlBuilder.append("  ")
                    .append(quoteIdentifier(column.getFiledName()))
                    .append(" ")
                    .append(buildDataTypeDefinition(column));

            String constraints = buildColumnConstraints(column);
            if (StringUtils.isNotEmpty(constraints)) {
                sqlBuilder.append(" ").append(constraints);
            }

            // 列注释
            if (StringUtils.isNotEmpty(column.getRemark())) {
                String remark = SqlValidator.escapeStringLiteral(column.getRemark().replace("\n", " "));
                sqlBuilder.append(" -- ").append(remark);
            }

            sqlBuilder.append(",\n");

            // 收集主键
            if (StringUtils.isNotEmpty(column.getKey())) {
                primaryKeyColumns.add(column.getFiledName());
            }
        }

        // 添加主键约束
        if (!primaryKeyColumns.isEmpty()) {
            sqlBuilder.append("  PRIMARY KEY (");
            for (int i = 0; i < primaryKeyColumns.size(); i++) {
                if (i > 0) sqlBuilder.append(", ");
                sqlBuilder.append(quoteIdentifier(primaryKeyColumns.get(i)));
            }
            sqlBuilder.append(")\n");
        } else {
            sqlBuilder.setLength(sqlBuilder.length() - 2);
        }

        sqlBuilder.append("\n);\n");

        return sqlBuilder.toString();
    }

    @Override
    public String generateTableCommentSql(CreateFrom createFrom) {
        String comment = SqlValidator.escapeStringLiteral(createFrom.getTableRemark());
        return "COMMENT ON TABLE " + quoteIdentifier(createFrom.getTableName()) +
                " IS '" + comment + "';\n";
    }

    @Override
    public String generateColumnCommentsSql(CreateFrom createFrom, List<ImportExcel> columns) {
        StringBuilder sb = new StringBuilder();

        for (ImportExcel column : columns) {
            if (StringUtils.isNotEmpty(column.getRemark())) {
                String comment = SqlValidator.escapeStringLiteral(column.getRemark());
                sb.append("COMMENT ON COLUMN ")
                        .append(quoteIdentifier(createFrom.getTableName()))
                        .append(".")
                        .append(quoteIdentifier(column.getFiledName()))
                        .append(" IS '").append(comment).append("';\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String quoteIdentifier(String identifier) {
        return "\"" + identifier + "\"";
    }

    @Override
    public String buildColumnDefinition(ImportExcel column) {
        StringBuilder definition = new StringBuilder();
        definition.append(quoteIdentifier(column.getFiledName())).append(" ");
        definition.append(buildDataTypeDefinition(column));
        definition.append(buildColumnConstraints(column));

        if (StringUtils.isNotEmpty(column.getRemark())) {
            String remark = SqlValidator.escapeStringLiteral(column.getRemark().replace("\n", " "));
            definition.append(" -- ").append(remark);
        }

        return definition.toString();
    }

    @Override
    public String getSupportedDatabaseType() {
        return "postgresql";
    }

    /**
     * 构建数据类型定义
     */
    private String buildDataTypeDefinition(ImportExcel column) {
        return dataTypeMapper.mapType(column);
    }

    /**
     * 构建列约束
     */
    private String buildColumnConstraints(ImportExcel column) {
        StringBuilder constraints = new StringBuilder();

        // NOT NULL
        if (YesOrNoFlag.FLAG_Y.equals(column.getNotNull())) {
            constraints.append("NOT NULL ");
        }

        // DEFAULT
        if (!StringUtils.equals(column.getDefaultValue(), "自增序列")) {
            if (StringUtils.isNotEmpty(column.getDefaultValue())) {
                constraints.append("DEFAULT '")
                        .append(SqlValidator.escapeStringLiteral(column.getDefaultValue()))
                        .append("' ");
            }
        }

        // 去除末尾空格
        if (constraints.length() > 0 && constraints.charAt(constraints.length() - 1) == ' ') {
            constraints.setLength(constraints.length() - 1);
        }

        return constraints.toString();
    }

    @Override
    public String generateIndexSql(CreateFrom createFrom, List<ImportExcel> columns) {
        StringBuilder sb = new StringBuilder();

        // 按索引名称分组列
        Map<String, IndexDefinition> indexMap = new HashMap<>();

        for (ImportExcel column : columns) {
            if (StringUtils.isNotEmpty(column.getIndexName())) {
                String indexName = column.getIndexName();

                IndexDefinition indexDef = indexMap.computeIfAbsent(indexName, k -> {
                    IndexDefinition def = new IndexDefinition();
                    def.setIndexName(indexName);
                    def.setIndexType(column.getIndexType());
                    def.setComment("Generated from Excel");
                    return def;
                });

                indexDef.addColumnName(column.getFiledName());
            }
        }

        // 生成索引SQL
        for (IndexDefinition indexDef : indexMap.values()) {
            sb.append(buildIndexSql(createFrom.getTableName(), indexDef)).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String generateForeignKeySql(CreateFrom createFrom, List<ImportExcel> columns) {
        StringBuilder sb = new StringBuilder();

        for (ImportExcel column : columns) {
            if (StringUtils.isNotEmpty(column.getForeignTable()) && StringUtils.isNotEmpty(column.getForeignColumn())) {
                sb.append("ALTER TABLE ")
                        .append(quoteIdentifier(createFrom.getTableName()))
                        .append(" ADD CONSTRAINT fk_")
                        .append(createFrom.getTableName())
                        .append("_")
                        .append(column.getFiledName())
                        .append(" FOREIGN KEY (")
                        .append(quoteIdentifier(column.getFiledName()))
                        .append(") REFERENCES ")
                        .append(quoteIdentifier(column.getForeignTable()))
                        .append("(")
                        .append(quoteIdentifier(column.getForeignColumn()))
                        .append(");\n");
            }
        }

        return sb.toString();
    }

    @Override
    public String generateSequenceSql(CreateFrom createFrom, List<ImportExcel> columns) {
        StringBuilder sb = new StringBuilder();

        // PostgreSQL使用SERIAL或BIGSERIAL，也可以使用SEQUENCE
        for (ImportExcel column : columns) {
            if (YesOrNoFlag.FLAG_Y.equals(column.getAutoIncrement())) {
                // 对于已存在的表，可以创建序列并设置默认值
                String sequenceName = "seq_" + createFrom.getTableName() + "_" + column.getFiledName();

                sb.append("CREATE SEQUENCE IF NOT EXISTS ")
                        .append(quoteIdentifier(sequenceName))
                        .append(";\n");

                sb.append("ALTER TABLE ")
                        .append(quoteIdentifier(createFrom.getTableName()))
                        .append(" ALTER COLUMN ")
                        .append(quoteIdentifier(column.getFiledName()))
                        .append(" SET DEFAULT nextval('")
                        .append(sequenceName)
                        .append("');\n");
            }
        }

        return sb.toString();
    }

    /**
     * 构建单个索引SQL
     */
    private String buildIndexSql(String tableName, IndexDefinition indexDef) {
        StringBuilder sb = new StringBuilder();

        // CREATE [UNIQUE] INDEX index_name ON table_name (column1, column2, ...)
        sb.append("CREATE ");

        if (indexDef.isUniqueIndex()) {
            sb.append("UNIQUE ");
        }

        sb.append("INDEX ")
                .append(quoteIdentifier(indexDef.getIndexName()))
                .append(" ON ")
                .append(quoteIdentifier(tableName))
                .append(" (");

        // 列名列表
        for (int i = 0; i < indexDef.getColumnNames().size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(quoteIdentifier(indexDef.getColumnNames().get(i)));
        }

        sb.append(");");

        return sb.toString();
    }
}
