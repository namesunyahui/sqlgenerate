<template>
  <div class="sql-generator-page">
    <div class="page-header">
      <h2 class="page-title">SQL 生成器</h2>
      <p class="page-desc">根据 Excel 表格定义生成建表 SQL，支持 MySQL、Oracle、PostgreSQL、SQL Server</p>
    </div>

    <div class="generator-card">
      <el-alert
        type="info"
        title="功能开发中"
        description="Vue 版本的 SQL 生成器正在开发中，请暂时使用 <a href='/sql-generator.html' style='color: var(--accent-blue)'>原版界面</a>"
        :closable="false"
        show-icon
      />
    </div>
  </div>
</template>

<script setup lang="ts">
// SQL 生成器页面 - 占位组件
// 完整功能将在后续开发
</script>

<style scoped lang="scss">
.sql-generator-page {
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: var(--spacing-lg);

  .page-title {
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

.generator-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--spacing-xl);
}
</style>
