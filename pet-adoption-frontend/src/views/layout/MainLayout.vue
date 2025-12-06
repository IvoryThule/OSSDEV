<template>
  <div class="main-layout">
    <!-- Premium Header -->
    <header class="header">
      <div class="header-container">
        <div class="logo" @click="$router.push('/')">
          <el-icon class="logo-icon"><House /></el-icon>
          <span class="logo-text">PetHaven</span>
        </div>
        
        <nav class="nav-menu">
          <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">
            首页
          </router-link>
          <router-link to="/pets" class="nav-link" :class="{ active: $route.path.startsWith('/pets') }">
            领养宠物
          </router-link>
          <router-link to="/publish" class="nav-link" :class="{ active: $route.path === '/publish' }">
            我要送养
          </router-link>
          <router-link to="/announcements" class="nav-link" :class="{ active: $route.path === '/announcements' }">
            公告
          </router-link>
          <router-link to="/forum" class="nav-link" :class="{ active: $route.path.startsWith('/forum') }">
            宠友交流
          </router-link>
        </nav>
        
        <div class="user-area">
          <template v-if="userStore.isLoggedIn">
            <el-dropdown @command="handleCommand" trigger="click">
              <div class="user-trigger">
                <el-avatar :size="36" :src="userStore.user?.avatar" class="user-avatar">
                  {{ userStore.user?.nickname?.charAt(0) || userStore.user?.username?.charAt(0) }}
                </el-avatar>
                <span class="user-name">{{ userStore.user?.nickname || userStore.user?.username }}</span>
                <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    <span>个人中心</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="my-pets">
                    <el-icon><Star /></el-icon>
                    <span>我的送养</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="applications">
                    <el-icon><Document /></el-icon>
                    <span>我的申请</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="received-applications">
                    <el-icon><Message /></el-icon>
                    <span>收到的申请</span>
                  </el-dropdown-item>
                  <el-dropdown-item v-if="userStore.canManage" command="admin" divided>
                    <el-icon><Setting /></el-icon>
                    <span>管理后台</span>
                  </el-dropdown-item>
                  <el-dropdown-item command="logout" divided>
                    <el-icon><SwitchButton /></el-icon>
                    <span>退出登录</span>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <button class="btn-link" @click="$router.push('/login')">登录</button>
            <button class="btn-primary" @click="$router.push('/register')">注册</button>
          </template>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- Elegant Footer -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-brand">
          <el-icon class="footer-logo"><House /></el-icon>
          <span>PetHaven</span>
        </div>
        <p class="footer-text">让每一个生命都被温柔以待</p>
        <div class="footer-copyright">
          © {{ new Date().getFullYear() }} 宠物领养管理系统 · IvoryThule
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { House, User, Document, Setting, SwitchButton, ArrowDown, Message, Star } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

function handleCommand(command) {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'my-pets':
      router.push('/my-pets')
      break
    case 'applications':
      router.push('/my-applications')
      break
    case 'received-applications':
      router.push('/received-applications')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      userStore.logout()
      ElMessage.success('已退出登录')
      router.push('/')
      break
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: $color-cream;
}

// Header
.header {
  position: sticky;
  top: 0;
  z-index: $z-sticky;
  background: rgba($color-white, 0.95);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid $color-gray-200;
}

.header-container {
  max-width: $container-max-width;
  margin: 0 auto;
  padding: 0 $spacing-8;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  cursor: pointer;
  transition: opacity $transition-fast;
  
  &:hover {
    opacity: 0.8;
  }
}

.logo-icon {
  font-size: 28px;
  color: $color-primary;
}

.logo-text {
  font-size: $font-size-xl;
  font-weight: $font-weight-bold;
  color: $color-primary;
  letter-spacing: $letter-spacing-tight;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: $spacing-10;
}

.nav-link {
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  color: $color-gray-600;
  text-decoration: none;
  letter-spacing: $letter-spacing-wide;
  text-transform: uppercase;
  padding: $spacing-2 0;
  position: relative;
  transition: color $transition-fast;
  
  &::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    width: 0;
    height: 2px;
    background-color: $color-primary;
    transition: width $transition-base;
  }
  
  &:hover,
  &.active {
    color: $color-primary;
    
    &::after {
      width: 100%;
    }
  }
  
  &.active {
    font-weight: $font-weight-semibold;
  }
}

.user-area {
  display: flex;
  align-items: center;
  gap: $spacing-4;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  cursor: pointer;
  padding: $spacing-2 $spacing-3;
  border-radius: $radius-full;
  transition: background-color $transition-fast;
  
  &:hover {
    background-color: $color-gray-100;
  }
}

.user-avatar {
  background-color: $color-primary;
  font-weight: $font-weight-semibold;
  font-size: $font-size-sm;
}

.user-name {
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  color: $color-gray-700;
}

.dropdown-arrow {
  font-size: 12px;
  color: $color-gray-500;
}

.btn-link {
  background: none;
  border: none;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  color: $color-gray-600;
  cursor: pointer;
  padding: $spacing-2 $spacing-4;
  transition: color $transition-fast;
  
  &:hover {
    color: $color-primary;
  }
}

.btn-primary {
  background-color: $color-primary;
  border: none;
  color: $color-white;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  padding: $spacing-3 $spacing-6;
  border-radius: $radius-sm;
  cursor: pointer;
  transition: all $transition-fast;
  
  &:hover {
    background-color: $color-primary-light;
    transform: translateY(-1px);
  }
}

// Main Content
.main-content {
  flex: 1;
  padding: $spacing-12 $spacing-8;
  max-width: $container-max-width;
  margin: 0 auto;
  width: 100%;
}

// Footer
.footer {
  background-color: $color-primary;
  color: $color-white;
  padding: $spacing-16 $spacing-8;
  margin-top: auto;
}

.footer-container {
  max-width: $container-max-width;
  margin: 0 auto;
  text-align: center;
}

.footer-brand {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-3;
  margin-bottom: $spacing-4;
}

.footer-logo {
  font-size: 24px;
  opacity: 0.9;
}

.footer-brand span {
  font-size: $font-size-lg;
  font-weight: $font-weight-bold;
  letter-spacing: $letter-spacing-wide;
}

.footer-text {
  font-size: $font-size-sm;
  color: rgba($color-white, 0.7);
  margin-bottom: $spacing-6;
  letter-spacing: $letter-spacing-wider;
}

.footer-copyright {
  font-size: $font-size-xs;
  color: rgba($color-white, 0.5);
}

// Transition
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

// Responsive
@media (max-width: $breakpoint-md) {
  .header-container {
    padding: 0 $spacing-4;
  }
  
  .nav-menu {
    gap: $spacing-6;
  }
  
  .nav-link {
    font-size: $font-size-xs;
  }
  
  .main-content {
    padding: $spacing-8 $spacing-4;
  }
}
</style>
