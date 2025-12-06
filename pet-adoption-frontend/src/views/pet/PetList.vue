<template>
  <div class="pet-list">
    <!-- Page Header -->
    <div class="page-header">
      <div class="page-header-content">
        <h1 class="page-title">领养宠物</h1>
        <p class="page-description">找到与您心灵相通的伙伴，开启一段美好的旅程</p>
      </div>
    </div>

    <!-- Filter Section -->
    <div class="filter-section">
      <div class="filter-group">
        <label class="filter-label">分类筛选</label>
        <div class="filter-chips">
          <button 
            class="filter-chip"
            :class="{ active: !filter.categoryId }"
            @click="filter.categoryId = null; loadPets()"
          >
            <el-icon><Grid /></el-icon>
            全部
          </button>
          <button 
            v-for="cat in categories" 
            :key="cat.id"
            class="filter-chip"
            :class="{ active: filter.categoryId === cat.id }"
            @click="filter.categoryId = cat.id; loadPets()"
          >
            {{ cat.name }}
          </button>
        </div>
      </div>
      
      <div class="search-group">
        <div class="search-input-wrapper">
          <el-icon class="search-icon"><Search /></el-icon>
          <input 
            v-model="filter.keyword"
            type="text"
            class="search-input"
            placeholder="搜索名字或品种..."
            @keyup.enter="loadPets"
          />
          <button v-if="filter.keyword" class="search-clear" @click="filter.keyword = ''; loadPets()">
            <el-icon><Close /></el-icon>
          </button>
        </div>
        <button class="btn-search" @click="loadPets">
          搜索
        </button>
      </div>
    </div>

    <!-- Results Info -->
    <div class="results-info" v-if="!loading">
      <span class="results-count">共找到 <strong>{{ total }}</strong> 只待领养宠物</span>
    </div>

    <!-- Pet Grid -->
    <div class="pets-grid" v-loading="loading">
      <article 
        v-for="(pet, index) in pets" 
        :key="pet.id"
        class="pet-card"
        :style="{ animationDelay: `${index * 50}ms` }"
        @click="$router.push(`/pets/${pet.id}`)"
      >
        <div class="pet-image-wrapper">
          <img 
            :src="getImageUrl(pet.imageUrl)" 
            :alt="pet.name"
            class="pet-image"
          />
          <div class="pet-overlay">
            <span class="pet-view-btn">
              <el-icon><View /></el-icon>
              查看详情
            </span>
          </div>
          <div class="pet-badge" :class="getGenderClass(pet.gender)">
            <el-icon>
              <Male v-if="pet.gender === 1" />
              <Female v-else />
            </el-icon>
          </div>
          <span class="pet-status-badge" :class="getStatusClass(pet.status)">
            {{ getStatusText(pet.status) }}
          </span>
        </div>
        
        <div class="pet-content">
          <div class="pet-header">
            <h3 class="pet-name">{{ pet.name }}</h3>
            <span class="pet-category">{{ pet.categoryName }}</span>
          </div>
          <p class="pet-breed">{{ pet.breed }}</p>
          <div class="pet-meta">
            <span class="pet-meta-item">
              <el-icon><Calendar /></el-icon>
              {{ pet.age }}
            </span>
            <span class="pet-meta-item">
              <el-icon><User /></el-icon>
              {{ getGenderText(pet.gender) }}
            </span>
          </div>
        </div>
      </article>
    </div>

    <!-- Empty State -->
    <div v-if="!loading && !pets.length" class="empty-state">
      <div class="empty-content">
        <el-icon class="empty-icon"><Box /></el-icon>
        <h3 class="empty-title">暂无符合条件的宠物</h3>
        <p class="empty-text">尝试调整筛选条件或稍后再来查看</p>
        <button class="btn-reset" @click="resetFilter">
          <el-icon><RefreshRight /></el-icon>
          重置筛选
        </button>
      </div>
    </div>
    
    <!-- Pagination -->
    <div class="pagination-wrapper" v-if="total > filter.pageSize">
      <div class="pagination-info">
        显示 {{ (filter.pageNum - 1) * filter.pageSize + 1 }}-{{ Math.min(filter.pageNum * filter.pageSize, total) }} 条，共 {{ total }} 条
      </div>
      <div class="pagination-controls">
        <button 
          class="pagination-btn"
          :disabled="filter.pageNum === 1"
          @click="changePage(filter.pageNum - 1)"
        >
          <el-icon><ArrowLeft /></el-icon>
          上一页
        </button>
        <div class="pagination-pages">
          <button 
            v-for="page in visiblePages" 
            :key="page"
            class="pagination-page"
            :class="{ active: page === filter.pageNum, ellipsis: page === '...' }"
            :disabled="page === '...'"
            @click="page !== '...' && changePage(page)"
          >
            {{ page }}
          </button>
        </div>
        <button 
          class="pagination-btn"
          :disabled="filter.pageNum === totalPages"
          @click="changePage(filter.pageNum + 1)"
        >
          下一页
          <el-icon><ArrowRight /></el-icon>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getPetList } from '@/api/pet'
import { getCategories } from '@/api/category'
import { 
  Search, Close, Grid, Calendar, User, View, Box, 
  RefreshRight, ArrowLeft, ArrowRight, Male, Female 
} from '@element-plus/icons-vue'

const pets = ref([])
const categories = ref([])
const total = ref(0)
const loading = ref(false)

const filter = reactive({
  pageNum: 1,
  pageSize: 12,
  categoryId: null,
  keyword: '',
  status: 0
})

const totalPages = computed(() => Math.ceil(total.value / filter.pageSize))

const visiblePages = computed(() => {
  const pages = []
  const current = filter.pageNum
  const total = totalPages.value
  
  if (total <= 7) {
    for (let i = 1; i <= total; i++) pages.push(i)
  } else {
    if (current <= 4) {
      for (let i = 1; i <= 5; i++) pages.push(i)
      pages.push('...')
      pages.push(total)
    } else if (current >= total - 3) {
      pages.push(1)
      pages.push('...')
      for (let i = total - 4; i <= total; i++) pages.push(i)
    } else {
      pages.push(1)
      pages.push('...')
      for (let i = current - 1; i <= current + 1; i++) pages.push(i)
      pages.push('...')
      pages.push(total)
    }
  }
  
  return pages
})

onMounted(async () => {
  const catRes = await getCategories()
  if (catRes.code === 200) {
    categories.value = catRes.data
  }
  loadPets()
})

async function loadPets() {
  loading.value = true
  try {
    const res = await getPetList(filter)
    if (res.code === 200) {
      pets.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function changePage(page) {
  filter.pageNum = page
  loadPets()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function resetFilter() {
  filter.categoryId = null
  filter.keyword = ''
  filter.pageNum = 1
  loadPets()
}

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
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.pet-list {
  animation: fadeIn 0.5s ease;
}

// Page Header
.page-header {
  text-align: center;
  margin-bottom: $spacing-12;
}

.page-title {
  font-size: $font-size-4xl;
  font-weight: $font-weight-bold;
  color: $color-primary;
  margin-bottom: $spacing-3;
}

.page-description {
  font-size: $font-size-lg;
  color: $color-gray-500;
}

// Filter Section
.filter-section {
  background: $color-white;
  border-radius: $radius-xl;
  padding: $spacing-8;
  margin-bottom: $spacing-8;
  box-shadow: $shadow-sm;
}

.filter-group {
  margin-bottom: $spacing-6;
}

.filter-label {
  display: block;
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
  color: $color-gray-700;
  text-transform: uppercase;
  letter-spacing: $letter-spacing-wider;
  margin-bottom: $spacing-4;
}

.filter-chips {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-3;
}

.filter-chip {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-2 $spacing-4;
  background: $color-gray-100;
  border: 1px solid transparent;
  border-radius: $radius-full;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  color: $color-gray-600;
  cursor: pointer;
  transition: all $transition-fast;
  
  &:hover {
    background: $color-gray-200;
  }
  
  &.active {
    background: $color-primary;
    color: $color-white;
  }
}

.search-group {
  display: flex;
  gap: $spacing-4;
  
  @media (max-width: $breakpoint-sm) {
    flex-direction: column;
  }
}

.search-input-wrapper {
  flex: 1;
  position: relative;
}

.search-icon {
  position: absolute;
  left: $spacing-4;
  top: 50%;
  transform: translateY(-50%);
  color: $color-gray-400;
  font-size: 18px;
}

.search-input {
  width: 100%;
  padding: $spacing-3 $spacing-12;
  border: 1px solid $color-gray-300;
  border-radius: $radius-lg;
  font-size: $font-size-base;
  transition: all $transition-fast;
  
  &:focus {
    outline: none;
    border-color: $color-primary;
    box-shadow: 0 0 0 3px rgba($color-primary, 0.1);
  }
  
  &::placeholder {
    color: $color-gray-400;
  }
}

.search-clear {
  position: absolute;
  right: $spacing-4;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: $color-gray-400;
  cursor: pointer;
  padding: $spacing-1;
  
  &:hover {
    color: $color-gray-600;
  }
}

.btn-search {
  padding: $spacing-3 $spacing-6;
  background: $color-primary;
  color: $color-white;
  border: none;
  border-radius: $radius-lg;
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
  cursor: pointer;
  transition: all $transition-fast;
  white-space: nowrap;
  
  &:hover {
    background: $color-primary-light;
  }
}

// Results Info
.results-info {
  margin-bottom: $spacing-6;
}

.results-count {
  font-size: $font-size-sm;
  color: $color-gray-500;
  
  strong {
    color: $color-primary;
    font-weight: $font-weight-semibold;
  }
}

// Pet Grid
.pets-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-6;
  min-height: 200px;
  
  @media (max-width: $breakpoint-xl) {
    grid-template-columns: repeat(3, 1fr);
  }
  
  @media (max-width: $breakpoint-lg) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (max-width: $breakpoint-sm) {
    grid-template-columns: 1fr;
  }
}

.pet-card {
  background: $color-white;
  border-radius: $radius-lg;
  overflow: hidden;
  cursor: pointer;
  transition: all $transition-base;
  animation: slideUp 0.5s ease forwards;
  opacity: 0;
  
  &:hover {
    transform: translateY(-6px);
    box-shadow: $shadow-xl;
    
    .pet-image {
      transform: scale(1.08);
    }
    
    .pet-overlay {
      opacity: 1;
    }
  }
}

.pet-image-wrapper {
  position: relative;
  height: 220px;
  overflow: hidden;
}

.pet-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform $transition-slow;
}

.pet-overlay {
  position: absolute;
  inset: 0;
  background: rgba($color-primary, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity $transition-base;
}

.pet-view-btn {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-3 $spacing-5;
  background: $color-white;
  color: $color-primary;
  font-size: $font-size-sm;
  font-weight: $font-weight-semibold;
  border-radius: $radius-full;
}

.pet-badge {
  position: absolute;
  top: $spacing-3;
  right: $spacing-3;
  width: 32px;
  height: 32px;
  border-radius: $radius-full;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $color-white;
  font-size: 14px;
  
  &.male {
    background: $color-info;
  }
  
  &.female {
    background: #d4a5a5;
  }
}

.pet-status-badge {
  position: absolute;
  top: $spacing-3;
  left: $spacing-3;
  padding: $spacing-1 $spacing-3;
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  border-radius: $radius-full;
  text-transform: uppercase;
  letter-spacing: $letter-spacing-wide;
  
  &.available {
    background: rgba($color-success, 0.9);
    color: $color-white;
  }
  
  &.pending {
    background: rgba($color-warning, 0.9);
    color: $color-white;
  }
  
  &.adopted {
    background: rgba($color-gray-600, 0.9);
    color: $color-white;
  }
}

.pet-content {
  padding: $spacing-5;
}

.pet-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: $spacing-2;
}

.pet-name {
  font-size: $font-size-lg;
  font-weight: $font-weight-semibold;
  color: $color-primary;
}

.pet-category {
  font-size: $font-size-xs;
  color: $color-gray-500;
  background: $color-gray-100;
  padding: $spacing-1 $spacing-2;
  border-radius: $radius-sm;
}

.pet-breed {
  font-size: $font-size-sm;
  color: $color-gray-500;
  margin-bottom: $spacing-4;
}

.pet-meta {
  display: flex;
  gap: $spacing-4;
}

.pet-meta-item {
  display: inline-flex;
  align-items: center;
  gap: $spacing-1;
  font-size: $font-size-xs;
  color: $color-gray-500;
  
  .el-icon {
    font-size: 12px;
  }
}

// Empty State
.empty-state {
  padding: $spacing-20 0;
}

.empty-content {
  text-align: center;
}

.empty-icon {
  font-size: 64px;
  color: $color-gray-300;
  margin-bottom: $spacing-6;
}

.empty-title {
  font-size: $font-size-xl;
  font-weight: $font-weight-semibold;
  color: $color-gray-700;
  margin-bottom: $spacing-2;
}

.empty-text {
  font-size: $font-size-base;
  color: $color-gray-500;
  margin-bottom: $spacing-8;
}

.btn-reset {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-3 $spacing-6;
  background: $color-gray-100;
  color: $color-gray-700;
  border: none;
  border-radius: $radius-md;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  cursor: pointer;
  transition: all $transition-fast;
  
  &:hover {
    background: $color-gray-200;
  }
}

// Pagination
.pagination-wrapper {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: $spacing-12;
  padding-top: $spacing-8;
  border-top: 1px solid $color-gray-200;
  
  @media (max-width: $breakpoint-md) {
    flex-direction: column;
    gap: $spacing-4;
  }
}

.pagination-info {
  font-size: $font-size-sm;
  color: $color-gray-500;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: $spacing-2;
}

.pagination-btn {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-2 $spacing-4;
  background: $color-white;
  border: 1px solid $color-gray-300;
  border-radius: $radius-md;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  color: $color-gray-700;
  cursor: pointer;
  transition: all $transition-fast;
  
  &:hover:not(:disabled) {
    border-color: $color-primary;
    color: $color-primary;
  }
  
  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.pagination-pages {
  display: flex;
  gap: $spacing-1;
}

.pagination-page {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $color-white;
  border: 1px solid $color-gray-300;
  border-radius: $radius-md;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  color: $color-gray-700;
  cursor: pointer;
  transition: all $transition-fast;
  
  &:hover:not(:disabled):not(.ellipsis) {
    border-color: $color-primary;
    color: $color-primary;
  }
  
  &.active {
    background: $color-primary;
    border-color: $color-primary;
    color: $color-white;
  }
  
  &.ellipsis {
    border: none;
    cursor: default;
  }
}

// Animations
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
