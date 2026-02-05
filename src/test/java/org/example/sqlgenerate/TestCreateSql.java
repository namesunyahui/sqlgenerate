package org.example.sqlgenerate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * SQL生成服务集成测试
 *
 * 注意：当前API已更改为使用MultipartFile上传Excel文件。
 * 完整的API测试需要通过Swagger UI或Postman进行手动测试。
 *
 * 测试步骤：
 * 1. 启动应用
 * 2. 访问 /sql/download/template 下载Excel模板
 * 3. 填写模板数据
 * 4. 调用 /sql/generate?file={excel}&tableName=t_user&database=mysql 生成SQL
 */
@SpringBootTest
@Disabled("需要手动测试 - 需要上传Excel文件")
public class TestCreateSql {

    @Test
    public void contextLoads() {
        // 测试Spring上下文可以正常加载
    }
}
