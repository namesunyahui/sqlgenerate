package org.example.sqlgenerate.createSql.util;

import com.alibaba.excel.EasyExcel;
import org.example.sqlgenerate.createSql.model.ImportExcel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel模板生成器
 */
public class ExcelTemplateGenerator {

    /**
     * 生成Excel模板字节数组（带示例数据）
     */
    public static byte[] generateTemplate() throws IOException {
        List<ImportExcel> data = new ArrayList<>();

        // 基础类型示例
        ImportExcel idRow = new ImportExcel();
        idRow.setFiledName("id");
        idRow.setDataType("BIGINT");
        idRow.setNotNull("Y");
        idRow.setKey("Y");
        idRow.setRemark("主键ID");
        data.add(idRow);

        ImportExcel userNameRow = new ImportExcel();
        userNameRow.setFiledName("user_name");
        userNameRow.setDataType("VARCHAR");
        userNameRow.setLength("50");
        userNameRow.setNotNull("Y");
        userNameRow.setRemark("用户名");
        userNameRow.setIndexName("idx_user_name");
        userNameRow.setIndexType("NORMAL");
        data.add(userNameRow);

        ImportExcel emailRow = new ImportExcel();
        emailRow.setFiledName("email");
        emailRow.setDataType("VARCHAR");
        emailRow.setLength("100");
        emailRow.setRemark("邮箱");
        emailRow.setIndexName("uk_email");
        emailRow.setIndexType("UNIQUE");
        data.add(emailRow);

        ImportExcel balanceRow = new ImportExcel();
        balanceRow.setFiledName("balance");
        balanceRow.setDataType("DECIMAL");
        balanceRow.setLength("10,2");
        balanceRow.setNotNull("Y");
        balanceRow.setDefaultValue("0.00");
        balanceRow.setRemark("账户余额");
        data.add(balanceRow);

        ImportExcel statusRow = new ImportExcel();
        statusRow.setFiledName("status");
        statusRow.setDataType("INT");
        statusRow.setLength("11");
        statusRow.setNotNull("Y");
        statusRow.setDefaultValue("1");
        statusRow.setRemark("状态：1-正常，0-禁用");
        data.add(statusRow);

        // 日期时间类型
        ImportExcel createTimeRow = new ImportExcel();
        createTimeRow.setFiledName("create_time");
        createTimeRow.setDataType("DATETIME");
        createTimeRow.setNotNull("Y");
        createTimeRow.setRemark("创建时间");
        createTimeRow.setIndexName("idx_create_time");
        createTimeRow.setIndexType("NORMAL");
        data.add(createTimeRow);

        ImportExcel updateTimeRow = new ImportExcel();
        updateTimeRow.setFiledName("update_time");
        updateTimeRow.setDataType("TIMESTAMP");
        updateTimeRow.setRemark("更新时间");
        data.add(updateTimeRow);

        // 文本类型
        ImportExcel descriptionRow = new ImportExcel();
        descriptionRow.setFiledName("description");
        descriptionRow.setDataType("TEXT");
        descriptionRow.setRemark("详细描述");
        data.add(descriptionRow);

        ImportExcel remarkRow = new ImportExcel();
        remarkRow.setFiledName("remark");
        remarkRow.setDataType("VARCHAR");
        remarkRow.setLength("500");
        remarkRow.setRemark("备注");
        data.add(remarkRow);

        // 二进制类型
        ImportExcel avatarRow = new ImportExcel();
        avatarRow.setFiledName("avatar");
        avatarRow.setDataType("BLOB");
        avatarRow.setRemark("头像图片");
        data.add(avatarRow);

        ImportExcel configRow = new ImportExcel();
        configRow.setFiledName("config");
        configRow.setDataType("JSON");
        configRow.setRemark("配置信息");
        data.add(configRow);

        // 外键示例
        ImportExcel deptIdRow = new ImportExcel();
        deptIdRow.setFiledName("dept_id");
        deptIdRow.setDataType("BIGINT");
        deptIdRow.setRemark("部门ID");
        deptIdRow.setIndexName("idx_dept_id");
        deptIdRow.setIndexType("NORMAL");
        deptIdRow.setForeignTable("t_dept");
        deptIdRow.setForeignColumn("id");
        data.add(deptIdRow);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            EasyExcel.write(out)
                    .head(ImportExcel.class)
                    .sheet("表结构定义")
                    .doWrite(data);
            return out.toByteArray();
        }
    }

    /**
     * 生成空模板
     */
    public static byte[] generateEmptyTemplate() throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            EasyExcel.write(out)
                    .head(ImportExcel.class)
                    .sheet("表结构定义")
                    .doWrite(new ArrayList<>());
            return out.toByteArray();
        }
    }

    /**
     * 创建一行数据（完整版本）
     */
    private static ImportExcel createRow(String filedName, String dataType, String length,
                                         String notNull, String key, String defaultValue, String remark,
                                         String indexName, String indexType, String foreignTable, String foreignColumn) {
        ImportExcel row = new ImportExcel();
        row.setFiledName(filedName);
        row.setDataType(dataType);
        row.setLength(length);
        row.setNotNull(notNull);
        row.setKey(key);
        row.setDefaultValue(defaultValue);
        row.setRemark(remark);
        row.setIndexName(indexName);
        row.setIndexType(indexType);
        row.setForeignTable(foreignTable);
        row.setForeignColumn(foreignColumn);
        return row;
    }

    /**
     * 创建一行数据（简化版本，兼容旧代码）
     */
    private static ImportExcel createRow(String filedName, String dataType, String length,
                                         String notNull, String key, String defaultValue, String remark) {
        return createRow(filedName, dataType, length, notNull, key, defaultValue, remark,
                "", "", "", "");
    }
}
