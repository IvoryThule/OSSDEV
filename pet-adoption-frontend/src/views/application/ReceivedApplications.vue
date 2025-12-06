<template>
  <div class="applications-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">收到的申请</h1>
        <p class="page-subtitle">管理您发布的宠物的领养申请</p>
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
              <div class="applicant-info">
                <span class="label">申请人:</span>
                <span class="value">{{ app.applicantName }}</span>
              </div>
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
                <template v-if="app.status === 0 && app.publisherStatus === 0">
                  <button class="btn-action btn-approve" @click="handleReview(app, 1)">
                    <el-icon><Check /></el-icon>
                    通过
                  </button>
                  <button class="btn-action btn-reject" @click="handleReview(app, 2)">
                    <el-icon><Close /></el-icon>
                    拒绝
                  </button>
                </template>
              </div>
            </div>
          </div>
        </div>

        <div v-if="!loading && !applications.length" class="empty-state">
          <el-icon class="empty-icon"><Document /></el-icon>
          <p class="empty-title">暂无申请记录</p>
        </div>
      </div>

      <div v-if="total > pageSize" class="pagination-container">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="pageNum"
          @current-change="loadApplications"
        />
      </div>
    </div>

    <!-- Detail Dialog -->
    <el-dialog v-model="detailVisible" title="申请详情" width="600px">
      <div v-if="currentApp" class="detail-content">
        <div class="detail-section">
          <h4>申请信息</h4>
          <p><strong>申请人：</strong>{{ currentApp.applicantName }}</p>
          <p><strong>联系电话：</strong>{{ currentApp.contactPhone }}</p>
          <p><strong>申请理由：</strong>{{ currentApp.reason }}</p>
          <p><strong>申请时间：</strong>{{ formatDate(currentApp.createTime) }}</p>
        </div>
        <div class="detail-section">
          <h4>宠物信息</h4>
          <p><strong>宠物名称：</strong>{{ currentApp.petName }}</p>
        </div>
        <div class="detail-section">
          <h4>审核状态</h4>
          <p><strong>管理员审核：</strong>{{ getAuditText(currentApp.adminStatus) }}</p>
          <p><strong>送养人审核：</strong>{{ getAuditText(currentApp.publisherStatus) }}</p>
          <p><strong>最终状态：</strong>{{ getStatusText(currentApp.status) }}</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
          <template v-if="currentApp && currentApp.status === 0 && currentApp.publisherStatus === 0">
            <el-button type="danger" @click="handleReview(currentApp, 2)">拒绝</el-button>
            <el-button type="success" @click="handleReview(currentApp, 1)">通过</el-button>
          </template>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getReceivedApplications, publisherReview } from '@/api/application'
import { Phone, Clock, View, Check, Close, Document, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

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
    const res = await getReceivedApplications({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (res.code === 200) {
      applications.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function showDetail(app) {
  currentApp.value = app
  detailVisible.value = true
}

function handleReview(app, status) {
  if (status === 2) {
    ElMessageBox.prompt('请输入拒绝理由', '拒绝申请', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    }).then(async ({ value }) => {
      await submitReview(app.id, status, value)
    })
  } else {
    ElMessageBox.confirm('确定通过该申请吗？', '提示', {
      type: 'warning'
    }).then(async () => {
      await submitReview(app.id, status)
    })
  }
}

async function submitReview(id, status, reason = null) {
  try {
    const res = await publisherReview(id, status, reason)
    if (res.code === 200) {
      ElMessage.success('操作成功')
      detailVisible.value = false
      loadApplications()
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
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
  max-width: 800px;
  margin: 0 auto;
  padding: $spacing-12 $spacing-6;
}

.applications-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-6;
}

.application-card {
  background: $color-white;
  border-radius: $radius-xl;
  overflow: hidden;
  display: flex;
  box-shadow: $shadow-sm;
  transition: all $transition-base;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: $shadow-lg;
  }
  
  @media (max-width: $breakpoint-sm) {
    flex-direction: column;
  }
}

.card-image {
  width: 200px;
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  @media (max-width: $breakpoint-sm) {
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
  
  @media (max-width: $breakpoint-sm) {
    flex-direction: column;
  }
}

.card-main {
  flex: 1;
}

.pet-name {
  font-size: $font-size-xl;
  font-weight: $font-weight-bold;
  color: $color-gray-900;
  margin-bottom: $spacing-2;
}

.applicant-info {
  margin-bottom: $spacing-2;
  font-size: $font-size-sm;
  
  .label {
    color: $color-gray-500;
    margin-right: $spacing-2;
  }
  
  .value {
    color: $color-gray-900;
    font-weight: $font-weight-medium;
  }
}

.application-reason {
  font-size: $font-size-base;
  color: $color-gray-600;
  margin-bottom: $spacing-4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.application-meta {
  display: flex;
  gap: $spacing-4;
  color: $color-gray-500;
  font-size: $font-size-sm;
  
  .meta-item {
    display: flex;
    align-items: center;
    gap: $spacing-1;
  }
}

.card-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-between;
  gap: $spacing-4;
  
  @media (max-width: $breakpoint-sm) {
    align-items: flex-start;
    flex-direction: row;
  }
}

.status-group {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: $spacing-2;
  
  @media (max-width: $breakpoint-sm) {
    align-items: flex-start;
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
  
  @media (max-width: $breakpoint-sm) {
    align-items: flex-start;
  }
}

.card-actions {
  display: flex;
  gap: $spacing-3;
}

.btn-action {
  display: inline-flex;
  align-items: center;
  gap: $spacing-1;
  padding: $spacing-2 $spacing-4;
  border-radius: $radius-lg;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  cursor: pointer;
  transition: all $transition-fast;
  border: 1px solid transparent;
  
  &.btn-view {
    background: $color-gray-100;
    color: $color-gray-700;
    
    &:hover {
      background: $color-gray-200;
    }
  }
  
  &.btn-approve {
    background: rgba($color-success, 0.1);
    color: $color-success;
    
    &:hover {
      background: rgba($color-success, 0.2);
    }
  }
  
  &.btn-reject {
    background: rgba($color-error, 0.1);
    color: $color-error;
    
    &:hover {
      background: rgba($color-error, 0.2);
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
  font-size: $font-size-lg;
  font-weight: $font-weight-bold;
  color: $color-gray-900;
  margin-bottom: $spacing-2;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: $spacing-12;
}

.detail-content {
  .detail-section {
    margin-bottom: $spacing-6;
    
    h4 {
      font-size: $font-size-lg;
      font-weight: $font-weight-bold;
      margin-bottom: $spacing-3;
      color: $color-primary;
      border-bottom: 1px solid $color-gray-100;
      padding-bottom: $spacing-2;
    }
    
    p {
      margin-bottom: $spacing-2;
      color: $color-gray-700;
      
      strong {
        color: $color-gray-900;
        margin-right: $spacing-2;
      }
    }
  }
}
</style>