<template>
  <div class="tool-page">
    <div class="page-header">
      <h2 class="page-title">JSON 转 Java Bean</h2>
      <p class="page-desc">将 JSON 数据转换为 Java 实体类，支持 Lombok 注解</p>
    </div>

    <el-alert type="info" title="功能开发中" :closable="false" show-icon />
  </div>
</template>

<script setup lang="ts">
// JSON 转 Java Bean 页面 - 开发中
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
