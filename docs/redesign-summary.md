# 重新设计文档

## 旧架构问题

原设计依赖MongoDB存储文件，流程绕了一圈：
1. 上传Excel到MongoDB
2. 获得fileId
3. 调用SQL生成接口
4. 从MongoDB读文件
5. 生成SQL

还包含AI聊天功能，和SQL生成没关系。

## 新架构

直接上传，无需存储：
1. 下载Excel模板
2. 填写表结构
3. 上传Excel
4. 生成SQL

## 移除的模块

| 模块 | 原因 |
|------|------|
| mongodb包 | 不存文件了 |
| ai包 | 和SQL生成无关 |
| FileStorageService | 不需要了 |
| FileInfoRepository | 不需要了 |
| AI SDK | 无关 |
| MongoDB依赖 | 不需要 |
| Redis依赖 | 不需要 |
| WebFlux依赖 | 不需要 |

## 保留的模块

| 模块 | 用途 |
|------|------|
| createSql | SQL生成核心 |
| SqlValidator | 安全校验 |
| DatabaseSqlStrategy | 策略模式 |
| SqlGenerationException | 异常处理 |

## 新增功能

| 功能 | 说明 |
|------|------|
| ExcelTemplateGenerator | 生成模板 |
| 模板下载接口 | GET /sql/download/template |
| 文件上传接口 | POST /sql/generate |

## 新API

### 下载模板
```
GET /sql/download/template
返回: Excel文件（带示例）
```

### 生成SQL
```
POST /sql/generate
参数:
  - file: Excel
  - tableName: 表名
  - tableRemark: 注释
  - database: mysql/oracle
返回: SQL语句
```

### 生成MySQL
```
POST /sql/generate/mysql
参数:
  - file: Excel
  - tableName: 表名
  - tableRemark: 注释
返回: MySQL SQL
```

### 生成Oracle
```
POST /sql/generate/oracle
参数:
  - file: Excel
  - tableName: 表名
  - tableRemark: 注释
返回: Oracle SQL
```

## 依赖

移除前：15+个
移除后：6个

- Spring Web
- EasyExcel
- Commons Lang
- Lombok
- SpringDoc
- Knife4j

## Excel格式

| 列 | 说明 | 示例 |
|----|------|------|
| 字段名 | filedName | id |
| 类型 | dataType | BIGINT |
| 长度 | length | 20 |
| 非空 | notNull | Y/N |
| 主键 | key | Y |
| 默认值 | defaultValue | (空) |
| 备注 | remark | 主键ID |

模板带8行示例，用户直接改。

## 代码变更

### 新增
- ExcelTemplateGenerator.java
- 模板下载接口
- generateSqlFromFile方法

### 修改
- CreateFrom加file字段
- Controller重写接口
- Service从文件流读取

### 删除
- mongodb目录
- ai目录
- 相关配置
- 相关依赖

## 安全保持

- SQL注入检测
- 参数校验
- 异常处理
- 策略模式

阶段1、2的改进全保留。
