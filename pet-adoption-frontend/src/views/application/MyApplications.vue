<template>
  <div class="applications-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">我的领养申请</h1>
        <p class="page-subtitle">追踪您的领养申请状态</p>
      </div>
    </div>

    <div class="page-container">
      <div class="applications-list" v-loading="loading">
        <div 
          v-for="app in applications" 
          :key="app.id" 
          class="application-card"
        >
          <div class="card-image">
            <img :src="app.petImageUrl || 'https://placehold.co/600x400/e8c547/1a1a2e?text=Pet+Image'" :alt="app.petName" />
          </div>
          <div class="card-body">
            <div class="card-main">
              <h3 class="pet-name">{{ app.petName }}</h3>
              <p class="application-reason">{{ app.reason }}</p>
              <div class="application-meta">
                <span class="meta-item">
                  <el-icon><Phone /></el-icon>
                  {{ app.contactPhone }}
                </span>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(app.createTime) }}
                </span>
              </div>
            </div>
            <div class="card-side">
              <div class="status-group">
                <span class="status-badge" :class="'status-' + app.status">
                  {{ getStatusText(app.status) }}
                </span>
                <div v-if="app.status === 0" class="sub-status">
                  <span :class="getStatusClass(app.adminStatus)">管理员: {{ getAuditText(app.adminStatus) }}</span>
                  <span :class="getStatusClass(app.publisherStatus)">送养人: {{ getAuditText(app.publisherStatus) }}</span>
                </div>
              </div>
              <div class="card-actions">
                <button class="btn-action btn-view" @click="showDetail(app)">
                  <el-icon><View /></el-icon>
                  详情
                </button>
                <button 
                  v-if="app.status === 0" 
                  class="btn-action btn-cancel" 
                  @click="handleCancel(app)"
                >
                  <el-icon><Close /></el-icon>
                  取消
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!loading && !applications.length" class="empty-state">
          <el-icon class="empty-icon"><Document /></el-icon>
          <p class="empty-title">暂无申请记录</p>
          <p class="empty-text">浏览可爱的宠物，提交您的第一份领养申请吧</p>
          <router-link to="/pets" class="btn-explore">
            浏览宠物
            <el-icon><ArrowRight /></el-icon>
          </router-link>
        </div>
      </div>

      <div v-if="total > pageSize" class="pagination-container">
        <button 
          class="pagination-btn" 
          :disabled="pageNum <= 1"
          @click="changePage(pageNum - 1)"
        >
          <el-icon><ArrowLeft /></el-icon>
          上一页
        </button>
        <span class="pagination-info">第 {{ pageNum }} / {{ Math.ceil(total / pageSize) }} 页</span>
        <button 
          class="pagination-btn" 
          :disabled="pageNum >= Math.ceil(total / pageSize)"
          @click="changePage(pageNum + 1)"
        >
          下一页
          <el-icon><ArrowRight /></el-icon>
        </button>
      </div>
    </div>

    <!-- Detail Modal -->
    <el-dialog 
      v-model="detailVisible" 
      title="" 
      width="560px" 
      class="detail-dialog"
      :show-close="false"
    >
      <template #header>
        <div class="dialog-header">
          <h3 class="dialog-title">申请详情</h3>
          <button class="dialog-close" @click="detailVisible = false">
            <el-icon><Close /></el-icon>
          </button>
        </div>
      </template>
      
      <div v-if="currentApp" class="detail-content">
        <div class="detail-pet">
          <img :src="currentApp.petImageUrl || 'https://placehold.co/600x400/e8c547/1a1a2e?text=Pet+Image'" :alt="currentApp.petName" class="detail-pet-image" />
          <div class="detail-pet-info">
            <h4 class="detail-pet-name">{{ currentApp.petName }}</h4>
            <span class="status-badge" :class="'status-' + currentApp.status">
              {{ getStatusText(currentApp.status) }}
            </span>
          </div>
        </div>

        <div class="detail-section">
          <h5 class="section-title">联系信息</h5>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">联系电话</span>
              <span class="detail-value">{{ currentApp.contactPhone }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">联系地址</span>
              <span class="detail-value">{{ currentApp.contactAddress || '未填写' }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h5 class="section-title">领养条件</h5>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">居住条件</span>
              <span class="detail-value">{{ currentApp.livingCondition || '未填写' }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">养宠经验</span>
              <span class="detail-value">{{ currentApp.experience || '未填写' }}</span>
            </div>
          </div>
        </div>

        <div class="detail-section">
          <h5 class="section-title">领养理由</h5>
          <p class="detail-reason">{{ currentApp.reason }}</p>
        </div>

        <div v-if="currentApp.status === 2 && currentApp.rejectReason" class="detail-section reject-section">
          <h5 class="section-title">拒绝原因</h5>
          <p class="detail-reason">{{ currentApp.rejectReason }}</p>
        </div>

        <div class="detail-footer">
          <span class="detail-time">
            申请时间：{{ formatDate(currentApp.createTime) }}
          </span>
          <span v-if="currentApp.reviewTime" class="detail-time">
            审核时间：{{ formatDate(currentApp.reviewTime) }}
          </span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyApplications, cancelApplication } from '@/api/application'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Phone, Clock, View, Close, Document, ArrowRight, ArrowLeft } from '@element-plus/icons-vue'

const applications = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentApp = ref(null)

onMounted(() => {
  loadApplications()
})

async function loadApplications() {
  loading.value = true
  try {
    const res = await getMyApplications({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (res.code === 200) {
      applications.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function changePage(page) {
  pageNum.value = page
  loadApplications()
}

function getStatusText(status) {
  const map = { 0: '审核中', 1: '已通过', 2: '已驳回' }
  return map[status] || '未知'
}

function getAuditText(status) {
  const map = { 0: '待审核', 1: '已通过', 2: '已驳回' }
  return map[status] || '待审核'
}

function getStatusClass(status) {
  const map = { 0: 'text-warning', 1: 'text-success', 2: 'text-danger' }
  return map[status] || 'text-gray'
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function showDetail(row) {
  currentApp.value = row
  detailVisible.value = true
}

async function handleCancel(row) {
  await ElMessageBox.confirm('确定要取消该领养申请吗？', '确认取消', { 
    type: 'warning',
    confirmButtonText: '确认',
    cancelButtonText: '返回'
  })
  const res = await cancelApplication(row.id)
  if (res.code === 200) {
    ElMessage.success('已取消申请')
    loadApplications()
  } else {
    ElMessage.error(res.message)
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.applications-page {
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
  max-width: $container-max-width;
  margin: 0 auto;
  padding: $spacing-12 $spacing-6;
}

.applications-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-6;
}

.application-card {
  display: flex;
  background: $color-white;
  border-radius: $radius-xl;
  overflow: hidden;
  transition: all $transition-base;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: $shadow-lg;
  }
  
  @media (max-width: $breakpoint-md) {
    flex-direction: column;
  }
}

.card-image {
  width: 160px;
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  @media (max-width: $breakpoint-md) {
    width: 100%;
    height: 200px;
  }
}

.card-body {
  flex: 1;
  padding: $spacing-6;
  display: flex;
  justify-content: space-between;
  gap: $spacing-6;
  
  @media (max-width: $breakpoint-md) {
    flex-direction: column;
  }
}

.card-main {
  flex: 1;
}

.pet-name {
  font-size: $font-size-xl;
  font-weight: $font-weight-semibold;
  color: $color-primary;
  margin-bottom: $spacing-2;
}

.application-reason {
  font-size: $font-size-sm;
  color: $color-gray-600;
  line-height: $line-height-relaxed;
  margin-bottom: $spacing-4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.application-meta {
  display: flex;
  gap: $spacing-6;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  font-size: $font-size-sm;
  color: $color-gray-500;
  
  .el-icon {
    font-size: 14px;
  }
}

.card-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  
  @media (max-width: $breakpoint-md) {
    flex-direction: row;
    align-items: center;
  }
}

.status-badge {
  display: inline-block;
  padding: $spacing-1 $spacing-3;
  border-radius: $radius-full;
  font-size: $font-size-xs;
  font-weight: $font-weight-bold;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  
  &.status-0 {
    background: rgba(#f59e0b, 0.1);
    color: #f59e0b;
  }
  
  &.status-1 {
    background: rgba($color-success, 0.1);
    color: $color-success;
  }
  
  &.status-2 {
    background: rgba($color-error, 0.1);
    color: $color-error;
  }
}

.status-group {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: $spacing-2;
}

.sub-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  font-size: $font-size-xs;
  gap: 2px;
  
  .text-warning { color: #f59e0b; }
  .text-success { color: $color-success; }
  .text-danger { color: $color-error; }
  .text-gray { color: $color-gray-400; }
}

.card-actions {
  display: flex;
  gap: $spacing-3;
}

.btn-action {
  display: inline-flex;
  align-items: center;
  gap: $spacing-1;
  padding: $spacing-2 $spacing-3;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  border: none;
  border-radius: $radius-md;
  cursor: pointer;
  transition: all $transition-fast;
  
  &.btn-view {
    background: rgba($color-primary, 0.1);
    color: $color-primary;
    
    &:hover {
      background: $color-primary;
      color: $color-white;
    }
  }
  
  &.btn-cancel {
    background: rgba($color-error, 0.1);
    color: $color-error;
    
    &:hover {
      background: $color-error;
      color: $color-white;
    }
  }
}

.empty-state {
  text-align: center;
  padding: $spacing-16 0;
}

.empty-icon {
  font-size: 64px;
  color: $color-gray-300;
  margin-bottom: $spacing-4;
}

.empty-title {
  font-size: $font-size-xl;
  font-weight: $font-weight-semibold;
  color: $color-gray-600;
  margin-bottom: $spacing-2;
}

.empty-text {
  font-size: $font-size-base;
  color: $color-gray-400;
  margin-bottom: $spacing-6;
}

.btn-explore {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-3 $spacing-6;
  background: $color-primary;
  color: $color-white;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  text-decoration: none;
  border-radius: $radius-lg;
  transition: all $transition-fast;
  
  &:hover {
    background: $color-primary-light;
    transform: translateY(-1px);
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: $spacing-6;
  margin-top: $spacing-12;
}

.pagination-btn {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-3 $spacing-5;
  background: $color-white;
  color: $color-gray-700;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  border: 1px solid $color-gray-200;
  border-radius: $radius-lg;
  cursor: pointer;
  transition: all $transition-fast;
  
  &:hover:not(:disabled) {
    background: $color-primary;
    color: $color-white;
    border-color: $color-primary;
  }
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.pagination-info {
  font-size: $font-size-sm;
  color: $color-gray-500;
}

// Dialog Styles
.detail-dialog {
  :deep(.el-dialog) {
    border-radius: $radius-xl;
    overflow: hidden;
  }
  
  :deep(.el-dialog__header) {
    padding: 0;
    margin: 0;
  }
  
  :deep(.el-dialog__body) {
    padding: 0;
  }
}

.dialog-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: $spacing-5 $spacing-6;
  border-bottom: 1px solid $color-gray-100;
}

.dialog-title {
  font-size: $font-size-lg;
  font-weight: $font-weight-semibold;
  color: $color-primary;
}

.dialog-close {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: none;
  border: none;
  color: $color-gray-400;
  border-radius: $radius-full;
  cursor: pointer;
  transition: all $transition-fast;
  
  &:hover {
    background: $color-gray-100;
    color: $color-gray-600;
  }
}

.detail-content {
  padding: $spacing-6;
}

.detail-pet {
  display: flex;
  gap: $spacing-4;
  padding-bottom: $spacing-5;
  border-bottom: 1px solid $color-gray-100;
  margin-bottom: $spacing-5;
}

.detail-pet-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: $radius-lg;
}

.detail-pet-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: $spacing-2;
}

.detail-pet-name {
  font-size: $font-size-xl;
  font-weight: $font-weight-semibold;
  color: $color-primary;
}

.detail-section {
  margin-bottom: $spacing-5;
  
  &.reject-section {
    background: rgba($color-error, 0.05);
    padding: $spacing-4;
    border-radius: $radius-lg;
    border-left: 3px solid $color-error;
  }
}

.section-title {
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
  color: $color-gray-700;
  margin-bottom: $spacing-3;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-4;
  
  @media (max-width: $breakpoint-sm) {
    grid-template-columns: 1fr;
  }
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: $spacing-1;
}

.detail-label {
  font-size: $font-size-xs;
  color: $color-gray-400;
}

.detail-value {
  font-size: $font-size-sm;
  color: $color-gray-700;
}

.detail-reason {
  font-size: $font-size-sm;
  color: $color-gray-600;
  line-height: $line-height-relaxed;
  white-space: pre-line;
}

.detail-footer {
  display: flex;
  justify-content: space-between;
  padding-top: $spacing-5;
  border-top: 1px solid $color-gray-100;
}

.detail-time {
  font-size: $font-size-xs;
  color: $color-gray-400;
}
</style>
