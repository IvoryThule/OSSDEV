<template>
  <div class="pet-detail" v-loading="loading">
    <!-- Breadcrumb -->
    <nav class="breadcrumb" v-if="pet">
      <router-link to="/pets" class="breadcrumb-link">
        <el-icon><ArrowLeft /></el-icon>
        返回列表
      </router-link>
    </nav>

    <div class="detail-container" v-if="pet">
      <!-- Image Gallery -->
      <div class="gallery-section">
        <div class="main-image-wrapper">
          <img :src="getImageUrl(pet.imageUrl)" :alt="pet.name" class="main-image" />
          <div class="image-badge" :class="getStatusClass(pet.status)">
            {{ getStatusText(pet.status) }}
          </div>
        </div>
      </div>

      <!-- Info Section -->
      <div class="info-section">
        <div class="info-header">
          <div class="name-group">
            <h1 class="pet-name">{{ pet.name }}</h1>
            <span class="pet-gender" :class="getGenderClass(pet.gender)">
              <el-icon>
                <Male v-if="pet.gender === 1" />
                <Female v-else />
              </el-icon>
              {{ getGenderText(pet.gender) }}
            </span>
          </div>
          <p class="pet-breed">{{ pet.categoryName }} · {{ pet.breed }}</p>
        </div>

        <!-- Quick Stats -->
        <div class="quick-stats">
          <div class="stat-card">
            <el-icon class="stat-card-icon"><Calendar /></el-icon>
            <div class="stat-card-content">
              <span class="stat-card-label">年龄</span>
              <span class="stat-card-value">{{ pet.age || '未知' }}</span>
            </div>
          </div>
          <div class="stat-card">
            <el-icon class="stat-card-icon"><ScaleToOriginal /></el-icon>
            <div class="stat-card-content">
              <span class="stat-card-label">体重</span>
              <span class="stat-card-value">{{ pet.weight ? pet.weight + 'kg' : '未知' }}</span>
            </div>
          </div>
          <div class="stat-card">
            <el-icon class="stat-card-icon"><Brush /></el-icon>
            <div class="stat-card-content">
              <span class="stat-card-label">毛色</span>
              <span class="stat-card-value">{{ pet.color || '未知' }}</span>
            </div>
          </div>
        </div>

        <!-- Health Info -->
        <div class="health-section">
          <h3 class="section-title">
            <el-icon><FirstAidKit /></el-icon>
            健康信息
          </h3>
          <div class="health-grid">
            <div class="health-item">
              <span class="health-label">健康状况</span>
              <span class="health-value">{{ pet.healthStatus || '未知' }}</span>
            </div>
            <div class="health-item">
              <span class="health-label">疫苗接种</span>
              <span class="health-badge" :class="pet.isVaccinated ? 'positive' : 'neutral'">
                <el-icon>
                  <Check v-if="pet.isVaccinated" />
                  <Close v-else />
                </el-icon>
                {{ pet.isVaccinated ? '已接种' : '未接种' }}
              </span>
            </div>
            <div class="health-item">
              <span class="health-label">绝育情况</span>
              <span class="health-badge" :class="pet.isSterilized ? 'positive' : 'neutral'">
                <el-icon>
                  <Check v-if="pet.isSterilized" />
                  <Close v-else />
                </el-icon>
                {{ pet.isSterilized ? '已绝育' : '未绝育' }}
              </span>
            </div>
          </div>
        </div>

        <!-- Description -->
        <div class="description-section">
          <h3 class="section-title">
            <el-icon><Document /></el-icon>
            详细介绍
          </h3>
          <p class="description-text">{{ pet.description || '这个小可爱还没有详细介绍，但它一定会成为您最好的伙伴！' }}</p>
        </div>

        <!-- Publisher Info -->
        <div class="publisher-section">
          <div class="publisher-info">
            <el-icon class="publisher-icon"><User /></el-icon>
            <div class="publisher-content">
              <span class="publisher-label">发布者</span>
              <span class="publisher-name">{{ pet.publisherName }}</span>
            </div>
          </div>
        </div>

        <!-- Action Button -->
        <div class="action-section" v-if="pet.status === 0">
          <button 
            class="btn-adopt" 
            @click="showApplyDialog" 
            :disabled="!userStore.isLoggedIn || isPublisher"
          >
            <el-icon><Edit /></el-icon>
            {{ getButtonText() }}
          </button>
          <p class="action-hint" v-if="!userStore.isLoggedIn">
            请先 <router-link to="/login">登录</router-link> 后再申请领养
          </p>
          <p class="action-hint" v-if="isPublisher">
            您是该宠物的发布者，无法申请领养
          </p>
        </div>
      </div>
    </div>

    <!-- Apply Dialog -->
    <el-dialog 
      v-model="applyDialogVisible" 
      title="申请领养"
      width="520px"
      :close-on-click-modal="false"
      class="apply-dialog"
    >
      <div class="dialog-intro">
        <p>请填写以下信息，我们将尽快审核您的申请</p>
      </div>
      
      <el-form ref="applyFormRef" :model="applyForm" :rules="applyRules" label-position="top" class="apply-form">
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="applyForm.contactPhone" placeholder="请输入您的联系电话">
            <template #prefix>
              <el-icon><Phone /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="联系地址" prop="contactAddress">
          <el-input v-model="applyForm.contactAddress" placeholder="请输入您的联系地址">
            <template #prefix>
              <el-icon><Location /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="居住条件" prop="livingCondition">
          <el-input v-model="applyForm.livingCondition" placeholder="如：有独立住房、有阳台等">
            <template #prefix>
              <el-icon><House /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="养宠经验" prop="experience">
          <el-input v-model="applyForm.experience" placeholder="如：曾养过3年猫咪">
            <template #prefix>
              <el-icon><Star /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item label="领养理由" prop="reason">
          <el-input 
            v-model="applyForm.reason" 
            type="textarea" 
            :rows="4" 
            placeholder="请详细说明您的领养理由，这将帮助我们更好地了解您"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <button class="btn-cancel" @click="applyDialogVisible = false">取消</button>
          <button class="btn-submit" @click="submitApply" :disabled="submitting">
            <el-icon v-if="submitting" class="is-loading"><Loading /></el-icon>
            {{ submitting ? '提交中...' : '提交申请' }}
          </button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getPetDetail } from '@/api/pet'
import { submitApplication } from '@/api/application'
import { ElMessage } from 'element-plus'
import { 
  ArrowLeft, Male, Female, Calendar, ScaleToOriginal, Brush,
  FirstAidKit, Check, Close, Document, User, Edit, Phone,
  Location, House, Star, Loading
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const pet = ref(null)
const loading = ref(true)
const applyDialogVisible = ref(false)
const applyFormRef = ref(null)
const submitting = ref(false)

const isPublisher = computed(() => {
  return userStore.isLoggedIn && pet.value && userStore.user?.id === pet.value.publisherId
})

function getButtonText() {
  if (!userStore.isLoggedIn) return '登录后申请领养'
  if (isPublisher.value) return '无法申请'
  return '申请领养'
}

const applyForm = ref({
  petId: null,
  contactPhone: '',
  contactAddress: '',
  livingCondition: '',
  experience: '',
  reason: ''
})

const applyRules = {
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入领养理由', trigger: 'blur' }]
}

onMounted(async () => {
  const id = route.params.id
  applyForm.value.petId = Number(id)
  try {
    const res = await getPetDetail(id)
    if (res.code === 200) {
      pet.value = res.data
    }
  } finally {
    loading.value = false
  }
})

function getImageUrl(url) {
  return url || 'https://placehold.co/600x400/e8c547/1a1a2e?text=Pet+Image'
}

function getGenderClass(gender) {
  return gender === 1 ? 'male' : 'female'
}

function getGenderText(gender) {
  return gender === 1 ? '公' : gender === 2 ? '母' : '未知'
}

function getStatusText(status) {
  const map = { 0: '待领养', 1: '申请中', 2: '已领养', 3: '已下架' }
  return map[status] || '未知'
}

function getStatusClass(status) {
  const map = { 0: 'available', 1: 'pending', 2: 'adopted', 3: 'offline' }
  return map[status] || ''
}

function showApplyDialog() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  applyForm.contactPhone = userStore.user?.phone || ''
  applyDialogVisible.value = true
}

async function submitApply() {
  await applyFormRef.value.validate()
  submitting.value = true
  try {
    const res = await submitApplication(applyForm)
    if (res.code === 200) {
      ElMessage.success('申请提交成功，请耐心等待审核')
      applyDialogVisible.value = false
      router.push('/my-applications')
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.pet-detail {
  animation: fadeIn 0.5s ease;
  min-height: 400px;
}

// Breadcrumb
.breadcrumb {
  margin-bottom: $spacing-8;
}

.breadcrumb-link {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  color: $color-gray-600;
  text-decoration: none;
  transition: color $transition-fast;
  
  &:hover {
    color: $color-primary;
  }
}

// Main Container
.detail-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $spacing-12;
  
  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
    gap: $spacing-8;
  }
}

// Gallery Section
.gallery-section {
  position: sticky;
  top: 100px;
  align-self: start;
  
  @media (max-width: $breakpoint-lg) {
    position: static;
  }
}

.main-image-wrapper {
  position: relative;
  border-radius: $radius-xl;
  overflow: hidden;
  background: $color-gray-100;
}

.main-image {
  width: 100%;
  height: 500px;
  object-fit: cover;
  display: block;
  
  @media (max-width: $breakpoint-lg) {
    height: 400px;
  }
}

.image-badge {
  position: absolute;
  top: $spacing-4;
  left: $spacing-4;
  padding: $spacing-2 $spacing-4;
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
  border-radius: $radius-full;
  text-transform: uppercase;
  letter-spacing: $letter-spacing-wide;
  
  &.available {
    background: $color-success;
    color: $color-white;
  }
  
  &.pending {
    background: $color-warning;
    color: $color-white;
  }
  
  &.adopted {
    background: $color-gray-600;
    color: $color-white;
  }
}

// Info Section
.info-section {
  padding: $spacing-4 0;
}

.info-header {
  margin-bottom: $spacing-8;
  padding-bottom: $spacing-6;
  border-bottom: 1px solid $color-gray-200;
}

.name-group {
  display: flex;
  align-items: center;
  gap: $spacing-4;
  margin-bottom: $spacing-2;
}

.pet-name {
  font-size: $font-size-4xl;
  font-weight: $font-weight-bold;
  color: $color-primary;
}

.pet-gender {
  display: inline-flex;
  align-items: center;
  gap: $spacing-1;
  padding: $spacing-1 $spacing-3;
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
  border-radius: $radius-full;
  
  &.male {
    background: rgba($color-info, 0.1);
    color: $color-info;
  }
  
  &.female {
    background: rgba(#d4a5a5, 0.2);
    color: #a67c7c;
  }
}

.pet-breed {
  font-size: $font-size-lg;
  color: $color-gray-500;
}

// Quick Stats
.quick-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-4;
  margin-bottom: $spacing-8;
  
  @media (max-width: $breakpoint-sm) {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  display: flex;
  align-items: center;
  gap: $spacing-4;
  padding: $spacing-5;
  background: $color-white;
  border-radius: $radius-lg;
  box-shadow: $shadow-sm;
}

.stat-card-icon {
  font-size: 24px;
  color: $color-accent;
}

.stat-card-content {
  display: flex;
  flex-direction: column;
}

.stat-card-label {
  font-size: $font-size-xs;
  color: $color-gray-500;
  text-transform: uppercase;
  letter-spacing: $letter-spacing-wider;
}

.stat-card-value {
  font-size: $font-size-base;
  font-weight: $font-weight-semibold;
  color: $color-primary;
}

// Section Styles
.section-title {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  font-size: $font-size-lg;
  font-weight: $font-weight-semibold;
  color: $color-primary;
  margin-bottom: $spacing-4;
  
  .el-icon {
    color: $color-accent;
  }
}

// Health Section
.health-section {
  margin-bottom: $spacing-8;
  padding: $spacing-6;
  background: $color-white;
  border-radius: $radius-lg;
  box-shadow: $shadow-sm;
}

.health-grid {
  display: grid;
  gap: $spacing-4;
}

.health-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $spacing-3 0;
  border-bottom: 1px solid $color-gray-100;
  
  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }
}

.health-label {
  font-size: $font-size-sm;
  color: $color-gray-600;
}

.health-value {
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  color: $color-primary;
}

.health-badge {
  display: inline-flex;
  align-items: center;
  gap: $spacing-1;
  padding: $spacing-1 $spacing-3;
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  border-radius: $radius-full;
  
  &.positive {
    background: rgba($color-success, 0.1);
    color: $color-success;
  }
  
  &.neutral {
    background: $color-gray-100;
    color: $color-gray-500;
  }
}

// Description Section
.description-section {
  margin-bottom: $spacing-8;
  padding: $spacing-6;
  background: $color-white;
  border-radius: $radius-lg;
  box-shadow: $shadow-sm;
}

.description-text {
  font-size: $font-size-base;
  color: $color-gray-600;
  line-height: $line-height-relaxed;
  white-space: pre-line;
}

// Publisher Section
.publisher-section {
  margin-bottom: $spacing-8;
}

.publisher-info {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  padding: $spacing-4;
  background: $color-gray-100;
  border-radius: $radius-lg;
}

.publisher-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $color-primary;
  color: $color-white;
  border-radius: $radius-full;
  font-size: 18px;
}

.publisher-content {
  display: flex;
  flex-direction: column;
}

.publisher-label {
  font-size: $font-size-xs;
  color: $color-gray-500;
}

.publisher-name {
  font-size: $font-size-base;
  font-weight: $font-weight-semibold;
  color: $color-primary;
}

// Action Section
.action-section {
  padding-top: $spacing-6;
  border-top: 1px solid $color-gray-200;
}

.btn-adopt {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-2;
  padding: $spacing-5 $spacing-8;
  background: $color-primary;
  color: $color-white;
  font-size: $font-size-lg;
  font-weight: $font-weight-semibold;
  border: none;
  border-radius: $radius-lg;
  cursor: pointer;
  transition: all $transition-base;
  
  &:hover:not(:disabled) {
    background: $color-primary-light;
    transform: translateY(-2px);
    box-shadow: $shadow-lg;
  }
  
  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

.action-hint {
  margin-top: $spacing-4;
  text-align: center;
  font-size: $font-size-sm;
  color: $color-gray-500;
  
  a {
    color: $color-primary;
    font-weight: $font-weight-medium;
  }
}

// Dialog Styles
.dialog-intro {
  margin-bottom: $spacing-6;
  padding: $spacing-4;
  background: $color-gray-100;
  border-radius: $radius-md;
  
  p {
    font-size: $font-size-sm;
    color: $color-gray-600;
    margin: 0;
  }
}

.apply-form {
  :deep(.el-form-item__label) {
    font-weight: $font-weight-semibold;
    color: $color-gray-700;
  }
}

.dialog-footer {
  display: flex;
  gap: $spacing-4;
  justify-content: flex-end;
}

.btn-cancel {
  padding: $spacing-3 $spacing-6;
  background: $color-white;
  color: $color-gray-700;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  border: 1px solid $color-gray-300;
  border-radius: $radius-md;
  cursor: pointer;
  transition: all $transition-fast;
  
  &:hover {
    background: $color-gray-100;
  }
}

.btn-submit {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-3 $spacing-6;
  background: $color-primary;
  color: $color-white;
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
  border: none;
  border-radius: $radius-md;
  cursor: pointer;
  transition: all $transition-fast;
  
  &:hover:not(:disabled) {
    background: $color-primary-light;
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

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
