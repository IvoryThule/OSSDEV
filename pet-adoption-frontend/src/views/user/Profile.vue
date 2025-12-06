<template>
  <div class="profile-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">个人中心</h1>
        <p class="page-subtitle">管理您的账户信息和安全设置</p>
      </div>
    </div>

    <div class="page-container">
      <div class="profile-card">
        <div class="profile-sidebar">
          <div class="user-info">
            <div class="avatar">
              <el-icon><User /></el-icon>
            </div>
            <h3 class="user-name">{{ infoForm.nickname || infoForm.username }}</h3>
            <p class="user-account">@{{ infoForm.username }}</p>
          </div>
          <nav class="profile-nav">
            <button 
              class="nav-item" 
              :class="{ active: activeTab === 'info' }"
              @click="activeTab = 'info'"
            >
              <el-icon><User /></el-icon>
              基本信息
            </button>
            <button 
              class="nav-item"
              :class="{ active: activeTab === 'password' }"
              @click="activeTab = 'password'"
            >
              <el-icon><Lock /></el-icon>
              修改密码
            </button>
          </nav>
        </div>

        <div class="profile-content">
          <!-- Basic Info Tab -->
          <div v-if="activeTab === 'info'" class="tab-content">
            <div class="content-header">
              <h2 class="content-title">基本信息</h2>
              <p class="content-desc">更新您的个人信息</p>
            </div>

            <form class="profile-form" @submit.prevent="updateInfo">
              <div class="form-group">
                <label class="form-label">用户名</label>
                <div class="input-wrapper disabled">
                  <el-icon><User /></el-icon>
                  <input type="text" :value="infoForm.username" disabled />
                </div>
                <span class="form-hint">用户名创建后不可修改</span>
              </div>

              <div class="form-group">
                <label class="form-label">昵称</label>
                <div class="input-wrapper">
                  <el-icon><UserFilled /></el-icon>
                  <input 
                    type="text" 
                    v-model="infoForm.nickname" 
                    placeholder="给自己起个名字" 
                  />
                </div>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">邮箱</label>
                  <div class="input-wrapper">
                    <el-icon><Message /></el-icon>
                    <input 
                      type="email" 
                      v-model="infoForm.email" 
                      placeholder="your@email.com" 
                    />
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">手机号</label>
                  <div class="input-wrapper">
                    <el-icon><Phone /></el-icon>
                    <input 
                      type="tel" 
                      v-model="infoForm.phone" 
                      placeholder="联系电话" 
                    />
                  </div>
                </div>
              </div>

              <div class="form-actions">
                <button type="submit" class="btn-primary" :disabled="infoLoading">
                  <el-icon v-if="infoLoading" class="is-loading"><Loading /></el-icon>
                  {{ infoLoading ? '保存中...' : '保存修改' }}
                </button>
              </div>
            </form>
          </div>

          <!-- Password Tab -->
          <div v-if="activeTab === 'password'" class="tab-content">
            <div class="content-header">
              <h2 class="content-title">修改密码</h2>
              <p class="content-desc">定期更换密码以保障账户安全</p>
            </div>

            <form class="profile-form" @submit.prevent="updatePassword">
              <div class="form-group">
                <label class="form-label">当前密码 <span class="required">*</span></label>
                <div class="input-wrapper">
                  <el-icon><Lock /></el-icon>
                  <input 
                    :type="showOldPwd ? 'text' : 'password'" 
                    v-model="pwdForm.oldPassword" 
                    placeholder="请输入当前密码" 
                  />
                  <button type="button" class="toggle-pwd" @click="showOldPwd = !showOldPwd">
                    <el-icon><View v-if="!showOldPwd" /><Hide v-else /></el-icon>
                  </button>
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">新密码 <span class="required">*</span></label>
                <div class="input-wrapper">
                  <el-icon><Lock /></el-icon>
                  <input 
                    :type="showNewPwd ? 'text' : 'password'" 
                    v-model="pwdForm.newPassword" 
                    placeholder="6-20个字符" 
                  />
                  <button type="button" class="toggle-pwd" @click="showNewPwd = !showNewPwd">
                    <el-icon><View v-if="!showNewPwd" /><Hide v-else /></el-icon>
                  </button>
                </div>
              </div>

              <div class="form-group">
                <label class="form-label">确认新密码 <span class="required">*</span></label>
                <div class="input-wrapper">
                  <el-icon><Lock /></el-icon>
                  <input 
                    :type="showConfirmPwd ? 'text' : 'password'" 
                    v-model="pwdForm.confirmPassword" 
                    placeholder="再次输入新密码" 
                  />
                  <button type="button" class="toggle-pwd" @click="showConfirmPwd = !showConfirmPwd">
                    <el-icon><View v-if="!showConfirmPwd" /><Hide v-else /></el-icon>
                  </button>
                </div>
                <span v-if="pwdForm.confirmPassword && pwdForm.confirmPassword !== pwdForm.newPassword" class="form-error">
                  两次输入的密码不一致
                </span>
              </div>

              <div class="form-actions">
                <button 
                  type="submit" 
                  class="btn-primary" 
                  :disabled="pwdLoading || !canSubmitPassword"
                >
                  <el-icon v-if="pwdLoading" class="is-loading"><Loading /></el-icon>
                  {{ pwdLoading ? '修改中...' : '修改密码' }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { updateUser, changePassword } from '@/api/user'
import { ElMessage } from 'element-plus'
import { User, Lock, UserFilled, Message, Phone, Loading, View, Hide } from '@element-plus/icons-vue'

const userStore = useUserStore()
const activeTab = ref('info')

const infoLoading = ref(false)
const infoForm = reactive({
  username: '',
  nickname: '',
  email: '',
  phone: ''
})

const pwdLoading = ref(false)
const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const showOldPwd = ref(false)
const showNewPwd = ref(false)
const showConfirmPwd = ref(false)

const canSubmitPassword = computed(() => {
  return pwdForm.oldPassword && 
         pwdForm.newPassword && 
         pwdForm.newPassword.length >= 6 &&
         pwdForm.confirmPassword === pwdForm.newPassword
})

onMounted(() => {
  const user = userStore.user
  if (user) {
    infoForm.username = user.username
    infoForm.nickname = user.nickname || ''
    infoForm.email = user.email || ''
    infoForm.phone = user.phone || ''
  }
})

async function updateInfo() {
  infoLoading.value = true
  try {
    const res = await updateUser(userStore.user.userId, {
      nickname: infoForm.nickname,
      email: infoForm.email,
      phone: infoForm.phone
    })
    if (res.code === 200) {
      ElMessage.success('修改成功')
      userStore.updateUser(infoForm)
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    infoLoading.value = false
  }
}

async function updatePassword() {
  if (!canSubmitPassword.value) return
  pwdLoading.value = true
  try {
    const res = await changePassword({
      userId: userStore.user.userId,
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    if (res.code === 200) {
      ElMessage.success('密码修改成功，请重新登录')
      userStore.logout()
      location.href = '/login'
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    pwdLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.profile-page {
  min-height: 100vh;
  background: $color-cream;
}

.page-header {
  background: $color-primary;
  padding: $spacing-16 0;
}

.header-content {
  max-width: $container-max-width;
  margin: 0 auto;
  padding: 0 $spacing-6;
  text-align: center;
}

.page-title {
  font-size: $font-size-4xl;
  font-weight: $font-weight-bold;
  color: $color-white;
  margin-bottom: $spacing-3;
}

.page-subtitle {
  font-size: $font-size-lg;
  color: rgba($color-white, 0.8);
}

.page-container {
  max-width: 960px;
  margin: 0 auto;
  padding: $spacing-12 $spacing-6;
}

.profile-card {
  display: grid;
  grid-template-columns: 280px 1fr;
  background: $color-white;
  border-radius: $radius-xl;
  overflow: hidden;
  box-shadow: $shadow-sm;
  
  @media (max-width: $breakpoint-md) {
    grid-template-columns: 1fr;
  }
}

.profile-sidebar {
  background: $color-primary;
  padding: $spacing-8;
  
  @media (max-width: $breakpoint-md) {
    padding: $spacing-6;
  }
}

.user-info {
  text-align: center;
  padding-bottom: $spacing-6;
  margin-bottom: $spacing-6;
  border-bottom: 1px solid rgba($color-white, 0.1);
}

.avatar {
  width: 80px;
  height: 80px;
  background: rgba($color-white, 0.1);
  border-radius: $radius-full;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto $spacing-4;
  
  .el-icon {
    font-size: 36px;
    color: $color-white;
  }
}

.user-name {
  font-size: $font-size-xl;
  font-weight: $font-weight-semibold;
  color: $color-white;
  margin-bottom: $spacing-1;
}

.user-account {
  font-size: $font-size-sm;
  color: rgba($color-white, 0.6);
}

.profile-nav {
  display: flex;
  flex-direction: column;
  gap: $spacing-2;
  
  @media (max-width: $breakpoint-md) {
    flex-direction: row;
  }
}

.nav-item {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  padding: $spacing-3 $spacing-4;
  background: transparent;
  color: rgba($color-white, 0.7);
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  border: none;
  border-radius: $radius-lg;
  cursor: pointer;
  transition: all $transition-fast;
  text-align: left;
  
  &:hover {
    background: rgba($color-white, 0.1);
    color: $color-white;
  }
  
  &.active {
    background: rgba($color-white, 0.15);
    color: $color-white;
  }
  
  .el-icon {
    font-size: 18px;
  }
}

.profile-content {
  padding: $spacing-8;
  
  @media (max-width: $breakpoint-md) {
    padding: $spacing-6;
  }
}

.content-header {
  margin-bottom: $spacing-8;
}

.content-title {
  font-size: $font-size-2xl;
  font-weight: $font-weight-semibold;
  color: $color-primary;
  margin-bottom: $spacing-2;
}

.content-desc {
  font-size: $font-size-base;
  color: $color-gray-500;
}

.profile-form {
  max-width: 480px;
}

.form-group {
  margin-bottom: $spacing-6;
}

.form-label {
  display: block;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  color: $color-gray-700;
  margin-bottom: $spacing-2;
  
  .required {
    color: $color-error;
  }
}

.input-wrapper {
  display: flex;
  align-items: center;
  padding: $spacing-3 $spacing-4;
  background: $color-cream;
  border: 1px solid $color-gray-200;
  border-radius: $radius-lg;
  transition: all $transition-fast;
  
  &:focus-within {
    border-color: $color-primary;
    box-shadow: 0 0 0 3px rgba($color-primary, 0.1);
  }
  
  &.disabled {
    background: $color-gray-100;
    
    input {
      color: $color-gray-500;
    }
  }
  
  .el-icon {
    color: $color-gray-400;
    margin-right: $spacing-3;
    font-size: 18px;
  }
  
  input {
    flex: 1;
    border: none;
    background: transparent;
    font-size: $font-size-base;
    color: $color-gray-800;
    outline: none;
    
    &::placeholder {
      color: $color-gray-400;
    }
    
    &:disabled {
      cursor: not-allowed;
    }
  }
}

.toggle-pwd {
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  color: $color-gray-400;
  cursor: pointer;
  padding: $spacing-1;
  
  &:hover {
    color: $color-gray-600;
  }
}

.form-hint {
  display: block;
  font-size: $font-size-xs;
  color: $color-gray-400;
  margin-top: $spacing-2;
}

.form-error {
  display: block;
  font-size: $font-size-xs;
  color: $color-error;
  margin-top: $spacing-2;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $spacing-4;
  
  @media (max-width: $breakpoint-sm) {
    grid-template-columns: 1fr;
  }
  
  .form-group {
    margin-bottom: 0;
  }
}

.form-actions {
  margin-top: $spacing-8;
}

.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-2;
  padding: $spacing-3 $spacing-8;
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

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
