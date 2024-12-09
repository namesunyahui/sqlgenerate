package org.example.sqlgenerate.createSql.service.Impl;

import com.alibaba.excel.EasyExcel;
import jakarta.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.example.sqlgenerate.createSql.codm.YesOrNoFlag;
import org.example.sqlgenerate.createSql.model.CreateFrom;
import org.example.sqlgenerate.createSql.model.ImportExcel;
import org.example.sqlgenerate.createSql.service.GenerateSqlService;
import org.example.sqlgenerate.mongodb.service.FileInfoRepository;
import org.example.sqlgenerate.mongodb.service.Impl.FileStorageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Service
public class GenerateSqlServiceImpl implements GenerateSqlService {
    private static final Logger logger = LoggerFactory.getLogger(GenerateSqlServiceImpl.class);


    @Resource
    private FileInfoRepository fileInfoRepository;

    @Resource
    private FileStorageServiceImpl fileStorageService;

    @Override
    public String generateMysqlSql(CreateFrom createFrom) {
        StringBuilder sqlBuilder = new StringBuilder();
        try (InputStream inputStream = fileStorageService.downloadFile(createFrom.getFileId())) {
            List<ImportExcel> columns = EasyExcel.read(inputStream)
                    .head(ImportExcel.class)
                    .sheet(0)
                    .doReadSync();

            sqlBuilder.append("DROP TABLE IF EXISTS ").append(createFrom.getTableName()).append(";\n")
                    .append("CREATE TABLE ").append(createFrom.getTableName()).append(" (\n");

            List<String> primaryKeyColumns = new ArrayList<>();
            for (ImportExcel column : columns) {
                sqlBuilder.append(buildMysqlColumnDefinition(column));
                if (StringUtils.isNotEmpty(column.getKey())) {
                    primaryKeyColumns.add(column.getFiledName());
                }
                sqlBuilder.append(",\n");
            }

            if (!primaryKeyColumns.isEmpty()) {
                sqlBuilder.append("  PRIMARY KEY (").append(String.join(", ", primaryKeyColumns)).append(")\n");
            }

            // Remove the last comma and add a semicolon
            if (sqlBuilder.charAt(sqlBuilder.length() - 2) == ',') {
                sqlBuilder.setLength(sqlBuilder.length() - 2);
            }
            sqlBuilder.append("\n);\n");

            sqlBuilder.append("ALTER TABLE ").append(createFrom.getTableName())
                    .append(" COMMENT '").append(createFrom.getTableRemark()).append("';\n");

        } catch (IOException e) {
            // Use a logging framework to log the exception
            logger.error("Failed to read the file", e);
        }

        return sqlBuilder.toString();
    }

    // Additional methods for building parts of the column definition can be added here
    @Override
    public String generateSql(CreateFrom createFrom) {
        if(StringUtils.equals(createFrom.getDatabase(),"mysql")){
            return generateMysqlSql(createFrom);
        }else if(StringUtils.equals(createFrom.getDatabase(),"oracle")){
            return generateOracleSql(createFrom);
        }
        return "请选择对应的数据库";
    }


    @Override
    public String generateOracleSql(CreateFrom createFrom) {
        List<ImportExcel> columns;
        try (InputStream inputStream = fileStorageService.downloadFile(createFrom.getFileId())) {
            // 解析Excel文件
            columns = EasyExcel.read(inputStream)
                    .head(ImportExcel.class)
                    .sheet(0)
                    .doReadSync();
        } catch (IOException e) {
            throw new RuntimeException("Failed to download file", e);
        }

        // 生成SQL语句
        StringBuilder sb = new StringBuilder("CREATE TABLE " + createFrom.getTableName() + " (\n");
        List<String> primaryKeyColumns = new ArrayList<>();
        for (ImportExcel importExcel : columns) {
            sb.append(importExcel.getFiledName()).append(" ");
            extracted(importExcel, sb);
            sb.append(",").append("\n");
            // 主键处理
            if (StringUtils.isNotEmpty(importExcel.getKey())) {
                primaryKeyColumns.add(importExcel.getFiledName());
            }
        }

        // 添加主键约束
        if (!primaryKeyColumns.isEmpty()) {
            sb.append("  PRIMARY KEY (").append(String.join(", ", primaryKeyColumns)).append(")\n");
        }

        // 移除最后一个逗号并添加表注释
        if (sb.charAt(sb.length() - 2) == ',') {
            sb.setLength(sb.length() - 2);
        }
        sb.append("\n);\n");

        // 添加表注释
        sb.append("COMMENT ON TABLE ").append(createFrom.getTableName())
                .append(" IS '").append(createFrom.getTableRemark()).append("';\n");

        // 添加字段注释
        for (ImportExcel importExcel : columns) {
            sb.append("COMMENT ON COLUMN ")
                    .append(createFrom.getTableName()).append(".")
                    .append(importExcel.getFiledName())
                    .append(" IS '").append(importExcel.getRemark()).append("';\n");
        }

        return sb.toString();
    }


    private String buildMysqlColumnDefinition(ImportExcel column) {
        StringBuilder definition = new StringBuilder(column.getFiledName()).append(" ");
        extracted(column, definition);
        // 注释
        String remark = column.getRemark();
        if(StringUtils.isNotEmpty(remark)){
            String replace = remark.replace("\n", " ");
            definition.append(" COMMENT '").append(replace).append("'");
        }

        //主键
        String key = column.getKey();
        if(StringUtils.isNotEmpty(key)){
            definition.append(", primary key (").append(column.getFiledName()).append(")");
        }

        definition.append(",\n");
        return definition.toString();
    }

    private static void extracted(ImportExcel column, StringBuilder definition) {
        if(StringUtils.contains("date", column.getDataType())){
            definition.append(column.getDataType());
        }else if(StringUtils.contains("datetime", column.getDataType())){
            definition.append(column.getDataType()).append(" ");
        }else if(StringUtils.isEmpty(column.getLength())){
            definition.append(column.getDataType()).append(" ");
        }else{
            definition.append(column.getDataType()).append("(").append(column.getLength()).append(") ");
        }

        // 非空与否
        if (YesOrNoFlag.FLAG_Y.equals(column.getNotNull())) {
            definition.append("NOT NULL ");
        }
        // 默认值
        if (!StringUtils.equals(column.getDefaultValue(), "自增序列")) {

            if(StringUtils.isNotEmpty(column.getDefaultValue())){
                definition.append("DEFAULT '").append(column.getDefaultValue()).append("' ");
            }
        }
    }


}
