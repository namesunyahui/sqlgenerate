import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home/index.vue'),
    meta: { title: 'CodeForge - 多功能开发工具平台' }
  },
  {
    path: '/sql',
    name: 'SqlTools',
    children: [
      {
        path: 'generator',
        name: 'SqlGenerator',
        component: () => import('@/views/sql/SqlGenerator.vue'),
        meta: { title: 'SQL 生成器', category: 'sql' }
      }
    ]
  },
  {
    path: '/codegen',
    name: 'CodeTools',
    children: [
      {
        path: 'json-to-bean',
        name: 'JsonToBean',
        component: () => import('@/views/codegen/JsonToBean.vue'),
        meta: { title: 'JSON 转 Java Bean', category: 'codegen' }
      },
      {
        path: 'regex-tester',
        name: 'RegexTester',
        component: () => import('@/views/codegen/RegexTester.vue'),
        meta: { title: '正则表达式测试', category: 'codegen' }
      },
      {
        path: 'crud-generator',
        name: 'CrudGenerator',
        component: () => import('@/views/codegen/CrudGenerator.vue'),
        meta: { title: 'CRUD 代码生成', category: 'codegen' }
      }
    ]
  },
  {
    path: '/data',
    name: 'DataTools',
    children: [
      {
        path: 'excel-converter',
        name: 'ExcelConverter',
        component: () => import('@/views/data/ExcelConverter.vue'),
        meta: { title: 'Excel 格式转换', category: 'data' }
      },
      {
        path: 'formatter',
        name: 'DataFormatter',
        component: () => import('@/views/data/DataFormatter.vue'),
        meta: { title: '数据格式化', category: 'data' }
      },
      {
        path: 'crypto',
        name: 'CryptoTool',
        component: () => import('@/views/data/CryptoTool.vue'),
        meta: { title: '加密解密', category: 'data' }
      }
    ]
  },
  {
    path: '/dev',
    name: 'DevTools',
    children: [
      {
        path: 'api-tester',
        name: 'ApiTester',
        component: () => import('@/views/dev/ApiTester.vue'),
        meta: { title: 'API 测试', category: 'dev' }
      },
      {
        path: 'doc-generator',
        name: 'DocGenerator',
        component: () => import('@/views/dev/DocGenerator.vue'),
        meta: { title: '接口文档生成', category: 'dev' }
      },
      {
        path: 'mock-generator',
        name: 'MockGenerator',
        component: () => import('@/views/dev/MockGenerator.vue'),
        meta: { title: 'Mock 数据生成', category: 'dev' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由标题设置
router.beforeEach((to, _from, next) => {
  if (to.meta.title) {
    document.title = to.meta.title as string
  }
  next()
})

export default router
