package org.example.sqlgenerate.createSql.service.Impl;

import com.alibaba.excel.EasyExcel;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang.StringUtils;
import org.example.sqlgenerate.createSql.codm.DatabaseType;
import org.example.sqlgenerate.createSql.model.CreateFrom;
import org.example.sqlgenerate.createSql.model.ImportExcel;
import org.example.sqlgenerate.createSql.service.GenerateSqlService;
import org.example.sqlgenerate.createSql.strategy.DatabaseSqlStrategy;
import org.example.sqlgenerate.createSql.strategy.Impl.MysqlSqlStrategy;
import org.example.sqlgenerate.createSql.strategy.Impl.OracleSqlStrategy;
import org.example.sqlgenerate.createSql.strategy.Impl.PostgreSqlStrategy;
import org.example.sqlgenerate.createSql.strategy.Impl.SqlServerStrategy;
import org.example.sqlgenerate.createSql.util.SqlValidator;
import org.example.sqlgenerate.exception.SqlGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GenerateSqlServiceImpl implements GenerateSqlService {
    private static final Logger logger = LoggerFactory.getLogger(GenerateSqlServiceImpl.class);

    /**
     * 策略映射表，根据数据库类型选择对应的策略
     */
    private final Map<DatabaseType, DatabaseSqlStrategy> strategyMap = new HashMap<>();

    @PostConstruct
    public void init() {
        strategyMap.put(DatabaseType.MYSQL, new MysqlSqlStrategy());
        strategyMap.put(DatabaseType.ORACLE, new OracleSqlStrategy());
        strategyMap.put(DatabaseType.POSTGRESQL, new PostgreSqlStrategy());
        strategyMap.put(DatabaseType.SQLSERVER, new SqlServerStrategy());
    }

    @Override
    public String generateSqlFromFile(CreateFrom createFrom) {
        validateCreateFromFile(createFrom);

        DatabaseType databaseType = DatabaseType.fromCode(createFrom.getDatabase());
        if (databaseType == null) {
            throw SqlGenerationException.validation("不支持的数据库类型: " + createFrom.getDatabase());
        }

        DatabaseSqlStrategy strategy = strategyMap.get(databaseType);
        if (strategy == null) {
            throw SqlGenerationException.validation("暂不支持该数据库类型: " + databaseType.getDisplayName());
        }

        List<ImportExcel> columns = readExcelFromFile(createFrom.getFile());
        validateColumns(columns);

        return generateSqlWithStrategy(createFrom, columns, strategy);
    }

    @Override
    @Deprecated
    public String generateSql(CreateFrom createFrom) {
        if (createFrom.getFile() != null) {
            return generateSqlFromFile(createFrom);
        }
        throw SqlGenerationException.validation("请上传Excel文件");
    }

    @Override
    @Deprecated
    public String generateMysqlSql(CreateFrom createFrom) {
        if (createFrom.getFile() != null) {
            createFrom.setDatabase("mysql");
            return generateSqlFromFile(createFrom);
        }
        throw SqlGenerationException.validation("请上传Excel文件");
    }

    @Override
    @Deprecated
    public String generateOracleSql(CreateFrom createFrom) {
        if (createFrom.getFile() != null) {
            createFrom.setDatabase("oracle");
            return generateSqlFromFile(createFrom);
        }
        throw SqlGenerationException.validation("请上传Excel文件");
    }

    private String generateSqlWithStrategy(CreateFrom createFrom, List<ImportExcel> columns, DatabaseSqlStrategy strategy) {
        StringBuilder sqlBuilder = new StringBuilder();

        // 1. CREATE TABLE
        sqlBuilder.append(strategy.generateCreateTableSql(createFrom, columns));

        // 2. 表注释
        sqlBuilder.append(strategy.generateTableCommentSql(createFrom));

        // 3. 列注释（Oracle需要）
        String columnComments = strategy.generateColumnCommentsSql(createFrom, columns);
        if (StringUtils.isNotEmpty(columnComments)) {
            sqlBuilder.append(columnComments);
        }

        // 4. 序列（Oracle需要）
        String sequenceSql = strategy.generateSequenceSql(createFrom, columns);
        if (StringUtils.isNotEmpty(sequenceSql)) {
            sqlBuilder.append("\n-- 自增序列\n");
            sqlBuilder.append(sequenceSql);
        }

        // 5. 索引
        String indexSql = strategy.generateIndexSql(createFrom, columns);
        if (StringUtils.isNotEmpty(indexSql)) {
            sqlBuilder.append("\n-- 索引\n");
            sqlBuilder.append(indexSql);
        }

        // 6. 外键约束
        String foreignKeySql = strategy.generateForeignKeySql(createFrom, columns);
        if (StringUtils.isNotEmpty(foreignKeySql)) {
            sqlBuilder.append("\n-- 外键约束\n");
            sqlBuilder.append(foreignKeySql);
        }

        return sqlBuilder.toString();
    }

    private List<ImportExcel> readExcelFromFile(MultipartFile file) {
        try {
            return EasyExcel.read(file.getInputStream())
                    .head(ImportExcel.class)
                    .sheet(0)
                    .doReadSync();
        } catch (IOException e) {
            logger.error("Failed to read uploaded file: {}", file.getOriginalFilename(), e);
            throw SqlGenerationException.fileProcessing("无法读取上传的文件: " + file.getOriginalFilename(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while reading file", e);
            throw SqlGenerationException.dataParsing("解析Excel时发生错误: " + e.getMessage());
        }
    }

    private void validateCreateFromFile(CreateFrom createFrom) {
        if (createFrom == null) {
            throw SqlGenerationException.validation("请求参数不能为空");
        }

        if (createFrom.getFile() == null || createFrom.getFile().isEmpty()) {
            throw SqlGenerationException.validation("请上传Excel文件");
        }

        SqlValidator.validateTableName(createFrom.getTableName());

        if (StringUtils.isNotEmpty(createFrom.getTableRemark())) {
            SqlValidator.validateComment(createFrom.getTableRemark());
        }

        if (StringUtils.isEmpty(createFrom.getDatabase())) {
            throw SqlGenerationException.validation("数据库类型不能为空");
        }

        if (!DatabaseType.isValid(createFrom.getDatabase())) {
            throw SqlGenerationException.validation("不支持的数据库类型: " + createFrom.getDatabase());
        }
    }

    private void validateColumns(List<ImportExcel> columns) {
        SqlValidator.validateColumns(columns);

        for (ImportExcel column : columns) {
            SqlValidator.validateColumnName(column.getFiledName());
        }
    }
}
