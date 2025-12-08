<template>
  <div class="forum-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">宠友交流</h1>
        <p class="page-subtitle">分享养宠经验，交流心得体会</p>
      </div>
    </div>

    <div class="page-container">
      <div class="toolbar">
        <div class="search-box">
          <el-input 
            v-model="keyword" 
            placeholder="搜索帖子标题或内容..." 
            size="large"
            clearable
            @keyup.enter="searchPosts"
            @clear="searchPosts"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button type="primary" size="large" @click="searchPosts">搜索</el-button>
        </div>
        <el-button type="primary" size="large" @click="showCreateDialog">
          <el-icon><Plus /></el-icon> 发布帖子
        </el-button>
      </div>

      <div class="posts-list" v-loading="loading">
        <div v-for="post in posts" :key="post.id" class="post-card" @click="goToDetail(post.id)">
          <div class="post-main">
            <h3 class="post-title">{{ post.title }}</h3>
            <p class="post-summary">{{ post.content }}</p>
            <div class="post-meta">
              <span class="author">
                <el-avatar :size="24" :src="post.userAvatar || defaultAvatar" />
                {{ post.userName }}
              </span>
              <span class="time">{{ formatDate(post.createTime) }}</span>
              <span class="stats">
                <el-icon><View /></el-icon> {{ post.viewCount || 0 }}
                <el-icon><ChatDotRound /></el-icon> {{ post.commentCount || 0 }}
                <el-icon><Star /></el-icon> {{ post.likeCount || 0 }}
              </span>
            </div>
          </div>
        </div>

        <div v-if="!loading && !posts.length" class="empty-state">
          <el-icon class="empty-icon"><ChatDotRound /></el-icon>
          <p class="empty-text">暂无帖子，快来发布第一条吧</p>
        </div>
      </div>

      <div v-if="total > pageSize" class="pagination-container">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="pageNum"
          @current-change="loadPosts"
        />
      </div>
    </div>

    <!-- Create Post Dialog -->
    <el-dialog v-model="createVisible" title="发布帖子" width="600px">
      <el-form :model="postForm" :rules="rules" ref="postFormRef" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="postForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="6"
            placeholder="分享你的养宠趣事..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createVisible = false">取消</el-button>
          <el-button type="primary" @click="submitPost" :loading="submitting">发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPosts, createPost } from '@/api/forum'
import { Plus, View, ChatDotRound, Star, Search } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

const router = useRouter()
const posts = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const keyword = ref('')

const createVisible = ref(false)
const submitting = ref(false)
const postFormRef = ref(null)
const postForm = ref({
  title: '',
  content: ''
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

onMounted(() => {
  loadPosts()
})

async function loadPosts() {
  loading.value = true
  try {
    const res = await getPosts({ pageNum: pageNum.value, pageSize: pageSize.value, keyword: keyword.value || undefined })
    if (res.code === 200) {
      posts.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function searchPosts() {
  pageNum.value = 1
  loadPosts()
}

function showCreateDialog() {
  createVisible.value = true
  postForm.value = { title: '', content: '' }
}

async function submitPost() {
  if (!postFormRef.value) return
  
  await postFormRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const res = await createPost(postForm.value)
        if (res.code === 200) {
          ElMessage.success('发布成功')
          createVisible.value = false
          loadPosts()
        } else {
          ElMessage.error(res.message || '发布失败')
        }
      } finally {
        submitting.value = false
      }
    }
  })
}

function goToDetail(id) {
  router.push(`/forum/${id}`)
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.forum-page {
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

.toolbar {
  margin-bottom: $spacing-6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: $spacing-4;
}

.search-box {
  display: flex;
  gap: $spacing-2;
  flex: 1;
  max-width: 400px;
}

.posts-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-4;
}

.post-card {
  background: $color-white;
  border-radius: $radius-lg;
  padding: $spacing-6;
  cursor: pointer;
  transition: all $transition-base;
  border: 1px solid transparent;

  &:hover {
    transform: translateY(-2px);
    box-shadow: $shadow-md;
    border-color: rgba($color-primary, 0.1);
  }
}

.post-title {
  font-size: $font-size-xl;
  font-weight: $font-weight-bold;
  color: $color-gray-900;
  margin-bottom: $spacing-2;
}

.post-summary {
  font-size: $font-size-base;
  color: $color-gray-600;
  margin-bottom: $spacing-4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: $spacing-6;
  color: $color-gray-500;
  font-size: $font-size-sm;

  .author {
    display: flex;
    align-items: center;
    gap: $spacing-2;
    color: $color-gray-700;
    font-weight: $font-weight-medium;
  }

  .stats {
    margin-left: auto;
    display: flex;
    gap: $spacing-4;
    
    .el-icon {
      margin-right: 4px;
    }
  }
}

.empty-state {
  text-align: center;
  padding: $spacing-16 0;
  
  .empty-icon {
    font-size: 64px;
    color: $color-gray-300;
    margin-bottom: $spacing-4;
  }
  
  .empty-text {
    color: $color-gray-500;
  }
}

.pagination-container {
  margin-top: $spacing-8;
  display: flex;
  justify-content: center;
}
</style>