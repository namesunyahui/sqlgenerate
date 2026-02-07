<template>
  <div class="sql-generator">
    <!-- Background Effects -->
    <div class="bg-grid"></div>
    <div class="bg-scanlines"></div>

    <div class="container">
      <!-- Header -->
      <header class="header">
        <div class="logo">
          <div class="logo-icon">⚡</div>
          <div class="logo-text">SQL Generator</div>
          <span class="logo-badge">v2.0</span>
        </div>
        <div class="header-actions">
          <button class="btn-icon" @click="downloadTemplate" title="下载模板">
            📥
          </button>
        </div>
      </header>

      <!-- Main Content -->
      <main class="main-content">
        <!-- Left Panel - Controls -->
        <div class="control-panel">
          <!-- Step 1: Upload -->
          <div class="panel-section">
            <span class="section-label">Step 1: Upload Excel</span>
            <div
              class="upload-area"
              :class="{ 'drag-over': isDragover, 'has-file': uploadedFile }"
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
                <div class="upload-icon">📁</div>
                <div class="upload-text">
                  <strong>Click to upload</strong> or drag & drop<br>
                  <small>.xlsx file with table structure</small>
                </div>
              </div>

              <div v-else class="file-info">
                <span class="file-icon">📄</span>
                <span class="file-name">{{ uploadedFile.name }}</span>
                <span class="file-remove" @click.stop="removeFile">×</span>
              </div>
            </div>
          </div>

          <!-- Step 2: Database Selection -->
          <div class="panel-section">
            <span class="section-label">Step 2: Select Database</span>
            <div class="db-selector">
              <div
                v-for="db in databases"
                :key="db.value"
                class="db-option"
                :class="{ selected: form.database === db.value }"
                @click="form.database = db.value"
              >
                <span class="db-icon">{{ db.icon }}</span>
                <div class="db-info">
                  <span class="db-name">{{ db.name }}</span>
                  <span class="db-ver">{{ db.version }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Step 3: Table Info -->
          <div class="panel-section">
            <span class="section-label">Step 3: Table Information</span>
            <div class="input-group">
              <label class="input-label">Table Name</label>
              <input
                v-model="form.tableName"
                class="input-field"
                placeholder="e.g., t_user"
              >
            </div>
            <div class="input-group">
              <label class="input-label">Table Remark</label>
              <input
                v-model="form.tableRemark"
                class="input-field"
                placeholder="e.g., User table"
              >
            </div>
          </div>

          <!-- Step 4: Generate -->
          <div class="panel-section">
            <span class="section-label">Step 4: Generate</span>
            <div class="btn-group">
              <button
                class="btn btn-primary btn-full"
                :disabled="!canGenerate || loading"
                @click="generateSql"
              >
                <span v-if="!loading">⚡ Generate SQL</span>
                <span v-else>⚡ Generating...</span>
              </button>
              <button class="btn btn-secondary" @click="reset">
                🔄 Reset
              </button>
            </div>
          </div>
        </div>

        <!-- Right Panel - Output -->
        <div class="output-panel">
          <div class="output-header">
            <div class="output-title">
              <span class="output-label">Generated SQL</span>
            </div>
            <div class="output-stats" v-if="generatedSql">
              <span class="stat-item">
                <span>Lines:</span>
                <span class="stat-value">{{ lineCount }}</span>
              </span>
              <span class="stat-item">
                <span>Database:</span>
                <span class="stat-value">{{ form.database.toUpperCase() }}</span>
              </span>
            </div>
            <div class="output-actions" v-if="generatedSql">
              <button class="btn-icon" @click="copySql" title="Copy">
                📋
              </button>
              <button class="btn-icon" @click="downloadSql" title="Download">
                💾
              </button>
            </div>
          </div>

          <div class="code-editor">
            <!-- Empty State -->
            <div v-if="!generatedSql && !loading" class="empty-state">
              <div class="empty-icon">📝</div>
              <div class="empty-text">Upload an Excel file and click Generate</div>
            </div>

            <!-- Loading State -->
            <div v-if="loading" class="loading-state">
              <div class="loader"></div>
              <div class="loading-text">Generating SQL...</div>
            </div>

            <!-- Code Display -->
            <div v-if="generatedSql" class="code-wrapper">
              <div class="line-numbers">
                <span v-for="n in lineCount" :key="n">{{ n }}</span>
              </div>
              <div class="code-content" v-html="highlightedSql"></div>
            </div>
          </div>
        </div>
      </main>
    </div>

    <!-- Toast -->
    <Transition name="toast">
      <div v-if="toast.show" :class="['toast', toast.type]">
        <span class="toast-icon">{{ toast.type === 'success' ? '✓' : '✕' }}</span>
        <span class="toast-message">{{ toast.message }}</span>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { sqlApi } from '@/api/sql'

// Form
const form = ref({
  database: 'mysql',
  tableName: '',
  tableRemark: ''
})

// Database options
const databases = [
  { value: 'mysql', name: 'MySQL', icon: '🐬', version: '8.0+' },
  { value: 'oracle', name: 'Oracle', icon: '🔴', version: '11g+' },
  { value: 'postgresql', name: 'PostgreSQL', icon: '🐘', version: '12+' },
  { value: 'sqlserver', name: 'SQL Server', icon: '🔷', version: '2017+' }
]

// File upload
const uploadedFile = ref<File | null>(null)
const fileInputRef = ref<HTMLInputElement>()
const isDragover = ref(false)

// Generation
const loading = ref(false)
const generatedSql = ref('')

// Toast
const toast = ref({ show: false, message: '', type: 'success' })
let toastTimer: ReturnType<typeof setTimeout> | null = null

// Computed
const canGenerate = computed(() => {
  return form.value.tableName && uploadedFile.value
})

const lineCount = computed(() => {
  return generatedSql.value ? generatedSql.value.split('\n').length : 0
})

const highlightedSql = computed(() => {
  return highlightSql(generatedSql.value)
})

// Methods
const selectFile = () => {
  fileInputRef.value?.click()
}

const handleFileChange = (e: Event) => {
  const target = e.target as HTMLInputElement
  const file = target.files?.[0]
  if (file) {
    if (!file.name.match(/\.(xlsx|xls)$/i)) {
      showToast('Please upload a valid Excel file', 'error')
      return
    }
    uploadedFile.value = file
  }
}

const handleDrop = (e: DragEvent) => {
  isDragover.value = false
  const file = e.dataTransfer?.files[0]
  if (file) {
    if (!file.name.match(/\.(xlsx|xls)$/i)) {
      showToast('Please upload a valid Excel file', 'error')
      return
    }
    uploadedFile.value = file
  }
}

const removeFile = () => {
  uploadedFile.value = null
  if (fileInputRef.value) {
    fileInputRef.value.value = ''
  }
}

const generateSql = async () => {
  if (!uploadedFile.value || !form.value.tableName) {
    showToast('Please complete all required fields', 'error')
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
    showToast('SQL generated successfully!', 'success')
  } catch (error) {
    showToast('Failed to generate SQL', 'error')
  } finally {
    loading.value = false
  }
}

const copySql = async () => {
  try {
    await navigator.clipboard.writeText(generatedSql.value)
    showToast('Copied to clipboard!', 'success')
  } catch {
    showToast('Failed to copy', 'error')
  }
}

const downloadSql = () => {
  const blob = new Blob([generatedSql.value], { type: 'text/plain' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `${form.value.tableName || 'table'}.sql`
  link.click()
  URL.revokeObjectURL(url)
  showToast('SQL file downloaded!', 'success')
}

const downloadTemplate = () => {
  window.location.href = sqlApi.downloadTemplate()
}

const reset = () => {
  removeFile()
  form.value.tableName = ''
  form.value.tableRemark = ''
  generatedSql.value = ''
  form.value.database = 'mysql'
}

const showToast = (message: string, type: 'success' | 'error' = 'success') => {
  toast.value = { show: true, message, type }
  if (toastTimer) clearTimeout(toastTimer)
  toastTimer = setTimeout(() => {
    toast.value.show = false
  }, 3000)
}

const highlightSql = (sql: string): string => {
  if (!sql) return ''

  let highlighted = sql
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')

  // Comments
  highlighted = highlighted.replace(/(--[^\n]*)/g, '<span class="sql-comment">$1</span>')

  // Strings
  highlighted = highlighted.replace(/('([^']*)')/g, '<span class="sql-string">$1</span>')

  // Keywords
  const keywords = ['SELECT', 'FROM', 'WHERE', 'INSERT', 'INTO', 'VALUES', 'UPDATE', 'SET', 'DELETE', 'CREATE', 'TABLE', 'ALTER', 'DROP', 'INDEX', 'PRIMARY', 'KEY', 'FOREIGN', 'REFERENCES', 'UNIQUE', 'CONSTRAINT', 'DEFAULT', 'NOT', 'NULL', 'COMMENT', 'ON', 'COLUMN', 'ADD', 'CASCADE', 'RESTRICT', 'NO', 'ACTION', 'IF', 'EXISTS', 'OR', 'REPLACE', 'TRIGGER', 'BEFORE', 'EACH', 'ROW', 'BEGIN', 'END', 'SEQUENCE', 'START', 'WITH', 'INCREMENT', 'BY', 'NOCACHE', 'NOCYCLE', 'NEXTVAL', 'INTO', 'THEN', 'ELSE', 'DECLARE']

  keywords.forEach(keyword => {
    const regex = new RegExp(`\\b${keyword}\\b`, 'gi')
    highlighted = highlighted.replace(regex, '<span class="sql-keyword">$&</span>')
  })

  // Data types
  const types = ['INT', 'INTEGER', 'BIGINT', 'SMALLINT', 'TINYINT', 'DECIMAL', 'NUMERIC', 'VARCHAR', 'CHAR', 'TEXT', 'DATE', 'DATETIME', 'TIMESTAMP', 'BLOB', 'CLOB', 'BOOLEAN', 'FLOAT', 'DOUBLE', 'JSON', 'BYTEA', 'REAL', 'BIT', 'VARCHAR2', 'NUMBER', 'NCHAR', 'NVARCHAR', 'DATETIME2']

  types.forEach(type => {
    const regex = new RegExp(`\\b${type}\\b`, 'gi')
    highlighted = highlighted.replace(regex, '<span class="sql-type">$&</span>')
  })

  // Functions
  const functions = ['COUNT', 'SUM', 'AVG', 'MIN', 'MAX', 'CONCAT', 'COALESCE', 'NULLIF', 'CAST', 'CONVERT']

  functions.forEach(func => {
    const regex = new RegExp(`\\b${func}\\b`, 'gi')
    highlighted = highlighted.replace(regex, '<span class="sql-function">$&</span>')
  })

  // Numbers
  highlighted = highlighted.replace(/\b(\d+)\b/g, '<span class="sql-number">$1</span>')

  return highlighted
}

// Keyboard shortcuts
const handleKeydown = (e: KeyboardEvent) => {
  if ((e.ctrlKey || e.metaKey) && e.key === 'Enter') {
    generateSql()
  }
  if ((e.ctrlKey || e.metaKey) && e.shiftKey && e.key === 'C') {
    copySql()
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
  if (toastTimer) clearTimeout(toastTimer)
})
</script>

<style scoped lang="scss">
@use '@/styles/variables' as *;

.sql-generator {
  min-height: 100vh;
  position: relative;
  font-family: 'SF Mono', 'Fira Code', 'JetBrains Mono', 'Consolas', monospace;
  background: var(--bg-primary);
  color: var(--text-primary);
  padding: var(--space-lg);
}

/* Background Effects */
.bg-grid {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(rgba(88, 166, 255, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(88, 166, 255, 0.03) 1px, transparent 1px);
  background-size: 40px 40px;
  pointer-events: none;
  z-index: 0;
}

.bg-scanlines {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: repeating-linear-gradient(
    0deg,
    rgba(0, 0, 0, 0.1) 0px,
    transparent 1px,
    transparent 2px,
    rgba(0, 0, 0, 0.1) 3px
  );
  background-size: 100% 4px;
  pointer-events: none;
  opacity: 0.3;
  z-index: 1;
}

.container {
  position: relative;
  z-index: 2;
  max-width: 1600px;
  margin: 0 auto;
  animation: slideDown 0.6s ease;
}

/* Header */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: var(--space-lg);
  border-bottom: 1px solid var(--border-default);
  margin-bottom: var(--space-xl);
}

.logo {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--accent-blue), var(--accent-purple));
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: var(--glow-blue);
}

.logo-text {
  font-size: 24px;
  font-weight: 600;
  background: linear-gradient(90deg, var(--accent-blue), var(--accent-cyan));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.logo-badge {
  font-size: 11px;
  padding: 2px 8px;
  background: var(--accent-green);
  color: var(--bg-primary);
  border-radius: var(--radius-sm);
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: var(--space-md);
}

.btn-icon {
  width: 40px;
  height: 40px;
  border: 1px solid var(--border-default);
  background: var(--bg-secondary);
  color: var(--text-secondary);
  border-radius: var(--radius-md);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
  font-size: 16px;

  &:hover {
    border-color: var(--accent-blue);
    color: var(--accent-blue);
    box-shadow: var(--glow-blue);
  }
}

/* Main Content */
.main-content {
  display: grid;
  grid-template-columns: 420px 1fr;
  gap: var(--space-xl);
  align-items: start;
}

/* Control Panel */
.control-panel {
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
  animation: slideLeft 0.6s ease 0.1s both;
}

.panel-section {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.section-label {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 2px;
  color: var(--text-muted);
  font-weight: 600;
}

/* Upload Area */
.upload-area {
  border: 2px dashed var(--border-default);
  border-radius: var(--radius-md);
  padding: var(--space-xl);
  text-align: center;
  cursor: pointer;
  transition: all var(--transition-normal);
  background: var(--bg-secondary);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(88, 166, 255, 0.1), transparent);
    transition: left 0.5s ease;
  }

  &:hover,
  &.drag-over {
    border-color: var(--accent-blue);
    background: var(--bg-elevated);

    &::before {
      left: 100%;
    }
  }

  &.drag-over {
    border-color: var(--accent-green);
    background: rgba(63, 185, 80, 0.1);
    animation: pulse 1s ease infinite;
  }

  &.has-file {
    border-style: solid;
    border-color: var(--accent-green);
  }
}

.upload-placeholder {
  .upload-icon {
    font-size: 48px;
    margin-bottom: var(--space-md);
    opacity: 0.5;
    transition: all var(--transition-normal);
  }

  .upload-area:hover .upload-icon {
    opacity: 1;
    transform: scale(1.1);
  }

  .upload-text {
    font-size: 14px;
    color: var(--text-secondary);

    strong {
      color: var(--accent-blue);
    }

    small {
      font-size: 11px;
      color: var(--text-muted);
    }
  }
}

.file-info {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-md);
  background: var(--bg-tertiary);
  border-radius: var(--radius-md);
}

.file-icon {
  font-size: 20px;
}

.file-name {
  flex: 1;
  font-size: 13px;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-remove {
  color: var(--accent-red);
  cursor: pointer;
  font-size: 18px;

  &:hover {
    color: #ff6b6b;
  }
}

/* Database Selector */
.db-selector {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-sm);
}

.db-option {
  padding: var(--space-md);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  background: var(--bg-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  transition: all var(--transition-fast);

  &:hover {
    border-color: var(--accent-blue);
    background: var(--bg-elevated);
  }

  &.selected {
    border-color: var(--accent-green);
    background: rgba(63, 185, 80, 0.1);
    box-shadow: var(--glow-green);
  }
}

.db-icon {
  font-size: 24px;
  opacity: 0.8;
}

.db-info {
  display: flex;
  flex-direction: column;
}

.db-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.db-ver {
  font-size: 10px;
  color: var(--text-muted);
}

/* Input Group */
.input-group {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.input-label {
  font-size: 12px;
  color: var(--text-secondary);
}

.input-field {
  background: var(--bg-secondary);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  padding: var(--space-sm) var(--space-md);
  color: var(--text-primary);
  font-family: inherit;
  font-size: 14px;
  transition: all var(--transition-fast);

  &:focus {
    outline: none;
    border-color: var(--accent-blue);
    box-shadow: var(--glow-blue);
  }

  &::placeholder {
    color: var(--text-muted);
  }
}

/* Buttons */
.btn-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-sm);
}

.btn {
  padding: var(--space-md) var(--space-lg);
  border: none;
  border-radius: var(--radius-md);
  font-family: inherit;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-sm);
}

.btn-primary {
  background: linear-gradient(135deg, var(--accent-blue), var(--accent-purple));
  color: white;
  box-shadow: var(--glow-blue);

  &:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 0 30px rgba(88, 166, 255, 0.5);
  }

  &:active:not(:disabled) {
    transform: translateY(0);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
    transform: none !important;
  }
}

.btn-secondary {
  background: var(--bg-tertiary);
  color: var(--text-primary);
  border: 1px solid var(--border-default);

  &:hover {
    border-color: var(--accent-blue);
    color: var(--accent-blue);
  }
}

.btn-full {
  grid-column: 1 / -1;
}

/* Output Panel */
.output-panel {
  background: var(--glass-bg);
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideRight 0.6s ease 0.2s both;
  min-height: 600px;
}

.output-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--space-md) var(--space-lg);
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-default);
  gap: var(--space-md);
}

.output-title {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.output-label {
  font-size: 13px;
  color: var(--text-secondary);
}

.output-stats {
  display: flex;
  gap: var(--space-lg);
  font-size: 12px;
  color: var(--text-muted);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
}

.stat-value {
  color: var(--accent-cyan);
  font-weight: 600;
}

.output-actions {
  display: flex;
  gap: var(--space-sm);
}

/* Code Editor */
.code-editor {
  flex: 1;
  background: var(--bg-primary);
  padding: var(--space-lg);
  overflow: auto;
  position: relative;
}

.code-wrapper {
  display: flex;
  gap: var(--space-md);
}

.line-numbers {
  color: var(--text-muted);
  user-select: none;
  text-align: right;
  min-width: 40px;
  font-size: 13px;
  line-height: 1.8;
}

.code-content {
  flex: 1;
  font-family: 'Fira Code', 'JetBrains Mono', monospace;
  font-size: 13px;
  line-height: 1.8;
  white-space: pre;
}

// Syntax Highlighting
:deep(.sql-keyword) { color: var(--accent-purple); }
:deep(.sql-function) { color: var(--accent-blue); }
:deep(.sql-string) { color: var(--accent-green); }
:deep(.sql-number) { color: var(--accent-orange); }
:deep(.sql-comment) { color: var(--text-muted); font-style: italic; }
:deep(.sql-type) { color: var(--accent-cyan); }

// Empty State
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: var(--text-muted);
  gap: var(--space-md);
}

.empty-icon {
  font-size: 64px;
  opacity: 0.3;
}

.empty-text {
  font-size: 14px;
}

// Loading State
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: var(--space-md);
  padding: var(--space-xl);
}

.loader {
  width: 48px;
  height: 48px;
  border: 3px solid var(--border-default);
  border-top-color: var(--accent-blue);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.loading-text {
  font-size: 14px;
  color: var(--text-secondary);
}

// Toast
.toast {
  position: fixed;
  bottom: var(--space-lg);
  right: var(--space-lg);
  padding: var(--space-md) var(--space-lg);
  background: var(--bg-secondary);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-card);
  display: flex;
  align-items: center;
  gap: var(--space-md);
  min-width: 300px;
  z-index: 100;

  &.success {
    border-color: var(--accent-green);
  }

  &.error {
    border-color: var(--accent-red);
  }
}

.toast-icon {
  font-size: 20px;

  .success & { color: var(--accent-green); }
  .error & { color: var(--accent-red); }
}

.toast-message {
  flex: 1;
  font-size: 13px;
}

.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(100%);
}

// Animations
@keyframes slideDown {
  from { opacity: 0; transform: translateY(-20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes slideLeft {
  from { opacity: 0; transform: translateX(-30px); }
  to { opacity: 1; transform: translateX(0); }
}

@keyframes slideRight {
  from { opacity: 0; transform: translateX(30px); }
  to { opacity: 1; transform: translateX(0); }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.8; }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

// Responsive
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }

  .output-panel {
    min-height: 400px;
  }
}

@media (max-width: 768px) {
  .sql-generator {
    padding: var(--space-md);
  }

  .db-selector {
    grid-template-columns: 1fr;
  }

  .header {
    flex-direction: column;
    gap: var(--space-md);
    align-items: flex-start;
  }
}

// Scrollbar
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: var(--bg-secondary);
}

::-webkit-scrollbar-thumb {
  background: var(--border-default);
  border-radius: var(--radius-sm);

  &:hover {
    background: var(--text-muted);
  }
}
</style>
