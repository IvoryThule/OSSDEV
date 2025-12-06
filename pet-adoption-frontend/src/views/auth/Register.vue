<template>
  <div class="auth-page">
    <div class="auth-container">
      <!-- Left Side - Branding -->
      <div class="auth-branding">
        <div class="branding-content">
          <div class="branding-logo">
            <el-icon><House /></el-icon>
          </div>
          <h1 class="branding-title">PetHaven</h1>
          <p class="branding-tagline">让每一个生命都被温柔以待</p>
          <div class="branding-features">
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>发现等待领养的可爱宠物</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>便捷的在线申请流程</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>专业的领养指导服务</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Right Side - Form -->
      <div class="auth-form-section">
        <div class="auth-form-container">
          <div class="form-header">
            <h2 class="form-title">创建账户</h2>
            <p class="form-subtitle">加入我们，开启领养之旅</p>
          </div>

          <el-form ref="formRef" :model="form" :rules="rules" class="auth-form" @submit.prevent="handleRegister">
            <el-form-item prop="username">
              <div class="input-group">
                <label class="input-label">用户名 <span class="required">*</span></label>
                <el-input 
                  v-model="form.username" 
                  placeholder="4-20个字符" 
                  size="large"
                  class="custom-input"
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </div>
            </el-form-item>

            <div class="form-row">
              <el-form-item prop="password" class="form-row-item">
                <div class="input-group">
                  <label class="input-label">密码 <span class="required">*</span></label>
                  <el-input 
                    v-model="form.password" 
                    type="password" 
                    placeholder="6-20个字符" 
                    size="large"
                    show-password
                    class="custom-input"
                  >
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>
                </div>
              </el-form-item>
              
              <el-form-item prop="confirmPassword" class="form-row-item">
                <div class="input-group">
                  <label class="input-label">确认密码 <span class="required">*</span></label>
                  <el-input 
                    v-model="form.confirmPassword" 
                    type="password" 
                    placeholder="再次输入密码" 
                    size="large"
                    show-password
                    class="custom-input"
                  >
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>
                </div>
              </el-form-item>
            </div>

            <el-form-item prop="nickname">
              <div class="input-group">
                <label class="input-label">昵称</label>
                <el-input 
                  v-model="form.nickname" 
                  placeholder="选填，给自己起个名字" 
                  size="large"
                  class="custom-input"
                >
                  <template #prefix>
                    <el-icon><UserFilled /></el-icon>
                  </template>
                </el-input>
              </div>
            </el-form-item>

            <el-form-item prop="phone">
              <div class="input-group">
                <label class="input-label">手机号</label>
                <el-input 
                  v-model="form.phone" 
                  placeholder="选填，方便我们联系您" 
                  size="large"
                  class="custom-input"
                >
                  <template #prefix>
                    <el-icon><Phone /></el-icon>
                  </template>
                </el-input>
              </div>
            </el-form-item>

            <div class="form-actions">
              <button type="submit" class="btn-register" :disabled="loading">
                <el-icon v-if="loading" class="is-loading"><Loading /></el-icon>
                {{ loading ? '注册中...' : '创建账户' }}
              </button>
            </div>
          </el-form>

          <div class="form-footer">
            <span class="footer-text">已有账号？</span>
            <router-link to="/login" class="footer-link">立即登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { House, Check, User, Lock, UserFilled, Phone, Loading } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  phone: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 4, max: 20, message: '用户名长度应在4-20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度应在6-20个字符之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

async function handleRegister() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await register(form)
    if (res.code === 200) {
      ElMessage.success('注册成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.auth-page {
  min-height: 100vh;
  display: flex;
  background: $color-cream;
}

.auth-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  width: 100%;
  
  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
  }
}

// Branding Side
.auth-branding {
  background: $color-primary;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-12;
  
  @media (max-width: $breakpoint-lg) {
    display: none;
  }
}

.branding-content {
  max-width: 400px;
  color: $color-white;
}

.branding-logo {
  width: 64px;
  height: 64px;
  background: rgba($color-white, 0.1);
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $spacing-6;
  
  .el-icon {
    font-size: 32px;
  }
}

.branding-title {
  font-size: $font-size-4xl;
  font-weight: $font-weight-bold;
  margin-bottom: $spacing-3;
}

.branding-tagline {
  font-size: $font-size-lg;
  opacity: 0.8;
  margin-bottom: $spacing-12;
}

.branding-features {
  display: flex;
  flex-direction: column;
  gap: $spacing-4;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  font-size: $font-size-sm;
  opacity: 0.9;
  
  .el-icon {
    width: 24px;
    height: 24px;
    background: rgba($color-white, 0.1);
    border-radius: $radius-full;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
  }
}

// Form Side
.auth-form-section {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-8;
  overflow-y: auto;
}

.auth-form-container {
  width: 100%;
  max-width: 480px;
}

.form-header {
  text-align: center;
  margin-bottom: $spacing-8;
}

.form-title {
  font-size: $font-size-3xl;
  font-weight: $font-weight-bold;
  color: $color-primary;
  margin-bottom: $spacing-2;
}

.form-subtitle {
  font-size: $font-size-base;
  color: $color-gray-500;
}

.auth-form {
  :deep(.el-form-item) {
    margin-bottom: $spacing-5;
  }
  
  :deep(.el-form-item__error) {
    padding-top: $spacing-1;
  }
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $spacing-4;
  
  @media (max-width: $breakpoint-sm) {
    grid-template-columns: 1fr;
  }
}

.form-row-item {
  margin-bottom: 0 !important;
}

.input-group {
  width: 100%;
}

.input-label {
  display: block;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  color: $color-gray-700;
  margin-bottom: $spacing-2;
  
  .required {
    color: $color-error;
  }
}

.custom-input {
  :deep(.el-input__wrapper) {
    padding: $spacing-3 $spacing-4;
    border-radius: $radius-lg;
    box-shadow: none;
    border: 1px solid $color-gray-300;
    transition: all $transition-fast;
    
    &:hover {
      border-color: $color-gray-400;
    }
    
    &.is-focus {
      border-color: $color-primary;
      box-shadow: 0 0 0 3px rgba($color-primary, 0.1);
    }
  }
  
  :deep(.el-input__prefix) {
    color: $color-gray-400;
  }
}

.form-actions {
  margin-top: $spacing-6;
}

.btn-register {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-2;
  padding: $spacing-4 $spacing-8;
  background: $color-primary;
  color: $color-white;
  font-size: $font-size-base;
  font-weight: $font-weight-semibold;
  border: none;
  border-radius: $radius-lg;
  cursor: pointer;
  transition: all $transition-base;
  
  &:hover:not(:disabled) {
    background: $color-primary-light;
    transform: translateY(-1px);
    box-shadow: $shadow-md;
  }
  
  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
  }
  
  .is-loading {
    animation: rotate 1s linear infinite;
  }
}

.form-footer {
  margin-top: $spacing-6;
  text-align: center;
}

.footer-text {
  font-size: $font-size-sm;
  color: $color-gray-500;
}

.footer-link {
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
  color: $color-primary;
  text-decoration: none;
  margin-left: $spacing-1;
  
  &:hover {
    text-decoration: underline;
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
