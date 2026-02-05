# 阶段3 - 数据类型映射增强

## 问题

之前类型映射太简单：
- 只判断是否包含"date"
- DECIMAL不支持精度
- 没CLOB/BLOB
- MySQL没字符集

## 方案

### 1. 类型映射器接口

`DataTypeMapper` 接口：
```java
String mapType(ImportExcel column);
boolean supportsType(String dataType);
String getDefaultCharset();
String getDefaultCollation();
```

### 2. MySQL映射器

支持类型：

| 类别 | 类型 |
|------|------|
| 整数 | TINYINT, SMALLINT, INT, BIGINT |
| 浮点 | FLOAT, DOUBLE, DECIMAL(p,s) |
| 字符串 | CHAR, VARCHAR, TEXT系列 |
| 二进制 | BINARY, VARBINARY, BLOB系列 |
| 日期 | DATE, DATETIME, TIMESTAMP |
| 其他 | BOOLEAN, JSON |

**DECIMAL精度**：
- `DECIMAL(10,2)` - 10位长2位小数
- `DECIMAL(m)` - 默认2位小数
- `DECIMAL` - 默认(10,2)

**字符集**：`utf8mb4` + `utf8mb4_unicode_ci`

### 3. Oracle映射器

支持类型：

| 类别 | 类型 |
|------|------|
| 数值 | NUMBER(p,s), INTEGER |
| 字符串 | VARCHAR2, NVARCHAR2 |
| 大字段 | CLOB, NCLOB, BLOB |
| 日期 | DATE, TIMESTAMP |

### 4. 策略改造

之前：
```java
if (dataType.contains("date")) {
    return column.getDataType();
}
```

现在：
```java
private final DataTypeMapper dataTypeMapper = new MysqlDataTypeMapper();

private String buildDataTypeDefinition(ImportExcel column) {
    return dataTypeMapper.mapType(column);
}
```

### 5. 模板更新

新增示例：
- DECIMAL(10,2)
- TEXT
- BLOB
- JSON

## 对比

| 方面 | 之前 | 之后 |
|------|------|------|
| 类型数量 | 5种 | 30+种 |
| DECIMAL精度 | 无 | 支持 |
| 大字段 | 无 | CLOB/BLOB |
| 字符集 | 无 | utf8mb4 |

## 映射示例

### MySQL
```
VARCHAR(50)  → VARCHAR(50)
DECIMAL      → DECIMAL(10,2)
TEXT         → TEXT
BLOB         → BLOB
```

### Oracle
```
VARCHAR      → VARCHAR2(50)
DECIMAL      → NUMBER(10,2)
TEXT         → CLOB
BLOB         → BLOB
```

## 完成

- ✅ DataTypeMapper接口
- ✅ MySQL映射器
- ✅ Oracle映射器
- ✅ 策略集成
- ✅ 模板更新
