<template>
  <div class="home-page">
    <el-container style="height: 100vh;">
      <el-header>网站标题</el-header>
      <el-main>
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="content-block">
              <h2>欢迎来到我们的网站！</h2>
              <p>这是一个简单的网站首页，使用了Vue 3和Element Plus构建。</p>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="content-block">
              <h2>功能介绍</h2>
              <ul>
                <li>功能一：提供简洁的界面设计。</li>
                <li>功能二：支持响应式布局。</li>
                <li>功能三：易于扩展和维护。</li>
              </ul>
            </div>
          </el-col>
        </el-row>
      </el-main>
      <el-footer>版权所有 © 2023</el-footer>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ElContainer, ElHeader, ElMain, ElFooter, ElRow, ElCol } from 'element-plus';

// 你可以在这里添加其他Vue 3和Element Plus的组件和逻辑
</script>

<style scoped>
.home-page {
  font-family: 'Arial', sans-serif;
  text-align: center;
}

.content-block {
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 4px;
  margin-bottom: 20px;
}

h2 {
  color: #333;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  margin-bottom: 10px;
}
</style>