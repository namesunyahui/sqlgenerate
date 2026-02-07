<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<template>
  <div class="app-container">
    <!-- 顶部导航 -->
    <header class="app-header">
      <div class="header-left">
        <h1 class="app-title">
          <span class="title-icon">⚡</span>
          <span class="title-text">CodeForge</span>
        </h1>
      </div>
      <div class="header-right">
        <a href="/swagger-ui.html" target="_blank" class="header-link">
          <el-icon><Document /></el-icon>
          API 文档
        </a>
        <a href="https://github.com/namesunyahui/sqlgenerate" target="_blank" class="header-link">
          <el-icon><Link /></el-icon>
          GitHub
        </a>
      </div>
    </header>

    <!-- 主体区域 -->
    <div class="app-main">
      <!-- 侧边栏 -->
      <aside class="app-sidebar">
        <nav class="sidebar-nav">
          <div class="nav-section">
            <h3 class="nav-section-title">SQL 工具</h3>
            <router-link to="/sql/generator" class="nav-item">
              <el-icon><Tickets /></el-icon>
              <span>SQL 生成器</span>
            </router-link>
          </div>

          <div class="nav-section">
            <h3 class="nav-section-title">代码生成</h3>
            <router-link to="/codegen/json-to-bean" class="nav-item">
              <el-icon><Coin /></el-icon>
              <span>JSON 转 Java Bean</span>
            </router-link>
            <router-link to="/codegen/regex-tester" class="nav-item">
              <el-icon><Edit /></el-icon>
              <span>正则表达式测试</span>
            </router-link>
            <router-link to="/codegen/crud-generator" class="nav-item">
              <el-icon><DocumentCopy /></el-icon>
              <span>CRUD 代码生成</span>
            </router-link>
          </div>

          <div class="nav-section">
            <h3 class="nav-section-title">数据处理</h3>
            <router-link to="/data/excel-converter" class="nav-item">
              <el-icon><Switch /></el-icon>
              <span>Excel 格式转换</span>
            </router-link>
            <router-link to="/data/formatter" class="nav-item">
              <el-icon><MagicStick /></el-icon>
              <span>数据格式化</span>
            </router-link>
            <router-link to="/data/crypto" class="nav-item">
              <el-icon><Lock /></el-icon>
              <span>加密解密</span>
            </router-link>
          </div>

          <div class="nav-section">
            <h3 class="nav-section-title">开发辅助</h3>
            <router-link to="/dev/api-tester" class="nav-item">
              <el-icon><Connection /></el-icon>
              <span>API 测试</span>
            </router-link>
            <router-link to="/dev/doc-generator" class="nav-item">
              <el-icon><Reading /></el-icon>
              <span>接口文档生成</span>
            </router-link>
            <router-link to="/dev/mock-generator" class="nav-item">
              <el-icon><Box /></el-icon>
              <span>Mock 数据生成</span>
            </router-link>
          </div>
        </nav>
      </aside>

      <!-- 内容区域 -->
      <main class="app-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<style scoped lang="scss">
.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--bg-primary);
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 var(--spacing-lg);
  height: 60px;
  background: var(--bg-secondary);
  border-bottom: 1px solid var(--border-default);
}

.header-left {
  .app-title {
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    margin: 0;
    font-size: 20px;
    font-weight: 600;
    color: var(--text-primary);

    .title-icon {
      font-size: 24px;
    }

    .title-text {
      background: linear-gradient(135deg, var(--accent-blue), var(--accent-purple));
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }
}

.header-right {
  display: flex;
  gap: var(--spacing-md);

  .header-link {
    display: flex;
    align-items: center;
    gap: var(--spacing-xs);
    padding: var(--spacing-sm) var(--spacing-md);
    color: var(--text-secondary);
    border-radius: var(--radius-md);
    transition: all var(--transition-fast);

    &:hover {
      color: var(--accent-blue);
      background: var(--bg-tertiary);
    }
  }
}

.app-main {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.app-sidebar {
  width: 260px;
  background: var(--bg-secondary);
  border-right: 1px solid var(--border-default);
  overflow-y: auto;
}

.sidebar-nav {
  padding: var(--spacing-md);

  .nav-section {
    margin-bottom: var(--spacing-lg);

    &-title {
      margin: 0 0 var(--spacing-sm) var(--spacing-sm);
      font-size: 12px;
      font-weight: 600;
      color: var(--text-muted);
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .nav-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);
      padding: var(--spacing-sm) var(--spacing-md);
      margin-bottom: var(--spacing-xs);
      color: var(--text-secondary);
      border-radius: var(--radius-md);
      transition: all var(--transition-fast);

      &:hover {
        color: var(--text-primary);
        background: var(--bg-tertiary);
      }

      &.router-link-active {
        color: var(--accent-blue);
        background: rgba(88, 166, 255, 0.1);
      }
    }
  }
}

.app-content {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-lg);
}
</style>
