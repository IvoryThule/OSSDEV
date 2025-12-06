<template>
  <div class="my-pets-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">我的送养</h1>
        <p class="page-subtitle">管理您发布的送养宠物信息</p>
      </div>
    </div>

    <div class="page-container">
      <div class="toolbar">
        <el-button type="primary" size="large" @click="$router.push('/publish')">
          <el-icon><Plus /></el-icon> 发布新的送养
        </el-button>
      </div>

      <div class="pets-list" v-loading="loading">
        <div v-for="pet in pets" :key="pet.id" class="pet-card">
          <div class="pet-image">
            <img :src="getImageUrl(pet.imageUrl)" :alt="pet.name" />
            <span class="status-badge" :class="getStatusClass(pet.status)">
              {{ getStatusText(pet.status) }}
            </span>
          </div>
          <div class="pet-info">
            <h3 class="pet-name">{{ pet.name }}</h3>
            <p class="pet-breed">{{ pet.categoryName }} · {{ pet.breed }}</p>
            <p class="pet-desc">{{ pet.description }}</p>
            <div class="pet-meta">
              <span>{{ pet.age }}</span>
              <span>{{ pet.gender === 1 ? '公' : '母' }}</span>
              <span>{{ pet.weight }}kg</span>
            </div>
            <div class="pet-time">发布于 {{ formatDate(pet.createTime) }}</div>
          </div>
          <div class="pet-actions">
            <el-button type="primary" plain size="small" @click="editPet(pet)">
              <el-icon><Edit /></el-icon> 编辑
            </el-button>
            <el-button v-if="pet.status === 0" type="warning" plain size="small" @click="cancelPublish(pet)">
              <el-icon><Close /></el-icon> 取消送养
            </el-button>
            <el-button type="danger" plain size="small" @click="deletePet(pet)">
              <el-icon><Delete /></el-icon> 删除
            </el-button>
          </div>
        </div>

        <div v-if="!loading && !pets.length" class="empty-state">
          <el-icon class="empty-icon"><Document /></el-icon>
          <p class="empty-text">您还没有发布送养信息</p>
          <el-button type="primary" @click="$router.push('/publish')">立即发布</el-button>
        </div>
      </div>

      <div v-if="total > pageSize" class="pagination-container">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="pageNum"
          @current-change="loadPets"
        />
      </div>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog v-model="editVisible" title="编辑送养信息" width="600px">
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-width="100px">
        <el-form-item label="宠物昵称" prop="name">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="品种" prop="breed">
          <el-input v-model="editForm.breed" />
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input v-model="editForm.age" />
        </el-form-item>
        <el-form-item label="详细描述" prop="description">
          <el-input v-model="editForm.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyPets, updatePet, deletePet as deletePetApi, updatePetStatus } from '@/api/pet'
import { Plus, Edit, Close, Delete, Document } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const pets = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const editVisible = ref(false)
const editFormRef = ref(null)
const editForm = ref({})
const saving = ref(false)
const currentPetId = ref(null)

const rules = {
  name: [{ required: true, message: '请输入宠物昵称', trigger: 'blur' }],
  breed: [{ required: true, message: '请输入品种', trigger: 'blur' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  description: [{ required: true, message: '请输入详细描述', trigger: 'blur' }]
}

onMounted(() => {
  loadPets()
})

async function loadPets() {
  loading.value = true
  try {
    const res = await getMyPets({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (res.code === 200) {
      pets.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function getImageUrl(url) {
  if (!url) return '/pets/default.jpg'
  if (url.startsWith('http')) return url
  return url
}

function getStatusText(status) {
  const map = { 0: '待领养', 1: '申请中', 2: '已领养', 3: '已取消' }
  return map[status] || '未知'
}

function getStatusClass(status) {
  const map = { 0: 'available', 1: 'pending', 2: 'adopted', 3: 'cancelled' }
  return map[status] || ''
}

function formatDate(date) {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

function editPet(pet) {
  currentPetId.value = pet.id
  editForm.value = {
    name: pet.name,
    breed: pet.breed,
    age: pet.age,
    description: pet.description
  }
  editVisible.value = true
}

async function saveEdit() {
  await editFormRef.value.validate()
  saving.value = true
  try {
    const res = await updatePet(currentPetId.value, editForm.value)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      editVisible.value = false
      loadPets()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    saving.value = false
  }
}

async function cancelPublish(pet) {
  try {
    await ElMessageBox.confirm('确定要取消该送养信息吗？取消后宠物将从待领养列表中移除。', '确认取消', {
      type: 'warning'
    })
    const res = await updatePetStatus(pet.id, 3)
    if (res.code === 200) {
      ElMessage.success('已取消送养')
      loadPets()
    } else {
      ElMessage.error(res.message)
    }
  } catch {}
}

async function deletePet(pet) {
  try {
    await ElMessageBox.confirm('确定要删除该送养信息吗？此操作不可恢复。', '确认删除', {
      type: 'warning'
    })
    const res = await deletePetApi(pet.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadPets()
    } else {
      ElMessage.error(res.message)
    }
  } catch {}
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.my-pets-page {
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

.toolbar {
  margin-bottom: $spacing-6;
}

.pets-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-4;
}

.pet-card {
  display: flex;
  gap: $spacing-6;
  background: $color-white;
  border-radius: $radius-xl;
  padding: $spacing-6;
  box-shadow: $shadow-sm;
}

.pet-image {
  position: relative;
  width: 160px;
  height: 160px;
  flex-shrink: 0;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: $radius-lg;
  }
  
  .status-badge {
    position: absolute;
    top: $spacing-2;
    left: $spacing-2;
    padding: $spacing-1 $spacing-3;
    font-size: $font-size-xs;
    font-weight: $font-weight-medium;
    border-radius: $radius-full;
    
    &.available {
      background: rgba($color-success, 0.9);
      color: $color-white;
    }
    
    &.pending {
      background: rgba($color-warning, 0.9);
      color: $color-white;
    }
    
    &.adopted {
      background: rgba($color-info, 0.9);
      color: $color-white;
    }
    
    &.cancelled {
      background: rgba($color-gray-500, 0.9);
      color: $color-white;
    }
  }
}

.pet-info {
  flex: 1;
  
  .pet-name {
    font-size: $font-size-xl;
    font-weight: $font-weight-bold;
    color: $color-primary;
    margin-bottom: $spacing-2;
  }
  
  .pet-breed {
    font-size: $font-size-sm;
    color: $color-gray-500;
    margin-bottom: $spacing-3;
  }
  
  .pet-desc {
    font-size: $font-size-sm;
    color: $color-gray-600;
    line-height: $line-height-relaxed;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    margin-bottom: $spacing-3;
  }
  
  .pet-meta {
    display: flex;
    gap: $spacing-4;
    font-size: $font-size-sm;
    color: $color-gray-500;
    margin-bottom: $spacing-2;
    
    span {
      display: flex;
      align-items: center;
      gap: $spacing-1;
    }
  }
  
  .pet-time {
    font-size: $font-size-xs;
    color: $color-gray-400;
  }
}

.pet-actions {
  display: flex;
  flex-direction: column;
  gap: $spacing-2;
  justify-content: center;
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
    font-size: $font-size-lg;
    color: $color-gray-400;
    margin-bottom: $spacing-6;
  }
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: $spacing-8;
}
</style>
