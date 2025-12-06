<template>
  <div class="publish-page">
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">发布送养信息</h1>
        <p class="page-subtitle">为您的爱宠寻找一个温暖的新家</p>
      </div>
    </div>

    <div class="page-container">
      <div class="form-card">
        <el-form 
          ref="formRef" 
          :model="form" 
          :rules="rules" 
          label-position="top"
          class="publish-form"
        >
          <!-- Basic Info Section -->
          <div class="form-section">
            <h3 class="section-title">基本信息</h3>
            <div class="form-grid">
              <el-form-item label="宠物昵称" prop="name">
                <el-input v-model="form.name" placeholder="给它起个名字" size="large" />
              </el-form-item>
              
              <el-form-item label="宠物分类" prop="categoryId">
                <el-select v-model="form.categoryId" placeholder="选择分类" size="large" style="width: 100%">
                  <el-option 
                    v-for="item in categories" 
                    :key="item.id" 
                    :label="item.name" 
                    :value="item.id" 
                  />
                </el-select>
              </el-form-item>

              <el-form-item label="品种" prop="breed">
                <el-input v-model="form.breed" placeholder="例如：中华田园猫" size="large" />
              </el-form-item>

              <el-form-item label="年龄" prop="age">
                <el-input v-model="form.age" placeholder="例如：1岁" size="large" />
              </el-form-item>

              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="form.gender" size="large">
                  <el-radio-button :label="1">公</el-radio-button>
                  <el-radio-button :label="2">母</el-radio-button>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="体重 (kg)" prop="weight">
                <el-input-number v-model="form.weight" :precision="1" :step="0.1" :min="0" size="large" style="width: 100%" />
              </el-form-item>
            </div>
          </div>

          <!-- Details Section -->
          <div class="form-section">
            <h3 class="section-title">详细特征</h3>
            <div class="form-grid">
              <el-form-item label="毛色" prop="color">
                <el-input v-model="form.color" placeholder="例如：橘色" size="large" />
              </el-form-item>

              <el-form-item label="健康状况" prop="healthStatus">
                <el-input v-model="form.healthStatus" placeholder="例如：健康、已驱虫" size="large" />
              </el-form-item>

              <el-form-item label="疫苗情况" prop="isVaccinated">
                <el-switch 
                  v-model="form.isVaccinated" 
                  :active-value="1" 
                  :inactive-value="0"
                  active-text="已接种"
                  inactive-text="未接种"
                />
              </el-form-item>

              <el-form-item label="绝育情况" prop="isSterilized">
                <el-switch 
                  v-model="form.isSterilized" 
                  :active-value="1" 
                  :inactive-value="0"
                  active-text="已绝育"
                  inactive-text="未绝育"
                />
              </el-form-item>
            </div>

            <el-form-item label="详细描述" prop="description">
              <el-input 
                v-model="form.description" 
                type="textarea" 
                :rows="4" 
                placeholder="介绍一下它的性格、习惯等..." 
              />
            </el-form-item>
          </div>

          <!-- Image Upload Section -->
          <div class="form-section">
            <h3 class="section-title">照片上传 <span class="optional-tag">(可选)</span></h3>
            <el-form-item>
              <el-upload
                class="avatar-uploader"
                action="/api/files/upload"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
                :headers="uploadHeaders"
              >
                <img v-if="form.imageUrl" :src="form.imageUrl" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
              <div class="upload-tip">支持 jpg/png 格式，大小不超过 2MB</div>
            </el-form-item>
          </div>

          <div class="form-actions">
            <el-button size="large" @click="$router.back()">取消</el-button>
            <el-button type="primary" size="large" :loading="loading" @click="handleSubmit">
              发布送养
            </el-button>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { addPet } from '@/api/pet'
import { getCategories } from '@/api/category'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const categories = ref([])

const form = reactive({
  name: '',
  categoryId: null,
  breed: '',
  age: '',
  gender: 1,
  weight: 0,
  color: '',
  healthStatus: '健康',
  isVaccinated: 0,
  isSterilized: 0,
  description: '',
  imageUrl: ''
})

const rules = {
  name: [{ required: true, message: '请输入宠物昵称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  breed: [{ required: true, message: '请输入品种', trigger: 'blur' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  description: [{ required: true, message: '请输入详细描述', trigger: 'blur' }]
}

const uploadHeaders = {
  Authorization: `Bearer ${userStore.token}`
}

onMounted(() => {
  loadCategories()
})

async function loadCategories() {
  const res = await getCategories()
  if (res.code === 200) {
    categories.value = res.data
  }
}

function handleAvatarSuccess(res) {
  if (res.code === 200) {
    form.imageUrl = res.data
    ElMessage.success('上传成功')
  } else {
    ElMessage.error(res.message)
  }
}

function beforeAvatarUpload(file) {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('上传头像图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!')
  }
  return isJPG && isLt2M
}

async function handleSubmit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await addPet(form)
    if (res.code === 200) {
      ElMessage.success('发布成功，请等待审核')
      router.push('/pets')
    } else {
      ElMessage.error(res.message)
    }
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.publish-page {
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

.form-card {
  background: $color-white;
  border-radius: $radius-xl;
  padding: $spacing-8;
  box-shadow: $shadow-lg;
}

.form-section {
  margin-bottom: $spacing-8;
  padding-bottom: $spacing-8;
  border-bottom: 1px solid $color-gray-100;
  
  &:last-child {
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 0;
  }
}

.section-title {
  font-size: $font-size-xl;
  font-weight: $font-weight-semibold;
  color: $color-primary;
  margin-bottom: $spacing-6;
  padding-left: $spacing-3;
  border-left: 4px solid $color-accent;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-6;
  
  @media (max-width: $breakpoint-sm) {
    grid-template-columns: 1fr;
  }
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 2px dashed $color-gray-300;
    border-radius: $radius-lg;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: all $transition-base;
    
    &:hover {
      border-color: $color-primary;
    }
  }
}

.avatar-uploader-icon {
  font-size: 28px;
  color: $color-gray-400;
  width: 178px;
  height: 178px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
  object-fit: cover;
}

.upload-tip {
  font-size: $font-size-xs;
  color: $color-gray-500;
  margin-top: $spacing-2;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: $spacing-4;
  margin-top: $spacing-8;
}
</style>