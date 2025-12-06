<template>
  <div class="announcement-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">公告中心</h1>
        <p class="page-subtitle">了解最新动态和领养须知</p>
      </div>
    </div>

    <div class="page-container">
      <div class="announcements-list">
        <article 
          v-for="item in announcements" 
          :key="item.id" 
          class="announcement-item"
          :class="{ 'is-pinned': item.isTop }"
          @click="showDetail(item)"
        >
          <div class="item-content">
            <div class="item-header">
              <div class="item-badges">
                <span class="badge" :class="'badge-' + getTypeClass(item.type)">
                  {{ getTypeText(item.type) }}
                </span>
                <span v-if="item.isTop" class="badge badge-pinned">
                  <el-icon><Top /></el-icon>
                  置顶
                </span>
              </div>
              <h3 class="item-title">{{ item.title }}</h3>
            </div>
            <p class="item-summary">{{ item.content }}</p>
            <div class="item-footer">
              <time class="item-date">{{ formatDate(item.createTime) }}</time>
              <span class="item-more">查看详情 <el-icon><ArrowRight /></el-icon></span>
            </div>
          </div>
        </article>

        <div v-if="!loading && !announcements.length" class="empty-state">
          <el-icon class="empty-icon"><Document /></el-icon>
          <p class="empty-text">暂无公告</p>
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

    <!-- Detail Dialog -->
    <el-dialog v-model="detailVisible" title="公告详情" width="600px" class="announcement-dialog">
      <div v-if="currentAnnouncement" class="detail-content">
        <div class="detail-header">
          <h2 class="detail-title">{{ currentAnnouncement.title }}</h2>
          <div class="detail-meta">
            <span class="badge" :class="'badge-' + getTypeClass(currentAnnouncement.type)">
              {{ getTypeText(currentAnnouncement.type) }}
            </span>
            <time>{{ formatDate(currentAnnouncement.createTime) }}</time>
          </div>
        </div>
        <div class="detail-body">
          {{ currentAnnouncement.content }}
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAnnouncements } from '@/api/announcement'
import { Document, Top, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'

const announcements = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const detailVisible = ref(false)
const currentAnnouncement = ref(null)

onMounted(() => {
  loadAnnouncements()
})

async function loadAnnouncements() {
  loading.value = true
  try {
    const res = await getAnnouncements({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (res.code === 200) {
      announcements.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function changePage(page) {
  pageNum.value = page
  loadAnnouncements()
}

function showDetail(item) {
  currentAnnouncement.value = item
  detailVisible.value = true
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

function getTypeText(type) {
  const map = { 0: '公告', 1: '领养须知', 2: '活动' }
  return map[type] || '公告'
}

function getTypeClass(type) {
  const map = { 0: 'info', 1: 'warning', 2: 'success' }
  return map[type] || 'info'
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.announcement-page {
  min-height: 100vh;
  background: $color-cream;
}

.page-header {
  background: $color-primary;
  padding: $spacing-16 0;
}

.header-content {
  max-width: 800px;
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

.announcements-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-4;
}

.announcement-item {
  background: $color-white;
  border-radius: $radius-xl;
  padding: $spacing-6;
  transition: all $transition-base;
  cursor: pointer;
  border: 1px solid transparent;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: $shadow-lg;
    border-color: rgba($color-primary, 0.1);
    
    .item-title {
      color: $color-accent;
    }
    
    .item-more {
      opacity: 1;
      transform: translateX(0);
    }
  }
  
  &.is-pinned {
    background: linear-gradient(to right, rgba($color-accent, 0.05), $color-white);
    border-left: 3px solid $color-accent;
  }
}

.item-header {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  margin-bottom: $spacing-3;
  flex-wrap: wrap;
}

.item-badges {
  display: flex;
  gap: $spacing-2;
}

.badge {
  display: inline-flex;
  align-items: center;
  gap: $spacing-1;
  padding: $spacing-1 $spacing-3;
  font-size: $font-size-xs;
  font-weight: $font-weight-medium;
  border-radius: $radius-full;
  
  &-info {
    background: rgba($color-primary, 0.1);
    color: $color-primary;
  }
  
  &-warning {
    background: rgba(#f59e0b, 0.1);
    color: #f59e0b;
  }
  
  &-success {
    background: rgba($color-success, 0.1);
    color: $color-success;
  }
  
  &-pinned {
    background: rgba($color-accent, 0.15);
    color: darken($color-accent, 10%);
    
    .el-icon {
      font-size: 10px;
    }
  }
}

.item-title {
  font-size: $font-size-lg;
  font-weight: $font-weight-semibold;
  color: $color-primary;
  transition: color $transition-fast;
  margin: 0;
}

.item-summary {
  font-size: $font-size-sm;
  color: $color-gray-600;
  line-height: $line-height-relaxed;
  margin-bottom: $spacing-4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-date {
  font-size: $font-size-xs;
  color: $color-gray-400;
}

.item-more {
  display: inline-flex;
  align-items: center;
  gap: $spacing-1;
  font-size: $font-size-sm;
  color: $color-primary;
  font-weight: $font-weight-medium;
  opacity: 0;
  transform: translateX(-10px);
  transition: all $transition-base;
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

.empty-text {
  font-size: $font-size-lg;
  color: $color-gray-400;
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

// Dialog
.announcement-dialog {
  :deep(.el-dialog) {
    border-radius: $radius-xl;
  }
}

.detail-header {
  margin-bottom: $spacing-6;
  padding-bottom: $spacing-4;
  border-bottom: 1px solid $color-gray-100;
}

.detail-title {
  font-size: $font-size-2xl;
  font-weight: $font-weight-bold;
  color: $color-primary;
  margin-bottom: $spacing-3;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: $spacing-4;
  color: $color-gray-500;
  font-size: $font-size-sm;
}

.detail-body {
  font-size: $font-size-base;
  color: $color-gray-700;
  line-height: $line-height-relaxed;
  white-space: pre-line;
}
</style>
