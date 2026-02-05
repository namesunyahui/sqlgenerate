# 阶段4 - 索引和约束支持

## 概述

本阶段实现了索引和外键约束的自动生成，支持普通索引、唯一索引、联合索引以及外键约束。同时为Oracle数据库添加了自增序列支持。

## 新增模型

### IndexDefinition.java

索引定义模型，包含：
- `indexName`: 索引名称
- `indexType`: 索引类型（NORMAL/UNIQUE/FULLTEXT）
- `columnNames`: 索引列名列表
- `unique`: 是否唯一索引
- `comment`: 索引注释

## ImportExcel模型扩展

新增字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `indexName` | String | 索引名称（可选） |
| `indexType` | String | 索引类型：NORMAL/UNIQUE/FULLTEXT |
| `foreignTable` | String | 外键关联表名（可选） |
| `foreignColumn` | String | 外键关联列名（可选） |
| `autoIncrement` | String | 是否自增（Oracle序列） |

## 策略接口扩展

DatabaseSqlStrategy新增方法：

```java
String generateIndexSql(CreateFrom createFrom, List<ImportExcel> columns);
String generateForeignKeySql(CreateFrom createFrom, List<ImportExcel> columns);
String generateSequenceSql(CreateFrom createFrom, List<ImportExcel> columns);
```

## MySQL实现

### 索引生成

```sql
CREATE INDEX idx_user_name ON t_user (user_name);
CREATE UNIQUE INDEX uk_email ON t_user (email);
CREATE FULLTEXT INDEX ft_content ON t_article (content);
```

支持联合索引：多列使用相同索引名自动合并。

### 外键约束

```sql
ALTER TABLE t_user ADD CONSTRAINT fk_t_user_dept_id
FOREIGN KEY (dept_id) REFERENCES t_dept(id);
```

### 序列

MySQL不需要，使用AUTO_INCREMENT。

## Oracle实现

### 索引生成

```sql
CREATE INDEX idx_user_name ON "t_user" ("user_name");
CREATE UNIQUE INDEX uk_email ON "t_user" ("email");
```

注意：Oracle不支持FULLTEXT索引，不支持索引注释。

### 外键约束

与MySQL相同的语法。

### 自增序列

Oracle需要SEQUENCE + TRIGGER：

```sql
-- 创建序列
CREATE SEQUENCE "seq_t_user_id"
  START WITH 1
  INCREMENT BY 1
  NOCACHE
  NOCYCLE;

-- 创建触发器
CREATE OR REPLACE TRIGGER "trg_t_user_id"
  BEFORE INSERT ON "t_user"
  FOR EACH ROW
BEGIN
  IF :NEW."id" IS NULL THEN
    SELECT "seq_t_user_id".NEXTVAL INTO :NEW."id" FROM DUAL;
  END IF;
END;
```

## Excel模板更新

新增列示例：

| 字段名 | 类型 | 长度 | 非空 | 主键 | 默认值 | 备注 | 索引名 | 索引类型 | 外键表 | 外键列 |
|--------|------|------|------|------|--------|------|--------|----------|--------|--------|
| id | BIGINT | | Y | Y | | 主键ID | | | | |
| user_name | VARCHAR | 50 | Y | | | 用户名 | idx_user_name | NORMAL | | |
| email | VARCHAR | 100 | N | | | 邮箱 | uk_email | UNIQUE | | |
| dept_id | BIGINT | | N | | | 部门ID | idx_dept_id | NORMAL | t_dept | id |

## 索引类型说明

| 类型 | 说明 | MySQL | Oracle |
|------|------|-------|--------|
| NORMAL | 普通索引 | ✅ | ✅ |
| UNIQUE | 唯一索引 | ✅ | ✅ |
| FULLTEXT | 全文索引 | ✅ | ❌ |

## SQL生成顺序

生成的SQL按以下顺序排列：

1. CREATE TABLE
2. 表注释
3. 列注释（Oracle）
4. 自增序列（Oracle）
5. 索引
6. 外键约束

## 完成状态

- ✅ IndexDefinition模型
- ✅ ImportExcel扩展
- ✅ 策略接口扩展
- ✅ MySQL索引/外键实现
- ✅ Oracle索引/外键/序列实现
- ✅ Excel模板更新
- ✅ 50个测试全部通过
