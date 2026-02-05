# API 文档

## 基础信息

- **Base URL**: `http://localhost:8080`
- **Content-Type**: `multipart/form-data`
- **响应格式**: `text/plain` (SQL)

---

## 接口列表

### 1. 下载Excel模板

```http
GET /sql/download/template
```

**功能描述**: 下载包含表结构定义示例的Excel模板文件

**请求参数**: 无

**响应**:

- **Content-Type**: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- **Content-Disposition**: `attachment; filename="sql_template.xlsx"`

**响应示例**:

```bash
curl -O http://localhost:8080/sql/download/template
```

---

### 2. 生成SQL（通用接口）

```http
POST /sql/generate
```

**功能描述**: 根据Excel文件和参数生成指定数据库的SQL语句

**请求参数** (multipart/form-data):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| file | File | ✅ | Excel文件 (.xlsx/.xls) |
| tableName | String | ✅ | 目标表名 |
| tableRemark | String | ❌ | 表注释/备注 |
| database | String | ✅ | 数据库类型 (mysql/oracle/postgresql/sqlserver) |

**请求示例** (curl):

```bash
curl -X POST http://localhost:8080/sql/generate \
  -F "file=@/path/to/table.xlsx" \
  -F "tableName=t_user" \
  -F "tableRemark=用户表" \
  -F "database=mysql"
```

**请求示例** (JavaScript):

```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);
formData.append('tableName', 't_user');
formData.append('tableRemark', '用户表');
formData.append('database', 'mysql');

fetch('/sql/generate', {
    method: 'POST',
    body: formData
})
.then(response => response.text())
.then(sql => console.log(sql));
```

**成功响应** (200 OK):

```sql
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  PRIMARY KEY (`id`)
) DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 表注释
ALTER TABLE `t_user` COMMENT '用户表';
```

**错误响应**:

| HTTP状态 | 错误类型 | 说明 |
|----------|----------|------|
| 400 | 参数验证失败 | 表名为空、文件为空等 |
| 400 | SQL注入风险 | 表名/列名包含非法字符 |
| 400 | 数据类型错误 | Excel格式不正确 |
| 500 | 文件处理失败 | Excel解析错误 |

**错误响应示例**:

```json
{
  "timestamp": "2026-02-05T15:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "表名不能为空",
  "path": "/sql/generate"
}
```

---

### 3. 生成MySQL SQL

```http
POST /sql/generate/mysql
```

**功能描述**: 生成MySQL数据库SQL语句（等同于 database=mysql 的通用接口）

**请求参数**: 同通用接口，无需指定 database 参数

**响应特性**:
- 使用反引号 `` ` `` 包裹标识符
- 支持 AUTO_INCREMENT
- 支持 FULLTEXT 索引
- 字符集: utf8mb4
- 排序规则: utf8mb4_unicode_ci

**请求示例**:

```bash
curl -X POST http://localhost:8080/sql/generate/mysql \
  -F "file=@table.xlsx" \
  -F "tableName=t_user" \
  -F "tableRemark=用户表"
```

---

### 4. 生成Oracle SQL

```http
POST /sql/generate/oracle
```

**功能描述**: 生成Oracle数据库SQL语句

**请求参数**: 同通用接口，无需指定 database 参数

**响应特性**:
- 使用双引号 `"` 包裹标识符
- 使用 SEQUENCE + TRIGGER 实现自增
- 使用 COMMENT ON 添加注释
- 不支持 FULLTEXT 索引

**请求示例**:

```bash
curl -X POST http://localhost:8080/sql/generate/oracle \
  -F "file=@table.xlsx" \
  -F "tableName=t_user" \
  -F "tableRemark=用户表"
```

---

### 5. 生成PostgreSQL SQL

```http
POST /sql/generate/postgresql
```

**功能描述**: 生成PostgreSQL数据库SQL语句

**请求参数**: 同通用接口，无需指定 database 参数

**响应特性**:
- 使用双引号 `"` 包裹标识符
- 使用 SEQUENCE 实现自增
- 使用 COMMENT ON 添加注释
- 支持 JSONB 类型

**请求示例**:

```bash
curl -X POST http://localhost:8080/sql/generate/postgresql \
  -F "file=@table.xlsx" \
  -F "tableName=t_user" \
  -F "tableRemark=用户表"
```

---

### 6. 生成SQL Server SQL

```http
POST /sql/generate/sqlserver
```

**功能描述**: 生成SQL Server数据库SQL语句

**请求参数**: 同通用接口，无需指定 database 参数

**响应特性**:
- 使用方括号 `[]` 包裹标识符
- 使用 IDENTITY 实现自增
- 使用 sp_addextendedproperty 添加注释
- 使用 NONCLUSTERED 索引

**请求示例**:

```bash
curl -X POST http://localhost:8080/sql/generate/sqlserver \
  -F "file=@table.xlsx" \
  -F "tableName=t_user" \
  -F "tableRemark=用户表"
```

---

## 数据类型映射

### MySQL 数据类型映射

| Excel类型 | MySQL类型 | 示例 |
|-----------|-----------|------|
| TINYINT | TINYINT | `TINYINT` |
| SMALLINT | SMALLINT | `SMALLINT` |
| INT/INTEGER | INT | `INT(11)` |
| BIGINT | BIGINT | `BIGINT(20)` |
| DECIMAL/NUMERIC | DECIMAL(M,D) | `DECIMAL(10,2)` |
| VARCHAR | VARCHAR(N) | `VARCHAR(255)` |
| CHAR | CHAR(N) | `CHAR(1)` |
| TEXT | TEXT | `TEXT` |
| BLOB | BLOB | `BLOB` |
| DATE | DATE | `DATE` |
| DATETIME | DATETIME | `DATETIME` |
| TIMESTAMP | TIMESTAMP | `TIMESTAMP` |
| BOOLEAN/BOOL | TINYINT(1) | `TINYINT(1)` |
| JSON | JSON | `JSON` |

### Oracle 数据类型映射

| Excel类型 | Oracle类型 | 示例 |
|-----------|-----------|------|
| TINYINT/SMALLINT | SMALLINT | `SMALLINT` |
| INT/INTEGER | INTEGER | `INTEGER` |
| BIGINT | INTEGER | `INTEGER` |
| DECIMAL/NUMERIC | NUMBER(M,D) | `NUMBER(10,2)` |
| VARCHAR | VARCHAR2(N) | `VARCHAR2(50)` |
| CHAR | CHAR(N) | `CHAR(1)` |
| TEXT | CLOB | `CLOB` |
| BLOB | BLOB | `BLOB` |
| DATE | DATE | `DATE` |
| DATETIME | DATE | `DATE` |
| TIMESTAMP | TIMESTAMP | `TIMESTAMP` |
| BOOLEAN | NUMBER(1) | `NUMBER(1)` |

### PostgreSQL 数据类型映射

| Excel类型 | PostgreSQL类型 | 示例 |
|-----------|---------------|------|
| TINYINT/SMALLINT | SMALLINT | `SMALLINT` |
| INT/INTEGER | INTEGER | `INTEGER` |
| BIGINT | BIGINT | `BIGINT` |
| DECIMAL/NUMERIC | NUMERIC(M,D) | `NUMERIC(10,2)` |
| VARCHAR | VARCHAR(N) | `VARCHAR(255)` |
| CHAR | CHAR(N) | `CHAR(1)` |
| TEXT | TEXT | `TEXT` |
| BLOB | BYTEA | `BYTEA` |
| DATE | DATE | `DATE` |
| DATETIME/TIMESTAMP | TIMESTAMP | `TIMESTAMP` |
| BOOLEAN/BOOL | BOOLEAN | `BOOLEAN` |
| JSON | JSONB | `JSONB` |

### SQL Server 数据类型映射

| Excel类型 | SQL Server类型 | 示例 |
|-----------|---------------|------|
| TINYINT | TINYINT | `TINYINT` |
| SMALLINT | SMALLINT | `SMALLINT` |
| INT/INTEGER | INT | `INT` |
| BIGINT | BIGINT | `BIGINT` |
| DECIMAL/NUMERIC | DECIMAL(M,D) | `DECIMAL(10,2)` |
| VARCHAR | VARCHAR(N) | `VARCHAR(50)` |
| CHAR | CHAR(N) | `CHAR(10)` |
| TEXT | VARCHAR(MAX) | `VARCHAR(MAX)` |
| BLOB | VARBINARY(MAX) | `VARBINARY(MAX)` |
| DATE | DATE | `DATE` |
| DATETIME | DATETIME2 | `DATETIME2` |
| TIMESTAMP | DATETIME2 | `DATETIME2` |
| BOOLEAN/BOOL | BIT | `BIT` |
| JSON | NVARCHAR(MAX) | `NVARCHAR(MAX)` |

---

## 错误码说明

| 错误码 | 说明 | 示例 |
|--------|------|------|
| VALIDATION_ERROR | 参数验证失败 | 表名为空、文件为空 |
| SQL_INJECTION_RISK | SQL注入风险 | 表名包含关键字 |
| FILE_PROCESSING_ERROR | 文件处理失败 | Excel解析错误 |
| DATA_PARSING_ERROR | 数据解析错误 | Excel格式错误 |
| UNSUPPORTED_DATABASE | 不支持的数据库 | 数据库类型无效 |

---

## 使用示例

### Python 示例

```python
import requests

url = "http://localhost:8080/sql/generate"

files = {'file': open('table.xlsx', 'rb')}
data = {
    'tableName': 't_user',
    'tableRemark': '用户表',
    'database': 'mysql'
}

response = requests.post(url, files=files, data=data)
print(response.text)
```

### Java 示例

```java
RestTemplate restTemplate = new RestTemplate();

HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.MULTIPART_FORM_DATA);

MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
body.add("file", new FileSystemResource("table.xlsx"));
body.add("tableName", "t_user");
body.add("tableRemark", "用户表");
body.add("database", "mysql");

HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

String sql = restTemplate.postForObject("/sql/generate", requestEntity, String.class);
System.out.println(sql);
```

### JavaScript/Node.js 示例

```javascript
const FormData = require('form-data');
const fs = require('fs');
const axios = require('axios');

const form = new FormData();
form.append('file', fs.createReadStream('table.xlsx'));
form.append('tableName', 't_user');
form.append('tableRemark', '用户表');
form.append('database', 'mysql');

axios.post('http://localhost:8080/sql/generate', form, {
  headers: form.getHeaders()
})
.then(response => {
  console.log(response.data);
});
```
