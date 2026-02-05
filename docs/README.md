# SQL生成功能 - 文档索引

本目录记录项目的优化和重新设计过程。

## 文档结构

```
docs/
├── README.md              # 本文件 - 文档索引
├── API.md                 # API接口文档
├── DEPLOYMENT.md          # 部署运维指南
├── task_plan.md           # 优化计划和阶段进度
├── findings.md            # 代码分析和问题发现
├── progress.md            # 工作日志和进度追踪
├── stage1-summary.md      # 阶段1：安全问题修复
├── stage2-summary.md      # 阶段2：代码质量重构
├── stage3-summary.md      # 阶段3：数据类型映射
├── stage4-summary.md      # 阶段4：索引和约束支持
├── stage5-summary.md      # 阶段5：扩展数据库支持
└── redesign-summary.md    # 重新设计：移除MongoDB和AI
```

## 快速导航

| 文档 | 用途 |
|------|------|
| [PROJECT.md](../PROJECT.md) | **项目总览文档** |
| [API.md](API.md) | **API接口文档** |
| [DEPLOYMENT.md](DEPLOYMENT.md) | **部署运维指南** |
| [redesign-summary.md](redesign-summary.md) | 重新设计总览 |
| [stage5-summary.md](stage5-summary.md) | 扩展数据库支持 |
| [stage4-summary.md](stage4-summary.md) | 索引和约束支持 |
| [stage3-summary.md](stage3-summary.md) | 数据类型映射 |
| [stage2-summary.md](stage2-summary.md) | 策略模式重构 |
| [stage1-summary.md](stage1-summary.md) | 安全问题修复 |
| [task_plan.md](task_plan.md) | 优化计划和任务清单 |
| [progress.md](progress.md) | 工作进度追踪 |

## 新API

| 接口 | 方法 | 说明 |
|------|------|------|
| `/sql/download/template` | GET | 下载Excel模板 |
| `/sql/generate` | POST | 生成SQL（通用） |
| `/sql/generate/mysql` | POST | 生成MySQL SQL |
| `/sql/generate/oracle` | POST | 生成Oracle SQL |
| `/sql/generate/postgresql` | POST | 生成PostgreSQL SQL |
| `/sql/generate/sqlserver` | POST | 生成SQL Server SQL |

## 阶段完成情况

| 阶段 | 名称 | 状态 |
|------|------|------|
| 1 | 安全问题修复 | ✅ 完成 |
| 2 | 代码质量重构 | ✅ 完成 |
| 3 | 数据类型映射 | ✅ 完成 |
| 4 | 索引和约束支持 | ✅ 完成 |
| 5 | 扩展数据库支持 | ✅ 完成 |
| - | 架构重新设计 | ✅ 完成 |

## 核心功能

- SQL注入检测
- 策略模式（MySQL/Oracle/PostgreSQL/SQL Server）
- 数据类型映射（30+类型）
- DECIMAL精度支持
- CLOB/BLOB支持
- MySQL字符集（utf8mb4）
- Excel模板下载
- 索引生成（普通/唯一/全文/联合）
- 外键约束
- 自增序列支持（Oracle SEQUENCE + TRIGGER，PostgreSQL SEQUENCE，SQL Server IDENTITY）
