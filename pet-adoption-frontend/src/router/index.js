import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    component: () => import('@/views/layout/MainLayout.vue'),
    children: [
      {
        path: '',
        name: 'Home',
        component: () => import('@/views/home/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'pets',
        name: 'PetList',
        component: () => import('@/views/pet/PetList.vue'),
        meta: { title: '宠物列表' }
      },
      {
        path: 'pets/:id',
        name: 'PetDetail',
        component: () => import('@/views/pet/PetDetail.vue'),
        meta: { title: '宠物详情' }
      },
      {
        path: 'publish',
        name: 'PetPublish',
        component: () => import('@/views/pet/PetPublish.vue'),
        meta: { title: '我要送养', requireAuth: true }
      },
      {
        path: 'announcements',
        name: 'Announcements',
        component: () => import('@/views/announcement/AnnouncementList.vue'),
        meta: { title: '公告列表' }
      },
      {
        path: 'forum',
        name: 'ForumList',
        component: () => import('@/views/forum/ForumList.vue'),
        meta: { title: '宠友交流' }
      },
      {
        path: 'forum/:id',
        name: 'ForumDetail',
        component: () => import('@/views/forum/ForumDetail.vue'),
        meta: { title: '帖子详情' }
      },
      {
        path: 'my-applications',
        name: 'MyApplications',
        component: () => import('@/views/application/MyApplications.vue'),
        meta: { title: '我的申请', requireAuth: true }
      },
      {
        path: 'received-applications',
        name: 'ReceivedApplications',
        component: () => import('@/views/application/ReceivedApplications.vue'),
        meta: { title: '收到的申请', requireAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人中心', requireAuth: true }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/auth/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/admin',
    component: () => import('@/views/layout/AdminLayout.vue'),
    meta: { requireAuth: true, requireAdmin: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '控制台' }
      },
      {
        path: 'pets',
        name: 'AdminPets',
        component: () => import('@/views/admin/PetManage.vue'),
        meta: { title: '宠物管理' }
      },
      {
        path: 'applications',
        name: 'AdminApplications',
        component: () => import('@/views/admin/ApplicationManage.vue'),
        meta: { title: '申请管理' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: () => import('@/views/admin/AnnouncementManage.vue'),
        meta: { title: '公告管理' }
      },
      {
        path: 'categories',
        name: 'AdminCategories',
        component: () => import('@/views/admin/CategoryManage.vue'),
        meta: { title: '分类管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 宠物领养系统` : '宠物领养系统'
  
  const userStore = useUserStore()
  
  if (to.meta.requireAuth && !userStore.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  
  if (to.meta.requireAdmin && !['ADMIN', 'SHELTER'].includes(userStore.user?.role)) {
    next({ name: 'Home' })
    return
  }
  
  next()
})

export default router
