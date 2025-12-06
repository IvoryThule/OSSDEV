<template>
  <div class="application-manage">
    <el-card>
      <template #header>
        <span>领养申请管理</span>
      </template>

      <el-form inline class="filter-form">
        <el-form-item label="状态">
          <el-select v-model="filter.status" placeholder="全部" clearable @change="loadApplications">
            <el-option label="待审核" :value="0" />
            <el-option label="已通过" :value="1" />
            <el-option label="已拒绝" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadApplications">搜索</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="applications" v-loading="loading">
        <el-table-column label="宠物" width="150">
          <template #default="{ row }">
            <div class="pet-cell">
              <el-avatar :src="row.petImageUrl" shape="square" :size="40" />
              <span>{{ row.petName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申请人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="140" />
        <el-table-column prop="reason" label="领养理由" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="申请时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="showDetail(row)">详情</el-button>
            <template v-if="row.status === 0">
              <el-button link type="success" @click="handleApprove(row)">通过</el-button>
              <el-button link type="danger" @click="handleReject(row)">拒绝</el-button>
            </template>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="filter.pageNum"
        v-model:page-size="filter.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadApplications"
        @current-change="loadApplications"
        style="margin-top: 20px;"
      />
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="申请详情" width="600px">
      <el-descriptions :column="2" border v-if="currentApp">
        <el-descriptions-item label="宠物名称">{{ currentApp.petName }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ currentApp.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentApp.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="联系地址">{{ currentApp.contactAddress || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="居住条件" :span="2">{{ currentApp.livingCondition || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="养宠经验" :span="2">{{ currentApp.experience || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="领养理由" :span="2">{{ currentApp.reason }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentApp.status)">{{ getStatusText(currentApp.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ formatDate(currentApp.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="审核人" v-if="currentApp.reviewerName">{{ currentApp.reviewerName }}</el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="currentApp.reviewTime">{{ formatDate(currentApp.reviewTime) }}</el-descriptions-item>
        <el-descriptions-item label="拒绝原因" :span="2" v-if="currentApp.status === 2">
          {{ currentApp.rejectReason || '无' }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer v-if="currentApp?.status === 0">
        <el-button type="success" @click="handleApprove(currentApp)">通过</el-button>
        <el-button type="danger" @click="handleReject(currentApp)">拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getApplicationList, approveApplication, rejectApplication } from '@/api/application'
import { ElMessage, ElMessageBox } from 'element-plus'

const applications = ref([])
const loading = ref(false)
const total = ref(0)
const detailVisible = ref(false)
const currentApp = ref(null)

const filter = reactive({
  pageNum: 1,
  pageSize: 10,
  status: null
})

onMounted(() => {
  loadApplications()
})

async function loadApplications() {
  loading.value = true
  try {
    const res = await getApplicationList(filter)
    if (res.code === 200) {
      applications.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function getStatusText(status) {
  const map = { 0: '待审核', 1: '已通过', 2: '已拒绝', 3: '已取消' }
  return map[status] || '未知'
}

function getStatusType(status) {
  const map = { 0: 'warning', 1: 'success', 2: 'danger', 3: 'info' }
  return map[status] || 'info'
}

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleString()
}

function showDetail(row) {
  currentApp.value = row
  detailVisible.value = true
}

async function handleApprove(row) {
  await ElMessageBox.confirm('确定要通过该领养申请吗？', '提示', { type: 'success' })
  const res = await approveApplication(row.id)
  if (res.code === 200) {
    ElMessage.success('已通过')
    detailVisible.value = false
    loadApplications()
  } else {
    ElMessage.error(res.message)
  }
}

async function handleReject(row) {
  const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝申请', {
    inputPlaceholder: '选填'
  })
  const res = await rejectApplication(row.id, value)
  if (res.code === 200) {
    ElMessage.success('已拒绝')
    detailVisible.value = false
    loadApplications()
  } else {
    ElMessage.error(res.message)
  }
}
</script>

<style scoped>
.filter-form {
  margin-bottom: 20px;
}

.pet-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
