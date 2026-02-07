<template>
  <div class="sql-generator-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">
        <el-icon><Tickets /></el-icon>
        SQL 生成器
      </h2>
      <p class="page-desc">根据 Excel 表格定义生成建表 SQL，支持多种数据库</p>
    </div>

    <el-row :gutter="24">
      <!-- 左侧：输入区域 -->
      <el-col :span="12">
        <el-card shadow="never" class="config-card">
          <template #header>
            <div class="card-header">
              <span>配置参数</span>
              <el-button type="primary" link @click="downloadTemplate">
                <el-icon><Download /></el-icon>
                下载模板
              </el-button>
            </div>
          </template>

          <el-form :model="form" label-width="100px" label-position="left">
            <!-- 数据库选择 -->
            <el-form-item label="数据库类型">
              <el-select v-model="form.database" placeholder="请选择数据库" style="width: 100%">
                <el-option label="MySQL" value="mysql">
                  <div class="db-option">
                    <el-icon><Coin /></el-icon>
                    <span>MySQL</span>
                  </div>
                </el-option>
                <el-option label="Oracle" value="oracle">
                  <div class="db-option">
                    <el-icon><Files /></el-icon>
                    <span>Oracle</span>
                  </div>
                </el-option>
                <el-option label="PostgreSQL" value="postgresql">
                  <div class="db-option">
                    <el-icon><Coin /></el-icon>
                    <span>PostgreSQL</span>
                  </div>
                </el-option>
                <el-option label="SQL Server" value="sqlserver">
                  <div class="db-option">
                    <el-icon><Coin /></el-icon>
                    <span>SQL Server</span>
                  </div>
                </el-option>
              </el-select>
            </el-form-item>

            <!-- 表名 -->
            <el-form-item label="表名">
              <el-input
                v-model="form.tableName"
                placeholder="请输入表名，如：t_user"
                clearable
              >
                <template #prefix>
                  <el-icon><Notebook /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <!-- 表注释 -->
            <el-form-item label="表注释">
              <el-input
                v-model="form.tableRemark"
                type="textarea"
                :rows="2"
                placeholder="请输入表注释，如：用户表"
                clearable
              />
            </el-form-item>

            <!-- 文件上传 -->
            <el-form-item label="Excel 文件">
              <div
                class="upload-area"
                :class="{ 'is-dragover': isDragover, 'has-file': uploadedFile }"
                @drop.prevent="handleDrop"
                @dragover.prevent="isDragover = true"
                @dragleave.prevent="isDragover = false"
                @click="selectFile"
              >
                <input
                  ref="fileInputRef"
                  type="file"
                  accept=".xlsx,.xls"
                  style="display: none"
                  @change="handleFileChange"
                />

                <div v-if="!uploadedFile" class="upload-placeholder">
                  <el-icon class="upload-icon"><UploadFilled /></el-icon>
                  <div class="upload-text">
                    <p>点击或拖拽文件到此处上传</p>
                    <p class="upload-hint">支持 .xlsx、.xls 格式</p>
                  </div>
                </div>

                <div v-else class="file-info">
                  <el-icon class="file-icon"><Document /></el-icon>
                  <div class="file-details">
                    <p class="file-name">{{ uploadedFile.name }}</p>
                    <p class="file-size">{{ formatFileSize(uploadedFile.size) }}</p>
                  </div>
                  <el-button type="danger" link @click.stop="removeFile">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </el-form-item>

            <!-- 生成按钮 -->
            <el-form-item>
              <el-button
                type="primary"
                :loading="loading"
                :disabled="!canGenerate"
                @click="generateSql"
                style="width: 100%"
              >
                <el-icon><MagicStick /></el-icon>
                {{ loading ? '生成中...' : '生成 SQL' }}
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 右侧：结果区域 -->
      <el-col :span="12">
        <el-card shadow="never" class="result-card">
          <template #header>
            <div class="card-header">
              <span>生成结果</span>
              <div class="header-actions" v-if="generatedSql">
                <el-button type="primary" link @click="copySql">
                  <el-icon><CopyDocument /></el-icon>
                  复制
                </el-button>
                <el-button type="primary" link @click="downloadSql">
                  <el-icon><Download /></el-icon>
                  下载
                </el-button>
              </div>
            </div>
          </template>

          <div class="sql-result" :class="{ 'is-empty': !generatedSql }">
            <div v-if="!generatedSql" class="empty-state">
              <el-icon class="empty-icon"><DocumentCopy /></el-icon>
              <p>配置参数并上传文件后生成 SQL</p>
            </div>

            <pre
              v-else
              class="sql-content"
              v-html="highlightSql(generatedSql)"
            ></pre>
          </div>
        </el-card>

        <!-- 使用说明 -->
        <el-card shadow="never" class="help-card">
          <template #header>
            <div class="card-header">
              <el-icon><QuestionFilled /></el-icon>
              <span>使用说明</span>
            </div>
          </template>
          <div class="help-content">
            <h4>Excel 模板格式：</h4>
            <ul>
              <li>字段名 | 类型 | 长度 | 非空 | 主键 | 默认值 | 备注</li>
            </ul>
            <h4>支持的数据类型：</h4>
            <ul>
              <li>整数：TINYINT, SMALLINT, INT, BIGINT</li>
              <li>浮点：FLOAT, DOUBLE, DECIMAL</li>
              <li>字符串：CHAR, VARCHAR, TEXT</li>
              <li>日期：DATE, DATETIME, TIMESTAMP</li>
              <li>其他：BOOLEAN, JSON, BLOB</li>
            </ul>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Tickets, Download, Coin, Files, Notebook, UploadFilled,
  Document, Delete, MagicStick, CopyDocument, DocumentCopy,
  QuestionFilled
} from '@element-plus/icons-vue'
import { sqlApi } from '@/api/sql'

// 表单数据
const form = ref({
  database: 'mysql',
  tableName: '',
  tableRemark: ''
})

// 上传状态
const uploadedFile = ref<File | null>(null)
const fileInputRef = ref<HTMLInputElement>()
const isDragover = ref(false)

// 生成状态
const loading = ref(false)
const generatedSql = ref('')

// 是否可以生成
const canGenerate = computed(() => {
  return form.value.tableName && uploadedFile.value
})

// 选择文件
const selectFile = () => {
  fileInputRef.value?.click()
}

// 处理文件选择
const handleFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    uploadedFile.value = file
  }
}

// 处理拖拽上传
const handleDrop = (e: DragEvent) => {
  isDragover.value = false
  const file = e.dataTransfer?.files[0]
  if (file && (file.name.endsWith('.xlsx') || file.name.endsWith('.xls'))) {
    uploadedFile.value = file
  } else {
    ElMessage.error('请上传 .xlsx 或 .xls 格式的文件')
  }
}

// 移除文件
const removeFile = () => {
  uploadedFile.value = null
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

// 生成 SQL
const generateSql = async () => {
  if (!uploadedFile.value) {
    ElMessage.warning('请先上传 Excel 文件')
    return
  }

  loading.value = true
  try {
    const formData = new FormData()
    formData.append('file', uploadedFile.value)
    formData.append('tableName', form.value.tableName)
    formData.append('tableRemark', form.value.tableRemark)
    formData.append('database', form.value.database)

    const response = await sqlApi.generateSql(formData)
    generatedSql.value = response.data

    ElMessage.success('SQL 生成成功！')
  } catch (error) {
    ElMessage.error('SQL 生成失败，请检查文件格式')
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 复制 SQL
const copySql = async () => {
  try {
    await navigator.clipboard.writeText(generatedSql.value)
    ElMessage.success('已复制到剪贴板')
  } catch {
    ElMessage.error('复制失败')
  }
}

// 下载 SQL
const downloadSql = () => {
  const blob = new Blob([generatedSql.value], { type: 'text/plain' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `${form.value.tableName}.sql`
  link.click()
  URL.revokeObjectURL(url)
  ElMessage.success('下载成功')
}

// 下载模板
const downloadTemplate = () => {
  window.location.href = sqlApi.downloadTemplate()
}

// SQL 语法高亮
const highlightSql = (sql: string): string => {
  if (!sql) return ''

  // 简单的语法高亮
  let highlighted = sql
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')

  // 关键字高亮
  const keywords = [
    'CREATE', 'TABLE', 'DROP', 'IF', 'EXISTS', 'PRIMARY', 'KEY',
    'NOT', 'NULL', 'DEFAULT', 'COMMENT', 'ON', 'COLUMN', 'ALTER',
    'ADD', 'CONSTRAINT', 'FOREIGN', 'REFERENCES', 'UNIQUE', 'INDEX',
    'INT', 'BIGINT', 'VARCHAR', 'TEXT', 'DATETIME', 'TIMESTAMP',
    'DECIMAL', 'BOOLEAN', 'AUTO_INCREMENT', 'CHARSET', 'COLLATE'
  ]

  keywords.forEach(keyword => {
    const regex = new RegExp(`\\b(${keyword})\\b`, 'gi')
    highlighted = highlighted.replace(regex, '<span class="sql-keyword">$1</span>')
  })

  // 字符串高亮
  highlighted = highlighted.replace(/'([^']*)'/g, '<span class="sql-string">\'$1\'</span>')

  // 注释高亮
  highlighted = highlighted.replace(/--(.*)$/gm, '<span class="sql-comment">--$1</span>')

  return highlighted
}
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.sql-generator-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: var(--spacing-lg);

  .page-title {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    font-size: 24px;
    font-weight: 600;
    margin-bottom: var(--spacing-sm);
    color: var(--text-primary);
  }

  .page-desc {
    font-size: 14px;
    color: var(--text-secondary);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.db-option {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.upload-area {
  border: 2px dashed var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--spacing-xl);
  text-align: center;
  cursor: pointer;
  transition: all var(--transition-fast);

  &:hover,
  &.is-dragover {
    border-color: var(--accent-blue);
    background: rgba(88, 166, 255, 0.05);
  }

  &.has-file {
    border-style: solid;
    border-color: var(--accent-green);
  }
}

.upload-placeholder {
  .upload-icon {
    font-size: 48px;
    color: var(--text-muted);
    margin-bottom: var(--spacing-md);
  }

  .upload-text {
    p {
      margin: var(--spacing-xs) 0;
      color: var(--text-primary);
    }

    .upload-hint {
      font-size: 12px;
      color: var(--text-muted);
    }
  }
}

.file-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);

  .file-icon {
    font-size: 32px;
    color: var(--accent-green);
  }

  .file-details {
    flex: 1;
    text-align: left;

    .file-name {
      font-weight: 500;
      color: var(--text-primary);
      margin-bottom: var(--spacing-xs);
    }

    .file-size {
      font-size: 12px;
      color: var(--text-muted);
    }
  }
}

.sql-result {
  min-height: 400px;
  max-height: 600px;
  overflow: auto;
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
  padding: var(--spacing-md);

  &.is-empty {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .empty-state {
    text-align: center;
    color: var(--text-muted);

    .empty-icon {
      font-size: 64px;
      margin-bottom: var(--spacing-md);
    }
  }

  .sql-content {
    margin: 0;
    font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, Courier, monospace;
    font-size: 13px;
    line-height: 1.6;
    color: var(--text-primary);

    :deep(.sql-keyword) {
      color: var(--syntax-keyword);
      font-weight: 600;
    }

    :deep(.sql-string) {
      color: var(--syntax-string);
    }

    :deep(.sql-comment) {
      color: var(--syntax-comment);
      font-style: italic;
    }
  }
}

.help-card {
  margin-top: var(--spacing-lg);

  .help-content {
    h4 {
      font-size: 14px;
      font-weight: 600;
      margin: var(--spacing-md) 0 var(--spacing-sm);
      color: var(--text-primary);
    }

    ul {
      margin: 0;
      padding-left: var(--spacing-lg);
      color: var(--text-secondary);
      font-size: 13px;
      line-height: 1.8;

      li {
        margin-bottom: var(--spacing-xs);
      }
    }
  }
}

// 滚动条样式
.sql-result::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.sql-result::-webkit-scrollbar-track {
  background: var(--bg-secondary);
}

.sql-result::-webkit-scrollbar-thumb {
  background: var(--bg-elevated);
  border-radius: var(--radius-sm);

  &:hover {
    background: var(--border-default);
  }
}
</style>
