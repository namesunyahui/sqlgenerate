# 阶段5 - 扩展数据库支持

## 概述

本阶段扩展了SQL生成功能，新增对PostgreSQL和SQL Server数据库的支持。至此，项目已支持4种主流数据库：MySQL、Oracle、PostgreSQL和SQL Server。

## 新增内容

### 1. DatabaseType枚举扩展

在`DatabaseType.java`中新增：

```java
/**
 * PostgreSQL数据库
 */
POSTGRESQL("postgresql", "PostgreSQL"),

/**
 * SQL Server数据库
 */
SQLSERVER("sqlserver", "SQL Server");
```

### 2. PostgreSqlDataTypeMapper.java

PostgreSQL数据类型映射实现：

| Excel类型 | PostgreSQL类型 |
|-----------|---------------|
| TINYINT/SMALLINT | SMALLINT |
| INT/INTEGER | INTEGER |
| BIGINT | BIGINT |
| DECIMAL/NUMERIC | NUMERIC(M,D) |
| VARCHAR | VARCHAR(N) |
| TEXT/MEDIUMTEXT/LONGTEXT | TEXT |
| BLOB/BINARY | BYTEA |
| DATETIME/TIMESTAMP | TIMESTAMP |
| BOOLEAN/BOOL | BOOLEAN |
| JSON | JSONB |

### 3. SqlServerDataTypeMapper.java

SQL Server数据类型映射实现：

| Excel类型 | SQL Server类型 |
|-----------|---------------|
| TINYINT | TINYINT |
| INT/INTEGER | INT |
| BIGINT | BIGINT |
| DECIMAL/NUMERIC | DECIMAL(M,D) |
| VARCHAR | VARCHAR(N) |
| TEXT/MEDIUMTEXT/LONGTEXT | VARCHAR(MAX) |
| BLOB/BINARY | VARBINARY(MAX) |
| DATETIME | DATETIME2 |
| BOOLEAN/BOOL | BIT |
| JSON | NVARCHAR(MAX) |

### 4. PostgreSqlStrategy.java

PostgreSQL策略实现特性：

- **标识符引用**: 使用双引号`"`包裹标识符
- **DROP TABLE**: `DROP TABLE IF EXISTS`
- **表注释**: `COMMENT ON TABLE`
- **列注释**: `COMMENT ON COLUMN`
- **自增序列**: 使用`CREATE SEQUENCE` + `ALTER COLUMN SET DEFAULT nextval()`
- **索引**: 支持`CREATE INDEX`和`CREATE UNIQUE INDEX`

### 5. SqlServerStrategy.java

SQL Server策略实现特性：

- **标识符引用**: 使用方括号`[]`包裹标识符
- **DROP TABLE**: `IF OBJECT_ID(...) IS NOT NULL DROP TABLE`
- **表注释**: 使用`EXEC sp_addextendedproperty`
- **列注释**: 使用`EXEC sp_addextendedproperty`
- **自增**: 使用`IDENTITY(1,1)`
- **索引**: 支持`CREATE NONCLUSTERED INDEX`和`CREATE UNIQUE NONCLUSTERED INDEX`

## SQL生成示例

### PostgreSQL

```sql
DROP TABLE IF EXISTS "t_user";
CREATE TABLE "t_user" (
  "id" BIGINT NOT NULL,
  "username" VARCHAR(50) NOT NULL,
  "balance" NUMERIC(10,2) NOT NULL DEFAULT '0.00',
  "reg_date" TIMESTAMP,
  "description" TEXT,
  PRIMARY KEY ("id")
);

COMMENT ON TABLE "t_user" IS '用户表';
COMMENT ON COLUMN "t_user"."id" IS '主键ID';
COMMENT ON COLUMN "t_user"."username" IS '用户名';

CREATE INDEX idx_username ON "t_user" ("username");
CREATE UNIQUE INDEX uk_email ON "t_user" ("email");
```

### SQL Server

```sql
IF OBJECT_ID('[t_user]', 'U') IS NOT NULL
    DROP TABLE [t_user];
CREATE TABLE [t_user] (
    [id] BIGINT NOT NULL IDENTITY(1,1),
    [username] VARCHAR(50) NOT NULL,
    [balance] DECIMAL(10,2) NOT NULL DEFAULT '0.00',
    [reg_date] DATETIME2,
    [description] VARCHAR(MAX),
    PRIMARY KEY ([id])
);

EXEC sp_addextendedproperty
    @name = N'MS_Description',
    @value = N'用户表',
    @level0type = N'SCHEMA', @level0name = 'dbo',
    @level1type = N'TABLE', @level1name = N't_user';

EXEC sp_addextendedproperty
    @name = N'MS_Description',
    @value = N'主键ID',
    @level0type = N'SCHEMA', @level0name = 'dbo',
    @level1type = N'TABLE', @level1name = N't_user',
    @level2type = N'COLUMN', @level2name = N'id';

CREATE NONCLUSTERED INDEX idx_username ON [t_user] ([username]);
CREATE UNIQUE NONCLUSTERED INDEX uk_email ON [t_user] ([email]);
```

## 数据库特性对比

| 特性 | MySQL | Oracle | PostgreSQL | SQL Server |
|------|-------|--------|------------|------------|
| 标识符引用 | ` ` | " | " | [] |
| DROP语法 | DROP IF EXISTS | 不支持 | DROP IF EXISTS | IF OBJECT_ID |
| 表注释 | COMMENT | COMMENT ON | COMMENT ON | sp_addextendedproperty |
| 列注释 | 行内COMMENT | COMMENT ON | COMMENT ON | sp_addextendedproperty |
| 自增 | AUTO_INCREMENT | SEQUENCE+TRIGGER | SEQUENCE/SERIAL | IDENTITY |
| FULLTEXT索引 | 支持 | 不支持 | 不支持 | 不支持 |
| 唯一索引 | UNIQUE INDEX | UNIQUE INDEX | UNIQUE INDEX | UNIQUE INDEX |

## API使用

```java
// PostgreSQL
CreateFrom createFrom = new CreateFrom();
createFrom.setDatabase("postgresql");
createFrom.setTableName("t_user");
createFrom.setFile(excelFile);
String sql = generateSqlService.generateSqlFromFile(createFrom);

// SQL Server
CreateFrom createFrom = new CreateFrom();
createFrom.setDatabase("sqlserver");
createFrom.setTableName("t_user");
createFrom.setFile(excelFile);
String sql = generateSqlService.generateSqlFromFile(createFrom);
```

## 完成状态

- ✅ DatabaseType枚举扩展
- ✅ PostgreSqlDataTypeMapper
- ✅ SqlServerDataTypeMapper
- ✅ PostgreSqlStrategy
- ✅ SqlServerStrategy
- ✅ GenerateSqlServiceImpl更新
- ✅ 测试更新（50个测试全部通过）

## 文件清单

| 文件 | 类型 |
|------|------|
| DatabaseType.java | 修改（添加2个枚举值） |
| PostgreSqlDataTypeMapper.java | 新建 |
| SqlServerDataTypeMapper.java | 新建 |
| PostgreSqlStrategy.java | 新建 |
| SqlServerStrategy.java | 新建 |
| GenerateSqlServiceImpl.java | 修改（注册新策略） |
| SqlValidatorTest.java | 修改（更新测试用例） |
