<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <span class="hero-badge">
          <el-icon><Sunny /></el-icon>
          让爱延续
        </span>
        <h1 class="hero-title">给流浪的它们<br/>一个温暖的家</h1>
        <p class="hero-description">
          每一个生命都值得被温柔以待。在这里，您可以找到等待领养的可爱伙伴，<br/>
          用爱心开启一段美好的旅程。
        </p>
        <div class="hero-actions">
          <button class="btn-hero-primary" @click="$router.push('/pets')">
            <el-icon><Search /></el-icon>
            浏览待领养宠物
          </button>
          <button class="btn-hero-secondary" @click="scrollToSection('pets')">
            <el-icon><ArrowDown /></el-icon>
            了解更多
          </button>
        </div>
      </div>
      <div class="hero-visual">
        <div class="hero-card">
          <div class="hero-card-inner">
            <el-icon class="hero-card-icon"><HomeFilled /></el-icon>
            <span class="hero-card-number">{{ stats.adoptedCount || '100+' }}</span>
            <span class="hero-card-label">成功领养</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats Section -->
    <section class="stats-section">
      <div class="stats-grid">
        <div class="stat-item">
          <el-icon class="stat-icon"><House /></el-icon>
          <span class="stat-number">{{ pets.length }}</span>
          <span class="stat-label">待领养宠物</span>
        </div>
        <div class="stat-item">
          <el-icon class="stat-icon"><Bell /></el-icon>
          <span class="stat-number">{{ announcements.length }}</span>
          <span class="stat-label">最新公告</span>
        </div>
        <div class="stat-item">
          <el-icon class="stat-icon"><Star /></el-icon>
          <span class="stat-number">100%</span>
          <span class="stat-label">领养保障</span>
        </div>
      </div>
    </section>

    <!-- Announcements Section -->
    <section class="announcements-section" v-if="announcements.length">
      <div class="section-header">
        <div class="section-title-group">
          <el-icon class="section-icon"><Bell /></el-icon>
          <h2 class="section-title">最新公告</h2>
        </div>
        <button class="btn-text" @click="$router.push('/announcements')">
          查看全部
          <el-icon><ArrowRight /></el-icon>
        </button>
      </div>
      
      <div class="announcements-grid">
        <article 
          v-for="(item, index) in announcements" 
          :key="item.id"
          class="announcement-card"
          :style="{ animationDelay: `${index * 100}ms` }"
        >
          <div class="announcement-meta">
            <el-icon class="announcement-type-icon">
              <component :is="getAnnouncementIcon(item.type)" />
            </el-icon>
            <span class="announcement-date">{{ formatDate(item.createTime) }}</span>
            <span v-if="item.isTop" class="announcement-pin">
              <el-icon><Top /></el-icon>
              置顶
            </span>
          </div>
          <h3 class="announcement-title">{{ item.title }}</h3>
          <p class="announcement-excerpt">{{ truncateText(item.content, 100) }}</p>
        </article>
      </div>
    </section>

    <!-- Pets Section -->
    <section id="pets" class="pets-section">
      <div class="section-header">
        <div class="section-title-group">
          <el-icon class="section-icon"><House /></el-icon>
          <h2 class="section-title">待领养宠物</h2>
        </div>
        <button class="btn-text" @click="$router.push('/pets')">
          查看全部
          <el-icon><ArrowRight /></el-icon>
        </button>
      </div>
      
      <div class="pets-grid">
        <article 
          v-for="(pet, index) in pets" 
          :key="pet.id"
          class="pet-card"
          :style="{ animationDelay: `${index * 80}ms` }"
          @click="$router.push(`/pets/${pet.id}`)"
        >
          <div class="pet-image-wrapper">
            <img 
              :src="pet.imageUrl || 'https://placehold.co/600x400/e8c547/1a1a2e?text=Pet+Image'" 
              :alt="pet.name"
              class="pet-image"
            />
            <div class="pet-badge" :class="getGenderClass(pet.gender)">
              <el-icon>
                <Male v-if="pet.gender === 1" />
                <Female v-else />
              </el-icon>
            </div>
          </div>
          <div class="pet-content">
            <h3 class="pet-name">{{ pet.name }}</h3>
            <p class="pet-breed">{{ pet.categoryName }} · {{ pet.breed }}</p>
            <div class="pet-details">
              <span class="pet-detail">
                <el-icon><Calendar /></el-icon>
                {{ pet.age }}
              </span>
            </div>
            <div class="pet-footer">
              <span class="pet-status">待领养</span>
              <el-icon class="pet-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
        </article>
      </div>
      
      <div v-if="!pets.length" class="empty-state">
        <el-icon class="empty-icon"><Box /></el-icon>
        <p class="empty-text">暂无待领养宠物</p>
        <p class="empty-subtext">请稍后再来查看</p>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="cta-content">
        <h2 class="cta-title">准备好迎接新的家庭成员了吗？</h2>
        <p class="cta-description">每一次领养，都是一次爱的传递</p>
        <button class="btn-cta" @click="$router.push('/pets')">
          开始寻找
          <el-icon><ArrowRight /></el-icon>
        </button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getPetList } from '@/api/pet'
import { getAnnouncements } from '@/api/announcement'
import { 
  Sunny, Search, ArrowDown, ArrowRight, HomeFilled, House, 
  Bell, Star, Top, Calendar, Box, Male, Female, InfoFilled,
  Notification, Flag
} from '@element-plus/icons-vue'

const pets = ref([])
const announcements = ref([])
const stats = ref({
  adoptedCount: '100+'
})

onMounted(async () => {
  try {
    const petRes = await getPetList({ pageNum: 1, pageSize: 8, status: 0 })
    if (petRes.code === 200) {
      pets.value = petRes.data.records
    }
    
    const annRes = await getAnnouncements({ pageNum: 1, pageSize: 3 })
    if (annRes.code === 200) {
      announcements.value = annRes.data.records
    }
  } catch (error) {
    console.error('Failed to load data:', error)
  }
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

function truncateText(text, length) {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

function getGenderClass(gender) {
  return gender === 1 ? 'male' : 'female'
}

function getAnnouncementIcon(type) {
  const icons = { 0: InfoFilled, 1: Notification, 2: Flag }
  return icons[type] || InfoFilled
}

function scrollToSection(id) {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.home {
  animation: fadeIn 0.5s ease;
}

// Hero Section
.hero {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: $spacing-16;
  align-items: center;
  padding: $spacing-12 0 $spacing-20;
  
  @media (max-width: $breakpoint-lg) {
    grid-template-columns: 1fr;
    text-align: center;
  }
}

.hero-content {
  max-width: 600px;
  
  @media (max-width: $breakpoint-lg) {
    max-width: 100%;
  }
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-2 $spacing-4;
  background: rgba($color-accent, 0.1);
  color: $color-accent-muted;
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  text-transform: uppercase;
  letter-spacing: $letter-spacing-widest;
  border-radius: $radius-full;
  margin-bottom: $spacing-6;
}

.hero-title {
  font-size: clamp($font-size-3xl, 5vw, $font-size-5xl);
  font-weight: $font-weight-bold;
  line-height: 1.1;
  color: $color-primary;
  margin-bottom: $spacing-6;
  letter-spacing: $letter-spacing-tight;
}

.hero-description {
  font-size: $font-size-lg;
  color: $color-gray-600;
  line-height: $line-height-relaxed;
  margin-bottom: $spacing-10;
}

.hero-actions {
  display: flex;
  gap: $spacing-4;
  
  @media (max-width: $breakpoint-lg) {
    justify-content: center;
  }
  
  @media (max-width: $breakpoint-sm) {
    flex-direction: column;
  }
}

.btn-hero-primary {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-4 $spacing-8;
  background: $color-primary;
  color: $color-white;
  font-size: $font-size-base;
  font-weight: $font-weight-semibold;
  border: none;
  border-radius: $radius-md;
  cursor: pointer;
  transition: all $transition-base;
  
  &:hover {
    background: $color-primary-light;
    transform: translateY(-2px);
    box-shadow: $shadow-lg;
  }
}

.btn-hero-secondary {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-4 $spacing-6;
  background: transparent;
  color: $color-gray-600;
  font-size: $font-size-base;
  font-weight: $font-weight-medium;
  border: 1px solid $color-gray-300;
  border-radius: $radius-md;
  cursor: pointer;
  transition: all $transition-base;
  
  &:hover {
    border-color: $color-gray-400;
    background: $color-gray-100;
  }
}

.hero-visual {
  @media (max-width: $breakpoint-lg) {
    display: none;
  }
}

.hero-card {
  width: 200px;
  height: 200px;
  background: $color-white;
  border-radius: $radius-xl;
  box-shadow: $shadow-xl;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: float 6s ease-in-out infinite;
}

.hero-card-inner {
  text-align: center;
}

.hero-card-icon {
  font-size: 40px;
  color: $color-accent;
  margin-bottom: $spacing-3;
}

.hero-card-number {
  display: block;
  font-size: $font-size-3xl;
  font-weight: $font-weight-bold;
  color: $color-primary;
}

.hero-card-label {
  font-size: $font-size-sm;
  color: $color-gray-500;
  text-transform: uppercase;
  letter-spacing: $letter-spacing-wider;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

// Stats Section
.stats-section {
  padding: $spacing-12 0;
  border-top: 1px solid $color-gray-200;
  border-bottom: 1px solid $color-gray-200;
  margin-bottom: $spacing-16;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-8;
  
  @media (max-width: $breakpoint-sm) {
    grid-template-columns: 1fr;
    gap: $spacing-6;
  }
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.stat-icon {
  font-size: 28px;
  color: $color-accent;
  margin-bottom: $spacing-3;
}

.stat-number {
  font-size: $font-size-3xl;
  font-weight: $font-weight-bold;
  color: $color-primary;
  line-height: 1;
  margin-bottom: $spacing-2;
}

.stat-label {
  font-size: $font-size-sm;
  color: $color-gray-500;
  text-transform: uppercase;
  letter-spacing: $letter-spacing-wider;
}

// Section Common
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-10;
}

.section-title-group {
  display: flex;
  align-items: center;
  gap: $spacing-3;
}

.section-icon {
  font-size: 24px;
  color: $color-primary;
}

.section-title {
  font-size: $font-size-2xl;
  font-weight: $font-weight-semibold;
  color: $color-primary;
}

.btn-text {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  background: none;
  border: none;
  color: $color-gray-600;
  font-size: $font-size-sm;
  font-weight: $font-weight-medium;
  cursor: pointer;
  transition: color $transition-fast;
  
  &:hover {
    color: $color-primary;
  }
}

// Announcements Section
.announcements-section {
  margin-bottom: $spacing-20;
}

.announcements-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-6;
  
  @media (max-width: $breakpoint-lg) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (max-width: $breakpoint-sm) {
    grid-template-columns: 1fr;
  }
}

.announcement-card {
  background: $color-white;
  border-radius: $radius-lg;
  padding: $spacing-6;
  transition: all $transition-base;
  animation: slideUp 0.5s ease forwards;
  opacity: 0;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-lg;
  }
}

.announcement-meta {
  display: flex;
  align-items: center;
  gap: $spacing-3;
  margin-bottom: $spacing-4;
}

.announcement-type-icon {
  font-size: 16px;
  color: $color-gray-400;
}

.announcement-date {
  font-size: $font-size-xs;
  color: $color-gray-500;
}

.announcement-pin {
  display: inline-flex;
  align-items: center;
  gap: $spacing-1;
  font-size: $font-size-xs;
  color: $color-accent-muted;
  font-weight: $font-weight-medium;
}

.announcement-title {
  font-size: $font-size-lg;
  font-weight: $font-weight-semibold;
  color: $color-primary;
  margin-bottom: $spacing-3;
  line-height: $line-height-tight;
}

.announcement-excerpt {
  font-size: $font-size-sm;
  color: $color-gray-600;
  line-height: $line-height-relaxed;
}

// Pets Section
.pets-section {
  margin-bottom: $spacing-20;
}

.pets-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-6;
  
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
      transform: scale(1.05);
    }
    
    .pet-arrow {
      transform: translateX(4px);
    }
  }
}

.pet-image-wrapper {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.pet-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform $transition-slow;
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

.pet-content {
  padding: $spacing-5;
}

.pet-name {
  font-size: $font-size-lg;
  font-weight: $font-weight-semibold;
  color: $color-primary;
  margin-bottom: $spacing-1;
}

.pet-breed {
  font-size: $font-size-sm;
  color: $color-gray-500;
  margin-bottom: $spacing-3;
}

.pet-details {
  margin-bottom: $spacing-4;
}

.pet-detail {
  display: inline-flex;
  align-items: center;
  gap: $spacing-1;
  font-size: $font-size-xs;
  color: $color-gray-500;
  
  .el-icon {
    font-size: 12px;
  }
}

.pet-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: $spacing-4;
  border-top: 1px solid $color-gray-200;
}

.pet-status {
  font-size: $font-size-xs;
  font-weight: $font-weight-semibold;
  color: $color-success;
  text-transform: uppercase;
  letter-spacing: $letter-spacing-wider;
}

.pet-arrow {
  color: $color-gray-400;
  transition: transform $transition-fast;
}

// Empty State
.empty-state {
  text-align: center;
  padding: $spacing-16 0;
}

.empty-icon {
  font-size: 48px;
  color: $color-gray-300;
  margin-bottom: $spacing-4;
}

.empty-text {
  font-size: $font-size-lg;
  color: $color-gray-600;
  margin-bottom: $spacing-2;
}

.empty-subtext {
  font-size: $font-size-sm;
  color: $color-gray-500;
}

// CTA Section
.cta-section {
  background: $color-primary;
  border-radius: $radius-xl;
  padding: $spacing-16 $spacing-8;
  text-align: center;
}

.cta-title {
  font-size: $font-size-3xl;
  font-weight: $font-weight-bold;
  color: $color-white;
  margin-bottom: $spacing-4;
}

.cta-description {
  font-size: $font-size-lg;
  color: rgba($color-white, 0.8);
  margin-bottom: $spacing-8;
}

.btn-cta {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-4 $spacing-8;
  background: $color-accent;
  color: $color-primary;
  font-size: $font-size-base;
  font-weight: $font-weight-semibold;
  border: none;
  border-radius: $radius-md;
  cursor: pointer;
  transition: all $transition-base;
  
  &:hover {
    background: $color-accent-muted;
    transform: translateY(-2px);
  }
}

// Animation keyframes
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
