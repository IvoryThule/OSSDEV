<template>
  <div class="forum-detail-page">
    <div class="page-container">
      <div class="back-link" @click="router.back()">
        <el-icon><ArrowLeft /></el-icon> 返回列表
      </div>

      <div v-loading="loading" class="content-wrapper">
        <article v-if="post" class="post-detail">
          <header class="post-header">
            <h1 class="post-title">{{ post.title }}</h1>
            <div class="post-meta">
              <div class="author-info">
                <el-avatar :size="40" :src="post.userAvatar || defaultAvatar" />
                <div class="author-details">
                  <span class="author-name">{{ post.userName }}</span>
                  <span class="post-time">{{ formatDate(post.createTime) }}</span>
                </div>
              </div>
              <div class="post-stats">
                <span class="stat-item">
                  <el-icon><View /></el-icon> {{ post.viewCount || 0 }}
                </span>
              </div>
            </div>
          </header>

          <div class="post-content">
            {{ post.content }}
          </div>

          <div class="post-actions">
            <button 
              class="action-btn like-btn" 
              :class="{ active: isLiked }"
              @click="handleLike"
            >
              <el-icon><StarFilled v-if="isLiked" /><Star v-else /></el-icon>
              {{ post.likeCount || 0 }} 赞
            </button>
          </div>
        </article>

        <div class="comments-section">
          <h3 class="section-title">评论 ({{ comments.length }})</h3>
          
          <div class="comment-input">
            <el-input
              v-model="commentContent"
              type="textarea"
              :rows="3"
              placeholder="写下你的评论..."
            />
            <div class="input-actions">
              <el-button type="primary" @click="submitComment" :loading="submitting" :disabled="!commentContent.trim()">
                发表评论
              </el-button>
            </div>
          </div>

          <div class="comments-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <el-avatar :size="32" :src="comment.userAvatar || defaultAvatar" class="comment-avatar" />
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-author">{{ comment.userName }}</span>
                  <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
              </div>
              <el-button 
                v-if="isCurrentUser(comment.userId)" 
                type="danger" 
                link 
                size="small"
                @click="handleDeleteComment(comment.id)"
              >
                删除
              </el-button>
            </div>
            
            <div v-if="!comments.length" class="empty-comments">
              暂无评论，快来抢沙发~
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getPostDetail, getComments, createComment, deleteComment, toggleLike, getLikeStatus } from '@/api/forum'
import { ArrowLeft, View, Star, StarFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const defaultAvatar = 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const post = ref(null)
const comments = ref([])
const loading = ref(false)
const commentContent = ref('')
const submitting = ref(false)
const isLiked = ref(false)

onMounted(() => {
  loadData()
})

async function loadData() {
  loading.value = true
  try {
    const postId = route.params.id
    const [postRes, commentsRes] = await Promise.all([
      getPostDetail(postId),
      getComments(postId)
    ])
    
    if (postRes.code === 200) {
      post.value = postRes.data
      checkLikeStatus()
    }
    if (commentsRes.code === 200) {
      comments.value = commentsRes.data
    }
  } finally {
    loading.value = false
  }
}

async function checkLikeStatus() {
  if (!userStore.userInfo) return
  try {
    const res = await getLikeStatus(post.value.id, 1) // 1 for POST
    if (res.code === 200) {
      isLiked.value = res.data
    }
  } catch (e) {
    console.error(e)
  }
}

async function handleLike() {
  if (!userStore.userInfo) {
    ElMessage.warning('请先登录')
    return
  }
  
  try {
    const res = await toggleLike({
      targetId: post.value.id,
      type: 1 // 1 for POST
    })
    
    if (res.code === 200) {
      isLiked.value = !isLiked.value
      post.value.likeCount += isLiked.value ? 1 : -1
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

async function submitComment() {
  if (!commentContent.value.trim()) return
  
  submitting.value = true
  try {
    const res = await createComment({
      postId: post.value.id,
      content: commentContent.value
    })
    
    if (res.code === 200) {
      ElMessage.success('评论成功')
      commentContent.value = ''
      // Reload comments
      const commentsRes = await getComments(post.value.id)
      if (commentsRes.code === 200) {
        comments.value = commentsRes.data
        post.value.commentCount++
      }
    } else {
      ElMessage.error(res.message || '评论失败')
    }
  } finally {
    submitting.value = false
  }
}

function handleDeleteComment(id) {
  ElMessageBox.confirm('确定删除这条评论吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    const res = await deleteComment(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      comments.value = comments.value.filter(c => c.id !== id)
      post.value.commentCount--
    }
  })
}

function isCurrentUser(userId) {
  return userStore.userInfo && userStore.userInfo.id === userId
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.forum-detail-page {
  min-height: 100vh;
  background: $color-cream;
  padding: $spacing-8 0;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 $spacing-6;
}

.back-link {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  color: $color-gray-600;
  cursor: pointer;
  margin-bottom: $spacing-6;
  font-weight: $font-weight-medium;
  
  &:hover {
    color: $color-primary;
  }
}

.content-wrapper {
  display: flex;
  flex-direction: column;
  gap: $spacing-6;
}

.post-detail {
  background: $color-white;
  border-radius: $radius-xl;
  padding: $spacing-8;
  box-shadow: $shadow-sm;
}

.post-header {
  margin-bottom: $spacing-8;
  padding-bottom: $spacing-6;
  border-bottom: 1px solid $color-gray-100;
}

.post-title {
  font-size: $font-size-3xl;
  font-weight: $font-weight-bold;
  color: $color-gray-900;
  margin-bottom: $spacing-6;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.author-info {
  display: flex;
  align-items: center;
  gap: $spacing-3;
}

.author-details {
  display: flex;
  flex-direction: column;
}

.author-name {
  font-weight: $font-weight-bold;
  color: $color-gray-900;
}

.post-time {
  font-size: $font-size-xs;
  color: $color-gray-500;
}

.post-stats {
  color: $color-gray-500;
  font-size: $font-size-sm;
}

.post-content {
  font-size: $font-size-lg;
  line-height: $line-height-relaxed;
  color: $color-gray-800;
  white-space: pre-wrap;
  margin-bottom: $spacing-8;
}

.post-actions {
  display: flex;
  justify-content: center;
  padding-top: $spacing-6;
  border-top: 1px solid $color-gray-100;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: $spacing-2;
  padding: $spacing-2 $spacing-6;
  border-radius: $radius-full;
  border: 1px solid $color-gray-200;
  background: $color-white;
  color: $color-gray-600;
  cursor: pointer;
  transition: all $transition-base;
  font-size: $font-size-base;
  
  &:hover {
    background: $color-gray-50;
  }
  
  &.active {
    background: rgba($color-accent, 0.1);
    color: $color-accent;
    border-color: $color-accent;
  }
  
  &.like-btn {
    .el-icon {
      font-size: 20px;
    }
  }
}

.comments-section {
  background: $color-white;
  border-radius: $radius-xl;
  padding: $spacing-8;
  box-shadow: $shadow-sm;
}

.section-title {
  font-size: $font-size-xl;
  font-weight: $font-weight-bold;
  margin-bottom: $spacing-6;
}

.comment-input {
  margin-bottom: $spacing-8;
  
  .input-actions {
    margin-top: $spacing-3;
    display: flex;
    justify-content: flex-end;
  }
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-6;
}

.comment-item {
  display: flex;
  gap: $spacing-4;
  padding-bottom: $spacing-6;
  border-bottom: 1px solid $color-gray-100;
  
  &:last-child {
    border-bottom: none;
    padding-bottom: 0;
  }
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: $spacing-2;
}

.comment-author {
  font-weight: $font-weight-bold;
  color: $color-gray-900;
}

.comment-time {
  font-size: $font-size-xs;
  color: $color-gray-500;
}

.comment-text {
  color: $color-gray-700;
  line-height: $line-height-relaxed;
}

.empty-comments {
  text-align: center;
  color: $color-gray-500;
  padding: $spacing-8 0;
}
</style>