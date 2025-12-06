<template>
  <div class="comment-manage">
    <div class="page-header">
      <h2>评论管理</h2>
    </div>

    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="帖子ID">
          <el-input v-model="searchForm.postId" placeholder="请输入帖子ID" clearable />
        </el-form-item>
        <el-form-item label="用户ID">
          <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable />
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="评论内容关键词" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadComments">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="comments" v-loading="loading" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="postId" label="帖子ID" width="80" />
        <el-table-column prop="postTitle" label="所属帖子" width="150" show-overflow-tooltip />
        <el-table-column prop="userName" label="评论用户" width="120" />
        <el-table-column prop="content" label="评论内容" show-overflow-tooltip />
        <el-table-column prop="createTime" label="评论时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click="deleteComment(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="pageNum"
          @current-change="loadComments"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllComments, deleteComment as deleteCommentApi } from '@/api/forum'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const comments = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  postId: '',
  userId: '',
  keyword: ''
})

onMounted(() => {
  loadComments()
})

async function loadComments() {
  loading.value = true
  try {
    const res = await getAllComments({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      postId: searchForm.postId || undefined,
      userId: searchForm.userId || undefined,
      keyword: searchForm.keyword || undefined
    })
    if (res.code === 200) {
      comments.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function resetSearch() {
  searchForm.postId = ''
  searchForm.userId = ''
  searchForm.keyword = ''
  pageNum.value = 1
  loadComments()
}

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

async function deleteComment(row) {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '确认删除', {
      type: 'warning'
    })
    const res = await deleteCommentApi(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadComments()
    } else {
      ElMessage.error(res.message)
    }
  } catch {}
}
</script>

<style lang="scss" scoped>
.page-header {
  margin-bottom: 20px;
  
  h2 {
    margin: 0;
    font-size: 20px;
  }
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.table-card {
  .pagination-container {
    display: flex;
    justify-content: flex-end;
    margin-top: 20px;
  }
}
</style>
