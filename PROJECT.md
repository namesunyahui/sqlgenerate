# SQL Generator - 数据库DDL生成工具

> 基于Excel表结构定义，自动生成多数据库DDL语句的Spring Boot应用

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-green.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📋 目录

- [功能特性](#功能特性)
- [快速开始](#快速开始)
- [支持的数据库](#支持的数据库)
- [API文档](#api文档)
- [Excel模板格式](#excel模板格式)
- [前端界面](#前端界面)
- [技术架构](#技术架构)
- [开发指南](#开发指南)
- [部署说明](#部署说明)
- [常见问题](#常见问题)

---

## ✨ 功能特性

### 核心功能

- 📊 **Excel导入**: 通过Excel文件定义表结构，无需手动编写SQL
- 🎯 **多数据库支持**: 一份Excel，生成4种数据库SQL
- 🔒 **安全防护**: SQL注入检测，输入验证，异常处理
- 🎨 **语法高亮**: 自动SQL语法高亮显示
- 📋 **一键复制**: 快捷复制到剪贴板
- 💾 **文件下载**: 下载生成的SQL文件
- 📥 **模板下载**: 内置Excel模板下载

### SQL生成能力

| 特性 | MySQL | Oracle | PostgreSQL | SQL Server |
|------|-------|--------|------------|------------|
| 数据类型映射 | ✅ 30+ | ✅ 30+ | ✅ 30+ | ✅ 30+ |
| DECIMAL精度 | ✅ | ✅ | ✅ | ✅ |
| 主键约束 | ✅ | ✅ | ✅ | ✅ |
| 非空约束 | ✅ | ✅ | ✅ | ✅ |
| 默认值 | ✅ | ✅ | ✅ | ✅ |
| 列注释 | ✅ | ✅ | ✅ | ✅ |
| 表注释 | ✅ | ✅ | ✅ | ✅ |
| 普通索引 | ✅ | ✅ | ✅ | ✅ |
| 唯一索引 | ✅ | ✅ | ✅ | ✅ |
| 全文索引 | ✅ | ❌ | ❌ | ❌ |
| 联合索引 | ✅ | ✅ | ✅ | ✅ |
| 外键约束 | ✅ | ✅ | ✅ | ✅ |
| 自增序列 | AUTO_INCREMENT | SEQUENCE+TRIGGER | SEQUENCE | IDENTITY |

---

## 🚀 快速开始

### 环境要求

- **JDK**: 17+
- **Maven**: 3.6+
- **MySQL**: 8.0+ (可选)
- **Oracle**: 11g+ (可选)
- **PostgreSQL**: 12+ (可选)
- **SQL Server**: 2017+ (可选)

### 克隆项目

```bash
git clone https://github.com/yourusername/sqlgenerate.git
cd sqlgenerate
```

### 编译运行

```bash
# 编译项目
mvn clean package

# 运行应用
java -jar target/sqlgenerate-1.0.0.jar

# 或使用Maven直接运行
mvn spring-boot:run
```

### 访问应用

```
主页: http://localhost:8080/sql-generator.html
```

---

## 🗄️ 支持的数据库

### MySQL 8.0+

```sql
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `balance` DECIMAL(10,2) NOT NULL DEFAULT '0.00' COMMENT '账户余额',
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

### Oracle 11g+

```sql
CREATE TABLE "t_user" (
  "id" INTEGER NOT NULL,
  "username" VARCHAR2(50) NOT NULL,
  "balance" NUMBER(10,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY ("id")
);

COMMENT ON TABLE "t_user" IS '用户表';
COMMENT ON COLUMN "t_user"."id" IS '主键ID';

CREATE SEQUENCE "seq_t_user_id" START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;
```

### PostgreSQL 12+

```sql
DROP TABLE IF EXISTS "t_user";
CREATE TABLE "t_user" (
  "id" BIGINT NOT NULL,
  "username" VARCHAR(50) NOT NULL,
  "balance" NUMERIC(10,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY ("id")
);

COMMENT ON TABLE "t_user" IS '用户表';
COMMENT ON COLUMN "t_user"."id" IS '主键ID';
```

### SQL Server 2017+

```sql
IF OBJECT_ID('[t_user]', 'U') IS NOT NULL
    DROP TABLE [t_user];
CREATE TABLE [t_user] (
    [id] BIGINT NOT NULL IDENTITY(1,1),
    [username] VARCHAR(50) NOT NULL,
    [balance] DECIMAL(10,2) NOT NULL DEFAULT '0.00',
    PRIMARY KEY ([id])
);

EXEC sp_addextendedproperty
    @name = N'MS_Description',
    @value = N'用户表',
    @level0type = N'SCHEMA', @level0name = 'dbo',
    @level1type = N'TABLE', @level1name = N't_user';
```

---

## 📡 API文档

### 1. 下载Excel模板

```http
GET /sql/download/template
```

**响应**: Excel文件 (`sql_template.xlsx`)

### 2. 生成SQL（通用）

```http
POST /sql/generate
Content-Type: multipart/form-data
```

**请求参数**:

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | ✅ | Excel文件 |
| tableName | String | ✅ | 表名 |
| tableRemark | String | ❌ | 表注释 |
| database | String | ✅ | 数据库类型 |

**请求示例**:

```bash
curl -X POST http://localhost:8080/sql/generate \
  -F "file=@table.xlsx" \
  -F "tableName=t_user" \
  -F "tableRemark=用户表" \
  -F "database=mysql"
```

**响应**: 生成的SQL文本

### 3. 生成MySQL SQL

```http
POST /sql/generate/mysql
Content-Type: multipart/form-data
```

### 4. 生成Oracle SQL

```http
POST /sql/generate/oracle
Content-Type: multipart/form-data
```

### 5. 生成PostgreSQL SQL

```http
POST /sql/generate/postgresql
Content-Type: multipart/form-data
```

### 6. 生成SQL Server SQL

```http
POST /sql/generate/sqlserver
Content-Type: multipart/form-data
```

---

## 📊 Excel模板格式

### 模板结构

| 字段名 | 类型 | 长度 | 非空 | 主键 | 默认值 | 备注 | 索引名 | 索引类型 | 外键表 | 外键列 | 自增 |
|--------|------|------|------|------|--------|------|--------|----------|--------|--------|------|
| id | BIGINT | | Y | Y | | 主键ID | | | | | Y |
| username | VARCHAR | 50 | Y | | | 用户名 | idx_username | NORMAL | | | |
| email | VARCHAR | 100 | N | | | 邮箱 | uk_email | UNIQUE | | | |
| dept_id | BIGINT | | N | | | 部门ID | idx_dept | NORMAL | t_dept | id | |

### 字段说明

| Excel列 | 说明 | 示例 |
|---------|------|------|
| 字段名 | 列名称 | id, username, create_time |
| 类型 | 数据类型 | VARCHAR, BIGINT, DECIMAL |
| 长度 | 类型长度 | 50, 10,2 |
| 非空 | 是否非空 | Y=是, N/空=否 |
| 主键 | 是否主键 | Y=是, N/空=否 |
| 默认值 | 默认值 | 0, CURRENT_TIMESTAMP |
| 备注 | 列注释 | 用户名 |
| 索引名 | 索引名称 | idx_username, uk_email |
| 索引类型 | NORMAL/UNIQUE/FULLTEXT | NORMAL |
| 外键表 | 关联表名 | t_dept |
| 外键列 | 关联列名 | id |
| 自增 | 是否自增 | Y=是 |

### 支持的数据类型

| 类型分类 | 支持的类型 |
|----------|-----------|
| 整数 | TINYINT, SMALLINT, INT, INTEGER, BIGINT |
| 浮点 | FLOAT, DOUBLE, DECIMAL, NUMERIC |
| 字符串 | CHAR, VARCHAR, TEXT, MEDIUMTEXT, LONGTEXT |
| 二进制 | BINARY, VARBINARY, BLOB |
| 日期 | DATE, TIME, DATETIME, TIMESTAMP, YEAR |
| 布尔 | BOOLEAN, BOOL |
| JSON | JSON |

---

## 🎨 前端界面

### 访问地址

```
http://localhost:8080/sql-generator.html
```

### 界面特性

- 🎯 **步骤引导**: 清晰的4步操作流程
- 🎨 **工业风格**: IDE暗色主题，语法高亮
- 📁 **拖放上传**: 支持拖放Excel文件
- 🔄 **实时反馈**: 加载动画、Toast通知
- ⌨️ **快捷键**: Ctrl+Enter生成，Ctrl+Shift+C复制
- 📱 **响应式**: 支持桌面、平板、手机

### 使用流程

```
1. 下载模板 → 2. 填写表结构 → 3. 上传文件 → 4. 选择数据库 → 5. 生成SQL
```

---

## 🏗️ 技术架构

### 后端技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.3.3 | 应用框架 |
| Spring Web | 6.x | REST API |
| EasyExcel | 4.x | Excel解析 |
| SLF4J | 2.x | 日志 |
| JUnit | 5.x | 单元测试 |
| Maven | 3.x | 构建工具 |

### 项目结构

```
sqlgenerate/
├── src/main/java/org/example/sqlgenerate/
│   ├── SqlgenerateApplication.java          # 启动类
│   ├── createSql/
│   │   ├── codm/                            # 枚举和常量
│   │   │   ├── DatabaseType.java            # 数据库类型枚举
│   │   │   └── YesOrNoFlag.java             # 是否标志枚举
│   │   ├── controller/
│   │   │   └── SqlGenerateController.java   # API控制器
│   │   ├── exception/
│   │   │   ├── SqlGenerationException.java  # 自定义异常
│   │   │   └── MyExceptionHandler.java      # 全局异常处理
│   │   ├── mapper/                          # 数据类型映射
│   │   │   ├── DataTypeMapper.java          # 映射器接口
│   │   │   ├── MysqlDataTypeMapper.java     # MySQL映射
│   │   │   ├── OracleDataTypeMapper.java    # Oracle映射
│   │   │   ├── PostgreSqlDataTypeMapper.java # PostgreSQL映射
│   │   │   └── SqlServerDataTypeMapper.java # SQL Server映射
│   │   ├── model/                           # 数据模型
│   │   │   ├── CreateFrom.java              # 请求参数
│   │   │   ├── ImportExcel.java             # Excel行模型
│   │   │   └── IndexDefinition.java         # 索引定义
│   │   ├── service/
│   │   │   ├── GenerateSqlService.java      # 服务接口
│   │   │   └── Impl/
│   │   │       └── GenerateSqlServiceImpl.java # 服务实现
│   │   ├── strategy/                        # 策略模式
│   │   │   ├── DatabaseSqlStrategy.java     # 策略接口
│   │   │   └── Impl/
│   │   │       ├── MysqlSqlStrategy.java     # MySQL策略
│   │   │       ├── OracleSqlStrategy.java    # Oracle策略
│   │   │       ├── PostgreSqlStrategy.java   # PostgreSQL策略
│   │   │       └── SqlServerStrategy.java    # SQL Server策略
│   │   └── util/
│   │       ├── SqlValidator.java            # SQL校验工具
│   │       └── ExcelTemplateGenerator.java  # Excel模板生成
│   └── test/                                 # 测试代码
│       ├── createSql/
│       │   ├── mapper/
│       │   ├── strategy/
│       │   └── util/
│       └── SqlgenerateApplicationTests.java
├── src/main/resources/
│   ├── application.yml                       # 应用配置
│   └── static/
│       └── sql-generator.html                # 前端页面
└── docs/                                     # 项目文档
    ├── README.md                             # 文档索引
    ├── task_plan.md                          # 优化计划
    ├── findings.md                           # 代码分析
    ├── progress.md                           # 工作日志
    ├── stage1-summary.md                     # 阶段1总结
    ├── stage2-summary.md                     # 阶段2总结
    ├── stage3-summary.md                     # 阶段3总结
    ├── stage4-summary.md                     # 阶段4总结
    ├── stage5-summary.md                     # 阶段5总结
    └── redesign-summary.md                   # 重新设计总结
```

### 设计模式

| 模式 | 应用场景 |
|------|----------|
| **策略模式** | 多数据库SQL生成 |
| **工厂模式** | 策略实例创建 |
| **模板方法** | SQL生成流程 |
| **单一职责** | 每个类职责明确 |
| **开闭原则** | 易于扩展新数据库 |

---

## 👨‍💻 开发指南

### 添加新的数据库支持

#### 1. 添加数据库类型枚举

```java
// DatabaseType.java
public enum DatabaseType {
    // 现有类型...
    NEWDB("newdb", "NewDatabase");  // 新增
}
```

#### 2. 创建数据类型映射器

```java
// NewDbDataTypeMapper.java
public class NewDbDataTypeMapper implements DataTypeMapper {
    @Override
    public String mapType(ImportExcel column) {
        // 实现类型映射逻辑
    }
}
```

#### 3. 创建SQL策略

```java
// NewDbSqlStrategy.java
public class NewDbSqlStrategy implements DatabaseSqlStrategy {
    @Override
    public String generateCreateTableSql(CreateFrom createFrom, List<ImportExcel> columns) {
        // 实现SQL生成逻辑
    }

    @Override
    public String quoteIdentifier(String identifier) {
        // 返回标识符引用方式
    }
}
```

#### 4. 注册策略

```java
// GenerateSqlServiceImpl.java
@PostConstruct
public void init() {
    strategyMap.put(DatabaseType.NEWDB, new NewDbSqlStrategy());
}
```

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=SqlValidatorTest

# 运行特定测试方法
mvn test -Dtest=SqlStrategyTest#testMySQL
```

### 代码规范

- 使用策略模式处理数据库差异
- 所有公共方法添加JavaDoc注释
- 异常使用自定义SqlGenerationException
- 输入验证使用SqlValidator
- 字符串转义使用SqlValidator.escapeStringLiteral()

---

## 🚀 部署说明

### Docker部署

#### Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app
COPY target/sqlgenerate-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 构建和运行

```bash
# 构建镜像
docker build -t sql-generator:latest .

# 运行容器
docker run -d -p 8080:8080 --name sql-generator sql-generator:latest
```

### Docker Compose

```yaml
version: '3.8'
services:
  sql-generator:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
    restart: unless-stopped
```

### 传统部署

```bash
# 打包
mvn clean package -DskipTests

# 上传到服务器
scp target/sqlgenerate-1.0.0.jar user@server:/opt/app/

# 运行
nohup java -jar /opt/app/sqlgenerate-1.0.0.jar > app.log 2>&1 &
```

### Nginx反向代理

```nginx
server {
    listen 80;
    server_name sql.example.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

---

## ❓ 常见问题

### Q1: 上传Excel后提示"无法读取文件"

**A**: 检查以下几点：
1. Excel文件格式是否为.xlsx或.xls
2. Excel文件是否包含"表结构定义"工作表
3. 列头是否与模板一致

### Q2: 生成的SQL执行报错

**A**: 可能原因：
1. 表名或列名包含数据库保留字
2. 数据类型不支持
3. 长度参数不正确

### Q3: 如何添加自定义数据类型？

**A**: 在对应的DataTypeMapper中添加映射逻辑：

```java
case "CUSTOM_TYPE" -> "MAPPED_TYPE";
```

### Q4: 索引和外键不生效？

**A**: 检查：
1. 索引名是否为空
2. 索引类型是否正确（NORMAL/UNIQUE/FULLTEXT）
3. 外键表和外键列是否都填写

### Q5: 如何批量生成多个表？

**A**: 目前每次生成一个表。如需批量处理：
1. 每个表一个Excel文件
2. 或在Excel中添加"表名"列
3. 或调用API循环处理

---

## 📝 更新日志

### v2.0.0 (2026-02-05)

- ✨ 新增PostgreSQL支持
- ✨ 新增SQL Server支持
- 🎨 全新工业终端风格前端界面
- 🔒 增强SQL注入防护
- 📚 完善项目文档

### v1.0.0 (2026-02-04)

- 🎉 初始版本发布
- ✅ MySQL和Oracle支持
- ✅ 策略模式架构
- ✅ Excel模板下载
- ✅ 索引和外键支持

---

## 📄 许可证

[MIT License](LICENSE)

---

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

---

## 📧 联系方式

- 项目地址: [https://github.com/yourusername/sqlgenerate](https://github.com/yourusername/sqlgenerate)
- 问题反馈: [Issues](https://github.com/yourusername/sqlgenerate/issues)

---

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [EasyExcel](https://github.com/alibaba/easyexcel)
- [JUnit 5](https://junit.org/junit5/)

---

**Made with ❤️ by CodeForge Team**
