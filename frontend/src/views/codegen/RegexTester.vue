<template>
  <div class="tool-page">
    <div class="page-header">
      <h2 class="page-title">正则表达式测试</h2>
      <p class="page-desc">实时测试正则表达式，支持多种匹配模式和替换</p>
    </div>
    <el-alert type="info" title="功能开发中" :closable="false" show-icon />
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.tool-page {
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
</style>
