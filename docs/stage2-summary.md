# 阶段2重构思路

## 问题

阶段1解决了安全问题，代码质量问题还在：

1. `generateMysqlSql` 和 `generateOracleSql` 重复80%
2. 方法名叫 `extracted`，没意义
3. 硬编码字符串：`"mysql"`, `"oracle"`
4. if-else判断数据库类型
5. 新增数据库要改多处

## 方案

### 1. 枚举替代字符串

新建 `DatabaseType` 枚举：

```java
public enum DatabaseType {
    MYSQL("mysql", "MySQL"),
    ORACLE("oracle", "Oracle");
}
```

好处：
- 类型安全
- IDE自动补全
- 集中管理
- 方便扩展

### 2. 策略模式

核心思路：不同数据库的SQL生成逻辑分别实现，通过接口统一调用。

**策略接口** `DatabaseSqlStrategy` 定义6个方法：
- `generateCreateTableSql` - 生成CREATE TABLE
- `generateTableCommentSql` - 生成表注释
- `generateColumnCommentsSql` - 生成列注释
- `quoteIdentifier` - 包裹标识符
- `buildColumnDefinition` - 构建列定义
- `getSupportedDatabaseType` - 支持的数据库类型

**实现类**：
- `MysqlSqlStrategy` - 用反引号包裹
- `OracleSqlStrategy` - 用双引号包裹

### 3. 服务层改造

`GenerateSqlServiceImpl` 重写：

**原来**：
```java
if (database.equals("mysql")) {
    return generateMysqlSql(createFrom);
} else if (database.equals("oracle")) {
    return generateOracleSql(createFrom);
}
```

**现在**：
```java
DatabaseType type = DatabaseType.fromCode(code);
DatabaseSqlStrategy strategy = strategyMap.get(type);
return generateSqlWithStrategy(createFrom, columns, strategy);
```

用 `Map<DatabaseType, DatabaseSqlStrategy>` 存策略，查表获取。

### 4. 抽取公共方法

把重复逻辑抽出来：
- `readExcelData` - 读Excel
- `validateColumns` - 校验列
- `generateSqlWithStrategy` - 调策略生成SQL

## 对比

| 方面 | 之前 | 之后 |
|------|------|------|
| 代码行数 | 240+ | 156 |
| 重复代码 | 大量 | 无 |
| 方法名 | `extracted` | `buildDataTypeDefinition` |
| 数据库判断 | if-else | 枚举+策略 |
| 扩展性 | 改多处 | 加策略类 |

## 方法改名

`extracted` → `buildDataTypeDefinition`，名字就能看出用途。

## 扩展性

支持PostgreSQL只需：
1. 枚举加 `POSTGRESQL`
2. 新建 `PostgreSqlStrategy`
3. `init()` 注册策略

服务类不用动。

## 差异

两种策略的差异在标识符包裹：
- MySQL：`` `table_name` ``
- Oracle：`"table_name"`

列注释也不同：
- MySQL：CREATE TABLE里用COMMENT
- Oracle：COMMENT ON COLUMN单独语句

策略模式封装了这些差异。
