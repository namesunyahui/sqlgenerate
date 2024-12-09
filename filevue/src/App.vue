
<template>
  <div class="common-layout" >
    <el-container class="el-container">
      <el-header class="el-header">
        <!-- 添加导航栏 -->
        <Menu
          :default-active="menuItems[0].index"
          class="el-menu-demo"
          mode="horizontal"
          @select="handleMenuSelect"
        >
          <MenuItem
            v-for="(item, index) in menuItems"
            :key="index"
            :index="item.index"
          >
            {{ item.label }}
          </MenuItem>
        </Menu>
      </el-header>
        <el-main>
          <RouterView />
        </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { RouterView ,useRouter } from 'vue-router';
const router = useRouter();
// 引入Element Plus的Menu和MenuItem组件
import { ElMenu, ElMenuItem } from 'element-plus';
// 为Element Plus的组件定义别名，以便在模板中使用
const Menu = ElMenu;
const MenuItem = ElMenuItem;

// 示例菜单项，你可以根据实际情况替换这些值
const menuItems = [
  { index: 'home', label: 'Home' },
  // { index: 'about', label: 'About' },
  { index: 'uploadFile', label: '生成sql' },
  { index: 'chatAI', label: 'AI应答' },
  { index: 'chatAISee', label: 'AI应答流式' },

  // ...其他菜单项
];

// 处理菜单选择事件的方法
function handleMenuSelect(index: string) {
  console.log('Menu item selected:', index);
  // 这里可以添加路由跳转逻辑，例如使用vue-router的push方法
  router.push(index); // 使用 Vue Router 进行导航

}
</script>




<style scoped>
.common-layout {
  display: flex;
  flex-direction: column;
  height: 100vh; /* 使用视口高度单位vh来确保占据整个屏幕高度 */
  width: 80vw;
}



</style>

