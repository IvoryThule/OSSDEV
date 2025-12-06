<template>
  <div class="pet-manage">
    <el-card>
      <template #header>
        <div class="header">
          <span>宠物管理</span>
          <el-button type="primary" @click="showAddDialog">发布宠物</el-button>
        </div>
      </template>

      <el-form inline class="filter-form">
        <el-form-item label="分类">
          <el-select v-model="filter.categoryId" placeholder="全部" clearable @change="loadPets">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filter.status" placeholder="全部" clearable @change="loadPets">
            <el-option label="待领养" :value="0" />
            <el-option label="申请中" :value="1" />
            <el-option label="已领养" :value="2" />
            <el-option label="已下架" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="搜索">
          <el-input v-model="filter.keyword" placeholder="名字/品种" clearable @keyup.enter="loadPets" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadPets">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="pets" v-loading="loading">
        <el-table-column prop="name" label="名称" width="120" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="breed" label="品种" width="120" />
        <el-table-column prop="age" label="年龄" width="80" />
        <el-table-column label="性别" width="80">
          <template #default="{ row }">{{ row.gender === 1 ? '公' : row.gender === 2 ? '母' : '未知' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publisherName" label="发布者" width="120" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="showEditDialog(row)">编辑</el-button>
            <el-button link type="warning" @click="handleUpdateStatus(row)">状态</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="filter.pageNum"
        v-model:page-size="filter.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadPets"
        @current-change="loadPets"
        style="margin-top: 20px;"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑宠物' : '发布宠物'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入宠物名称" />
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="品种" prop="breed">
          <el-input v-model="form.breed" placeholder="请输入品种" />
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input v-model="form.age" placeholder="如：2岁、6个月" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio :label="1">公</el-radio>
            <el-radio :label="2">母</el-radio>
            <el-radio :label="0">未知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="体重(kg)">
          <el-input-number v-model="form.weight" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="毛色">
          <el-input v-model="form.color" placeholder="请输入毛色" />
        </el-form-item>
        <el-form-item label="健康状况">
          <el-input v-model="form.healthStatus" placeholder="请输入健康状况" />
        </el-form-item>
        <el-form-item label="疫苗">
          <el-switch v-model="form.isVaccinated" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="绝育">
          <el-switch v-model="form.isSterilized" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="详细介绍">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入详细介绍" />
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
import { getPetList, addPet, updatePet, deletePet, updatePetStatus } from '@/api/pet'
import { getCategories } from '@/api/category'
import { ElMessage, ElMessageBox } from 'element-plus'

const pets = ref([])
const categories = ref([])
const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const submitting = ref(false)

const filter = reactive({
  pageNum: 1,
  pageSize: 10,
  categoryId: null,
  status: null,
  keyword: ''
})

const form = reactive({
  id: null,
  name: '',
  categoryId: null,
  breed: '',
  age: '',
  gender: 0,
  weight: null,
  color: '',
  healthStatus: '',
  isVaccinated: 0,
  isSterilized: 0,
  imageUrl: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

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

function getStatusText(status) {
  const map = { 0: '待领养', 1: '申请中', 2: '已领养', 3: '已下架' }
  return map[status] || '未知'
}

function getStatusType(status) {
  const map = { 0: 'success', 1: 'warning', 2: 'info', 3: 'danger' }
  return map[status] || 'info'
}

function resetForm() {
  Object.assign(form, {
    id: null, name: '', categoryId: null, breed: '', age: '', gender: 0,
    weight: null, color: '', healthStatus: '', isVaccinated: 0, isSterilized: 0,
    imageUrl: '', description: ''
  })
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
    const res = isEdit.value ? await updatePet(form.id, form) : await addPet(form)
    if (res.code === 200) {
      ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
      dialogVisible.value = false
      loadPets()
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    submitting.value = false
  }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除该宠物吗？', '提示', { type: 'warning' })
  const res = await deletePet(row.id)
  if (res.code === 200) {
    ElMessage.success('删除成功')
    loadPets()
  }
}

async function handleUpdateStatus(row) {
  const { value } = await ElMessageBox.prompt('请输入新状态 (0-待领养, 1-申请中, 2-已领养, 3-已下架)', '修改状态', {
    inputPattern: /^[0-3]$/,
    inputErrorMessage: '请输入0-3之间的数字',
    inputValue: String(row.status)
  })
  const res = await updatePetStatus(row.id, Number(value))
  if (res.code === 200) {
    ElMessage.success('状态更新成功')
    loadPets()
  }
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-form {
  margin-bottom: 20px;
}
</style>
