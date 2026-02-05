# 阶段1修复思路

## 问题分析

代码审查发现了个安全隐患：原有实现直接把用户输入拼接到SQL语句里，没做校验。表名、字段名都直接进SQL字符串。攻击者传个恶意值就能执行任意SQL。

异常处理也有问题。IOException只记日志就返回空字符串，用户不知道出错了。

## 解决方案

### 1. 异常体系

新建了 `SqlGenerationException`，定义四种错误类型：

- SQL_INJECTION_ERROR - 检测到注入攻击
- VALIDATION_ERROR - 参数校验失败
- FILE_PROCESSING_ERROR - 文件读取问题
- DATA_PARSING_ERROR - 数据解析错误

用静态工厂方法创建异常，调用起来简洁。

### 2. SQL校验工具

`SqlValidator` 做几件事：

**关键字检测** - 维护60多个SQL关键字，表名或字段名包含这些关键字就直接拒绝。

**注入模式检测** - 检测20多种攻击模式，比如 `--`、`;`、`' or '1'='1` 这些经典注入。

**标识符校验** - 用正则 `^[a-zA-Z_][a-zA-Z0-9_]*$` 限制标识符格式，只能字母或下划线开头，后面跟字母数字下划线。MySQL和Oracle都适用。

**字符串转义** - 单引号替换成两个单引号。

### 3. 参数校验

`generateSql` 方法开头调用 `validateCreateFrom`，检查：

1. 请求对象非空
2. 文件ID
3. 表名（长度、关键字、注入模式、格式）
4. 表注释

读取Excel后还要遍历所有字段名校验。

### 4. SQL生成防护

MySQL用反引号包裹：
```java
"DROP TABLE IF EXISTS `" + tableName + "`"
```

Oracle用双引号包裹：
```java
"CREATE TABLE \"" + tableName + "\""
```

特殊字符会被当成标识符处理。

### 5. 异常处理

原来这样：
```java
} catch (IOException e) {
    logger.error("Failed to read the file", e);
}
return sqlBuilder.toString(); // 可能返回空
```

现在这样：
```java
} catch (IOException e) {
    logger.error("Failed: {}", fileId, e);
    throw SqlGenerationException.fileProcessing("无法读取文件，检查ID", e);
}
```

异常向上抛，全局处理器统一处理。

### 6. 全局异常处理

`MyExceptionHandler` 新增两个处理器。

`SqlGenerationException` 返回结构化错误：
```json
{
  "code": "SQL_INJECTION_ERROR",
  "message": "检测到注入风险",
  "success": false
}
```

通用处理器捕获剩余异常，避免泄露敏感信息。

## 效果

恶意表名会被拦截返回明确错误。异常信息友好。安全性提升。

## 接下来

`generateMysqlSql` 和 `generateOracleSql` 还有重复逻辑。阶段2会重构。
