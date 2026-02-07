<template>
  <div class="home-page">
    <div class="welcome-section">
      <h1 class="welcome-title">欢迎使用 CodeForge</h1>
      <p class="welcome-desc">多功能开发工具平台，提升开发效率</p>
    </div>

    <div class="tools-grid">
      <div
        v-for="tool in tools"
        :key="tool.path"
        class="tool-card"
        @click="router.push(tool.path)"
      >
        <div class="tool-icon">
          <component :is="tool.icon" />
        </div>
        <h3 class="tool-title">{{ tool.title }}</h3>
        <p class="tool-desc">{{ tool.description }}</p>
      </div>
    </div>

    <div class="info-section">
      <div class="info-card">
        <h3>关于平台</h3>
        <p>CodeForge 是一个面向开发者的多功能工具平台，提供代码生成、数据处理、开发辅助等实用工具，帮助开发者提高工作效率。</p>
      </div>
      <div class="info-card">
        <h3>技术栈</h3>
        <div class="tech-tags">
          <span class="tech-tag">Spring Boot 3.3</span>
          <span class="tech-tag">Vue 3</span>
          <span class="tech-tag">TypeScript</span>
          <span class="tech-tag">Element Plus</span>
          <span class="tech-tag">Vite</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import {
  Tickets, Coin, Edit, DocumentCopy,
  Switch, MagicStick, Lock,
  Connection, Reading, Box
} from '@element-plus/icons-vue'

const router = useRouter()

const tools = [
  {
    path: '/sql/generator',
    title: 'SQL 生成器',
    description: '根据 Excel 表格定义生成建表 SQL，支持多种数据库',
    icon: Tickets
  },
  {
    path: '/codegen/json-to-bean',
    title: 'JSON 转 Java Bean',
    description: '将 JSON 数据转换为 Java 实体类',
    icon: Coin
  },
  {
    path: '/codegen/regex-tester',
    title: '正则表达式测试',
    description: '实时测试正则表达式，支持多种匹配模式',
    icon: Edit
  },
  {
    path: '/codegen/crud-generator',
    title: 'CRUD 代码生成',
    description: '根据表结构生成前后端 CRUD 代码',
    icon: DocumentCopy
  },
  {
    path: '/data/excel-converter',
    title: 'Excel 格式转换',
    description: 'Excel 与 CSV、JSON 之间相互转换',
    icon: Switch
  },
  {
    path: '/data/formatter',
    title: '数据格式化',
    description: 'JSON、XML、SQL 等数据格式化与美化',
    icon: MagicStick
  },
  {
    path: '/data/crypto',
    title: '加密解密',
    description: '支持 MD5、SHA、AES、RSA 等加密算法',
    icon: Lock
  },
  {
    path: '/dev/api-tester',
    title: 'API 测试',
    description: '发送 HTTP 请求，测试 API 接口',
    icon: Connection
  },
  {
    path: '/dev/doc-generator',
    title: '接口文档生成',
    description: '根据代码注释生成 API 接口文档',
    icon: Reading
  },
  {
    path: '/dev/mock-generator',
    title: 'Mock 数据生成',
    description: '生成各种类型的模拟测试数据',
    icon: Box
  }
]
</script>

<style scoped lang="scss">
.home-page {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-section {
  text-align: center;
  padding: var(--spacing-xl) 0;

  .welcome-title {
    font-size: 36px;
    font-weight: 700;
    margin-bottom: var(--spacing-md);
    background: linear-gradient(135deg, var(--accent-blue), var(--accent-purple));
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .welcome-desc {
    font-size: 16px;
    color: var(--text-secondary);
  }
}

.tools-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
}

.tool-card {
  background: var(--bg-secondary);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-lg);
  padding: var(--spacing-lg);
  cursor: pointer;
  transition: all var(--transition-normal);

  &:hover {
    border-color: var(--accent-blue);
    transform: translateY(-4px);
    box-shadow: var(--shadow-md);
  }

  .tool-icon {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: rgba(88, 166, 255, 0.1);
    border-radius: var(--radius-md);
    margin-bottom: var(--spacing-md);
    color: var(--accent-blue);
    font-size: 24px;
  }

  .tool-title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: var(--spacing-sm);
    color: var(--text-primary);
  }

  .tool-desc {
    font-size: 13px;
    color: var(--text-secondary);
    line-height: 1.6;
  }
}

.info-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-lg);

  .info-card {
    background: var(--bg-secondary);
    border: 1px solid var(--border-default);
    border-radius: var(--radius-lg);
    padding: var(--spacing-lg);

    h3 {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: var(--spacing-md);
      color: var(--text-primary);
    }

    p {
      font-size: 14px;
      color: var(--text-secondary);
      line-height: 1.8;
    }
  }
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);

  .tech-tag {
    padding: var(--spacing-xs) var(--spacing-md);
    background: var(--bg-tertiary);
    border: 1px solid var(--border-default);
    border-radius: var(--radius-sm);
    font-size: 12px;
    color: var(--accent-blue);
  }
}

@media (max-width: 768px) {
  .info-section {
    grid-template-columns: 1fr;
  }
}
</style>
