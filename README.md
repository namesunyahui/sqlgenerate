# SQL Generator

写 SQL 建表语句挺烦的，尤其是字段多的时候。这个工具帮你从 Excel 表格定义自动生成建表 SQL，支持 MySQL、Oracle、PostgreSQL 和 SQL Server。

## 为什么做这个

之前做项目时，每次建表都要手写 SQL，字段多了就容易出错。后来想，为什么不从 Excel 直接生成呢？于是就有了这个工具。

## 功能

- 支持 MySQL、Oracle、PostgreSQL、SQL Server 四种数据库
- 上传 Excel 就能生成 SQL，不用手写
- 内置 Excel 模板，填好就能用
- Web 界面直接预览生成的 SQL，带语法高亮
- 一键复制或下载 SQL 文件

## 技术栈

后端用的是 Spring Boot 3.3.3 + Java 17，Excel 解析用的 EasyExcel。前端没用框架，纯 HTML/CSS/JavaScript 写的，界面模仿了终端风格，用起来还顺手。

API 文档集成了 Knife4j，启动后就能看。

## 快速上手

### 环境要求

JDK 17 和 Maven 3.6+ 就行。

### 运行

```bash
git clone https://github.com/yourusername/sqlgenerate.git
cd sqlgenerate
mvn clean package
mvn spring-boot:run
```

启动后打开 http://localhost:8080/sql-generator.html。

想看 API 文档的话，访问 http://localhost:8080/doc.html。

## 怎么用

1. 点击界面上方的下载按钮，获取 Excel 模板
2. 在模板里填写表结构信息（字段名、类型、长度、是否可空、主键、默认值、注释）
3. 把 Excel 拖到上传区域
4. 选一下目标数据库
5. 输入表名和表注释
6. 点生成按钮

## Excel 模板长这样

| 字段名 | 类型 | 长度 | 可空 | 主键 | 默认值 | 注释 |
|--------|------|------|------|------|--------|------|
| id     | BIGINT | - | 否 | 是 | | 主键ID |
| name   | VARCHAR | 100 | 否 | 否 | | 名称 |
| status | TINYINT | - | 是 | 否 | 0 | 状态 |

第一列是字段名，然后是数据类型、长度、能不能为空、是不是主键、默认值和注释。

## API 接口

下载模板：
```
GET /sql/download/template
```

生成 SQL：
```
POST /sql/generate
Content-Type: multipart/form-data

参数:
- file: Excel 文件
- tableName: 表名
- tableRemark: 表注释（可选）
- database: mysql/oracle/postgresql/sqlserver，默认是 mysql
```

## 项目结构

```
sqlgenerate/
├── src/main/java/org/example/sqlgenerate/
│   ├── createSql/
│   │   ├── controller/          # REST API
│   │   ├── service/             # 业务逻辑
│   │   ├── strategy/            # 数据库策略
│   │   ├── mapper/              # 数据类型映射
│   │   ├── model/               # 数据模型
│   │   └── util/                # 工具类
│   ├── config/                  # 配置
│   └── exception/               # 异常处理
└── src/main/resources/
    ├── application.yml          # 配置文件
    └── static/
        └── sql-generator.html   # Web 界面
```

## 设计思路

用策略模式来处理不同数据库的 SQL 生成。`DatabaseSqlStrategy` 接口定义了统一的方法，每个数据库有自己的实现类（`MysqlSqlStrategy`、`OracleSqlStrategy` 等）。`DataTypeMapper` 负责把 Excel 里的类型映射成对应数据库的类型。

这样以后要加新数据库，多写一个实现类就行，不用改现有代码。

## 后续计划

- [ ] 支持索引定义
- [ ] 支持外键关系
- [ ] 再加几种数据库
- [ ] 支持批量生成多个表

## 许可证

MIT

## 问题反馈

有 Bug 或者建议直接提 Issue。
