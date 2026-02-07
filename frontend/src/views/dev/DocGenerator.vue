<template>
  <div class="tool-page">
    <div class="page-header">
      <h2 class="page-title">接口文档生成</h2>
      <p class="page-desc">根据代码注释生成 API 接口文档</p>
    </div>
    <el-alert type="info" title="功能开发中" :closable="false" show-icon />
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped lang="scss">
.tool-page { max-width: 1000px; margin: 0 auto; }
.page-header { margin-bottom: var(--spacing-lg); }
.page-title { font-size: 24px; font-weight: 600; margin-bottom: var(--spacing-sm); color: var(--text-primary); }
.page-desc { font-size: 14px; color: var(--text-secondary); }
</style>
