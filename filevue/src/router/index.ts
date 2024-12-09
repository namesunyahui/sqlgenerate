import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/home',
      name: 'home',
      component: HomeView
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue')
    },
    {
      path: '/uploadFile',
      name: 'uploadFile',
      component: () => import('../views/FileUpload.vue') // 确保文件路径和名称正确
    },
    {
      path: '/chatAI',
      name: 'chatAI',
      component: () => import('../views/ChatAI.vue') 
    },
    {
      path: '/chatAISee',
      name: 'chatAISee',
      component: () => import('../views/ChatAISee.vue') 
    }
  ]
})

export default router
