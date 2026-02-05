# SQL生成功能优化计划

## 目标

优化 `GenerateSqlServiceImpl` 和相关代码，解决SQL注入风险、代码重复、功能缺失等问题，提升代码质量和功能完整性。

## 当前状态

- **项目**: sqlgenerate
- **模块**: createSql (SQL生成模块)
- **主要文件**:
  - `GenerateSqlServiceImpl.java` (服务实现)
  - `SqlGenerateController.java` (控制器)
  - `ImportExcel.java` (Excel模型)
  - `CreateFrom.java` (请求模型)

---

## 阶段计划

### 阶段 1: 立即修复 - 安全问题 [P0]

**状态**: `✅ 完成`

**目标**: 修复严重的安全漏洞

| 任务 | 状态 | 负责文件 |
|------|------|----------|
| 添加SQL关键字校验和标识符转义 | ✅ | SqlValidator.java |
| 添加表名校验（防止SQL注入） | ✅ | SqlValidator.java |
| 添加字段名校验 | ✅ | SqlValidator.java |
| 添加参数校验（文件ID、表名、Excel数据） | ✅ | GenerateSqlServiceImpl.java |
| 改进异常处理，返回用户友好提示 | ✅ | GenerateSqlServiceImpl.java, MyExceptionHandler.java |

**实际产出**:
- ✅ `SqlValidator.java` - SQL校验工具（60+关键字、20+注入模式）
- ✅ `SqlGenerationException.java` - 自定义异常（4种错误类型）
- ✅ `GenerateSqlServiceImpl.java` - 添加校验逻辑和异常处理
- ✅ `MyExceptionHandler.java` - 全局异常处理
- ✅ `stage1-summary.md` - 阶段1修复思路文档（已用humanizer-zh优化）

---

### 阶段 2: 短期优化 - 代码质量 [P1]

**状态**: `✅ 完成`

**目标**: 消除代码重复，改进命名和结构

| 任务 | 状态 | 负责文件 |
|------|------|----------|
| 重命名 `extracted` 方法为 `buildDataTypeDefinition` | ✅ | 策略实现类 |
| 抽取公共的Excel读取逻辑 | ✅ | GenerateSqlServiceImpl.java |
| 抽取主键收集逻辑 | ✅ | 策略实现类 |
| 创建数据库策略接口，消除 if-else | ✅ | DatabaseSqlStrategy.java |
| 引入枚举类替代硬编码字符串 | ✅ | DatabaseType.java |
| 重构字符串拼接逻辑，使用更安全的方式 | ✅ | 策略实现类 |

**实际产出**:
- ✅ `DatabaseType.java` - 数据库类型枚举
- ✅ `DatabaseSqlStrategy.java` - 策略接口
- ✅ `MysqlSqlStrategy.java` - MySQL实现
- ✅ `OracleSqlStrategy.java` - Oracle实现
- ✅ `GenerateSqlServiceImpl.java` - 重构使用策略模式
- ✅ `SqlValidator.java` - 使用DatabaseType枚举
- ✅ `stage2-summary.md` - 阶段2重构思路文档（已用humanizer-zh优化）

---

### 阶段 3: 中期增强 - 数据类型映射 [P2]

**状态**: `pending`

**目标**: 完善数据类型映射，支持更多类型

| 任务 | 状态 | 描述 |
|------|------|------|
| 创建数据类型映射器 | pending | 统一管理各数据库类型映射 |
| 支持DECIMAL精度 | pending | 处理数字类型精度和小数位 |
| 支持CLOB/BLOB | pending | 大字段类型支持 |
| 支持VARCHAR/NVARCHAR区别 | pending | Unicode字段支持 |
| MySQL字符集设置 | pending | 添加CHARSET和COLLATE |

**预期产出**:
- `DataTypeMapper.java` - 类型映射接口
- `MysqlDataTypeMapper.java`
- `OracleDataTypeMapper.java`

---

### 阶段 4: 功能增强 - 索引和约束 [P2]

**状态**: `✅ 完成`

**目标**: 支持更多数据库特性

| 任务 | 状态 | 描述 |
|------|------|------|
| 扩展ImportExcel模型支持索引 | ✅ | 添加索引字段 |
| 实现普通索引生成 | ✅ | INDEX |
| 实现唯一索引生成 | ✅ | UNIQUE INDEX |
| 实现联合索引生成 | ✅ | 多列索引 |
| 支持外键约束 | ✅ | FOREIGN KEY |
| 改进Oracle自增序列支持 | ✅ | SEQUENCE + TRIGGER |

**实际产出**:
- ✅ `IndexDefinition.java` - 索引定义模型
- ✅ 更新 `ImportExcel.java` 添加索引字段
- ✅ 策略接口添加索引/外键/序列方法
- ✅ MySQL和Oracle索引生成实现
- ✅ Excel模板更新（含索引示例）
- ✅ `stage4-summary.md` - 阶段4文档（已用humanizer-zh优化）

---

### 阶段 5: 长期规划 - 扩展数据库支持 [P3]

**状态**: `✅ 完成`

**目标**: 支持更多数据库类型

| 任务 | 状态 | 描述 |
|------|------|------|
| PostgreSQL支持 | ✅ | 添加PostgreSQL策略 |
| SQL Server支持 | ✅ | 添加SQL Server策略 |
| 数据库方言配置化 | ✅ | 通过策略模式实现 |

**实际产出**:
- ✅ `DatabaseType.java` - 添加POSTGRESQL和SQLSERVER枚举
- ✅ `PostgreSqlDataTypeMapper.java` - PostgreSQL类型映射
- ✅ `SqlServerDataTypeMapper.java` - SQL Server类型映射
- ✅ `PostgreSqlStrategy.java` - PostgreSQL策略实现
- ✅ `SqlServerStrategy.java` - SQL Server策略实现
- ✅ `GenerateSqlServiceImpl.java` - 注册新策略
- ✅ `SqlValidatorTest.java` - 更新测试用例
- ✅ `stage5-summary.md` - 阶段5文档

**测试结果**: 50个测试全部通过

---

### 阶段 6: 测试和文档 [P1]

**状态**: `pending`

**目标**: 确保代码质量和可维护性

| 任务 | 状态 | 描述 |
|------|------|------|
| 编写单元测试 | pending | GenerateSqlServiceImplTest |
| 集成测试 | pending | 端到端测试 |
| 更新API文档 | pending | Swagger注解完善 |
| Excel模板文档 | pending | 提供标准Excel模板 |

---

## 遇到的错误

| 错误 | 尝试 | 解决方案 |
|------|------|----------|
| (暂无) | - | - |

---

## 决策记录

| 日期 | 决策 | 原因 |
|------|------|------|
| 2026-02-04 | 采用策略模式重构数据库生成逻辑 | 消除代码重复，便于扩展新数据库 |
| 2026-02-04 | 优先处理安全问题 | SQL注入风险最高，需立即修复 |
| 2026-02-05 | 扩展PostgreSQL和SQL Server支持 | 满足更多数据库用户需求 |

---

## 文件清单

### 新增文件
- [x] `SqlValidator.java` - SQL校验工具
- [x] `SqlGenerationException.java` - 自定义异常
- [x] `DatabaseType.java` - 数据库类型枚举
- [x] `DatabaseSqlStrategy.java` - 策略接口
- [x] `MysqlSqlStrategy.java` - MySQL策略实现
- [x] `OracleSqlStrategy.java` - Oracle策略实现
- [x] `DataTypeMapper.java` - 类型映射接口
- [x] `MysqlDataTypeMapper.java`
- [x] `OracleDataTypeMapper.java`
- [x] `PostgreSqlDataTypeMapper.java`
- [x] `SqlServerDataTypeMapper.java`
- [x] `IndexDefinition.java` - 索引定义模型
- [x] `PostgreSqlStrategy.java` - PostgreSQL策略实现
- [x] `SqlServerStrategy.java` - SQL Server策略实现

### 修改文件
- [x] `GenerateSqlServiceImpl.java` - 重构使用策略模式
- [x] `MyExceptionHandler.java` - 全局异常处理
- [x] `SqlValidator.java` - 使用DatabaseType枚举
- [ ] `SqlGenerateController.java` - 添加异常处理
- [ ] `ImportExcel.java` - 扩展模型
- [ ] `CreateFrom.java` - 添加校验注解

### 文档文件
所有文档存放在 `docs/` 目录下：
- [x] `docs/task_plan.md` - 任务计划
- [x] `docs/findings.md` - 研究发现
- [x] `docs/progress.md` - 进度记录
- [x] `docs/stage1-summary.md` - 阶段1修复思路（已用humanizer-zh优化）
- [x] `docs/stage2-summary.md` - 阶段2重构思路（已用humanizer-zh优化）
- [x] `docs/README.md` - 文档索引

---

## 备注

- 当前Excel列定义：字段名、类型、长度、非空、主键、默认值、备注
- 需要设计新的Excel格式以支持索引和外键
- 考虑向后兼容性
