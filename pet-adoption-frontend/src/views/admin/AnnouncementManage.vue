<template>
  <div class="announcement-manage">
    <el-card>
      <template #header>
        <div class="header">
          <span>公告管理</span>
          <el-button type="primary" @click="showAddDialog">发布公告</el-button>
        </div>
      </template>

      <el-table :data="announcements" v-loading="loading">
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)" size="small">{{ getTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="置顶" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isTop" type="danger" size="small">置顶</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="showEditDialog(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadAnnouncements"
        @current-change="loadAnnouncements"
        style="margin-top: 20px;"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type">
            <el-option label="普通公告" :value="0" />
            <el-option label="领养须知" :value="1" />
            <el-option label="活动公告" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="0">草稿</el-radio>
            <el-radio :label="1">发布</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAllAnnouncements, addAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/announcement'
import { ElMessage, ElMessageBox } from 'element-plus'

const announcements = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const submitting = ref(false)

const form = reactive({
  id: null,
  title: '',
  type: 0,
  content: '',
  isTop: 0,
  status: 1
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

onMounted(() => {
  loadAnnouncements()
})

async function loadAnnouncements() {
  loading.value = true
  try {
    const res = await getAllAnnouncements({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (res.code === 200) {
      announcements.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function getTypeText(type) {
  const map = { 0: '普通公告', 1: '领养须知', 2: '活动公告' }
  return map[type] || '公告'
}

function getTypeTag(type) {
  const map = { 0: 'info', 1: 'warning', 2: 'success' }
  return map[type] || 'info'
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

function resetForm() {
  Object.assign(form, { id: null, title: '', type: 0, content: '', isTop: 0, status: 1 })
}

function showAddDialog() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function showEditDialog(row) {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    const res = isEdit.value ? await updateAnnouncement(form.id, form) : await addAnnouncement(form)
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
      dialogVisible.value = false
      loadAnnouncements()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除该公告吗？', '提示', { type: 'warning' })
  const res = await deleteAnnouncement(row.id)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    loadAnnouncements()
  }
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
