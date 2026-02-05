package org.example.sqlgenerate.createSql.util;

import com.alibaba.excel.EasyExcel;
import org.example.sqlgenerate.createSql.model.ImportExcel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExcelTemplateGenerator测试
 */
@DisplayName("Excel模板生成测试")
class ExcelTemplateGeneratorTest {

    @Test
    @DisplayName("生成带示例数据的模板")
    void testGenerateTemplate() throws IOException {
        byte[] template = ExcelTemplateGenerator.generateTemplate();

        assertNotNull(template);
        assertTrue(template.length > 0, "模板字节数组不应为空");

        // 验证可以读取Excel内容
        List<ImportExcel> data = EasyExcel.read(new ByteArrayInputStream(template))
                .head(ImportExcel.class)
                .sheet()
                .doReadSync();

        assertNotNull(data);
        assertFalse(data.isEmpty(), "模板应包含示例数据");

        // 验证示例数据包含各种类型
        boolean hasDecimal = false;
        boolean hasDatetime = false;
        boolean hasText = false;
        boolean hasBlob = false;
        boolean hasJson = false;

        for (ImportExcel row : data) {
            if ("DECIMAL".equals(row.getDataType())) {
                hasDecimal = true;
                assertEquals("10,2", row.getLength(), "DECIMAL应有精度示例");
            }
            if ("DATETIME".equals(row.getDataType())) {
                hasDatetime = true;
            }
            if ("TEXT".equals(row.getDataType())) {
                hasText = true;
            }
            if ("BLOB".equals(row.getDataType())) {
                hasBlob = true;
            }
            if ("JSON".equals(row.getDataType())) {
                hasJson = true;
            }
        }

        assertTrue(hasDecimal, "模板应包含DECIMAL示例");
        assertTrue(hasDatetime, "模板应包含DATETIME示例");
        assertTrue(hasText, "模板应包含TEXT示例");
        assertTrue(hasBlob, "模板应包含BLOB示例");
        assertTrue(hasJson, "模板应包含JSON示例");
    }

    @Test
    @DisplayName("生成空模板")
    void testGenerateEmptyTemplate() throws IOException {
        byte[] template = ExcelTemplateGenerator.generateEmptyTemplate();

        assertNotNull(template);
        assertTrue(template.length > 0, "模板字节数组不应为空");

        // 验证可以读取Excel但无数据行
        List<ImportExcel> data = EasyExcel.read(new ByteArrayInputStream(template))
                .head(ImportExcel.class)
                .sheet()
                .doReadSync();

        assertNotNull(data);
        assertTrue(data.isEmpty(), "空模板不应包含数据行");
    }

    @Test
    @DisplayName("模板应包含必要字段")
    void testTemplateContainsRequiredFields() throws IOException {
        byte[] template = ExcelTemplateGenerator.generateTemplate();

        List<ImportExcel> data = EasyExcel.read(new ByteArrayInputStream(template))
                .head(ImportExcel.class)
                .sheet()
                .doReadSync();

        assertFalse(data.isEmpty(), "模板应有数据");

        // 检查第一行数据（通常是id字段）
        ImportExcel firstRow = data.get(0);
        assertNotNull(firstRow.getFiledName(), "字段名不应为空");
        assertNotNull(firstRow.getDataType(), "数据类型不应为空");
    }

    @Test
    @DisplayName("模板示例数据验证")
    void testTemplateExampleData() throws IOException {
        byte[] template = ExcelTemplateGenerator.generateTemplate();

        List<ImportExcel> data = EasyExcel.read(new ByteArrayInputStream(template))
                .head(ImportExcel.class)
                .sheet()
                .doReadSync();

        // 查找关键字段
        boolean hasIdField = false;
        boolean hasUserNameField = false;
        boolean hasBalanceField = false;

        for (ImportExcel row : data) {
            if ("id".equals(row.getFiledName())) {
                hasIdField = true;
                assertEquals("BIGINT", row.getDataType());
                assertEquals("Y", row.getNotNull());
                assertEquals("Y", row.getKey());
            }
            if ("user_name".equals(row.getFiledName())) {
                hasUserNameField = true;
                assertEquals("VARCHAR", row.getDataType());
                assertEquals("50", row.getLength());
            }
            if ("balance".equals(row.getFiledName())) {
                hasBalanceField = true;
                assertEquals("DECIMAL", row.getDataType());
                assertEquals("10,2", row.getLength());
            }
        }

        assertTrue(hasIdField, "模板应有id字段示例");
        assertTrue(hasUserNameField, "模板应有user_name字段示例");
        assertTrue(hasBalanceField, "模板应有balance字段示例");
    }

    @Test
    @DisplayName("两种模板生成方法都应成功")
    void testBothTemplateMethods() throws IOException {
        byte[] templateWithData = ExcelTemplateGenerator.generateTemplate();
        byte[] emptyTemplate = ExcelTemplateGenerator.generateEmptyTemplate();

        assertNotNull(templateWithData);
        assertNotNull(emptyTemplate);
        assertTrue(templateWithData.length > 0);
        assertTrue(emptyTemplate.length > 0);

        // 带数据的模板应该更大
        assertTrue(templateWithData.length > emptyTemplate.length,
                "带示例数据的模板应该比空模板大");
    }
}
