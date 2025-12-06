<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #67c23a;">
            <el-icon><House /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.availablePets || 0 }}</div>
            <div class="stat-label">待领养宠物</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #409eff;">
            <el-icon><Check /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.adoptedPets || 0 }}</div>
            <div class="stat-label">已领养宠物</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #e6a23c;">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.pendingApplications || 0 }}</div>
            <div class="stat-label">待审核申请</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: #909399;">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ statistics.totalUsers || 0 }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>待审核申请</span>
              <el-button link type="primary" @click="$router.push('/admin/applications')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="pendingApplications" max-height="300">
            <el-table-column prop="petName" label="宠物" />
            <el-table-column prop="applicantName" label="申请人" />
            <el-table-column prop="createTime" label="申请时间">
              <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!pendingApplications.length" description="暂无待审核申请" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最新发布宠物</span>
              <el-button link type="primary" @click="$router.push('/admin/pets')">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentPets" max-height="300">
            <el-table-column prop="name" label="名称" />
            <el-table-column prop="categoryName" label="分类" />
            <el-table-column label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!recentPets.length" description="暂无宠物" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStatistics } from '@/api/user'
import { getApplicationList } from '@/api/application'
import { getPetList } from '@/api/pet'

const statistics = ref({})
const pendingApplications = ref([])
const recentPets = ref([])

onMounted(async () => {
  const statRes = await getStatistics()
  if (statRes.code === 200) {
    statistics.value = statRes.data
  }

  const appRes = await getApplicationList({ pageNum: 1, pageSize: 5, status: 0 })
  if (appRes.code === 200) {
    pendingApplications.value = appRes.data.records
  }

  const petRes = await getPetList({ pageNum: 1, pageSize: 5 })
  if (petRes.code === 200) {
    recentPets.value = petRes.data.records
  }
})

function formatDate(dateStr) {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString()
}

function getStatusText(status) {
  const map = { 0: '待领养', 1: '申请中', 2: '已领养', 3: '已下架' }
  return map[status] || '未知'
}

function getStatusType(status) {
  const map = { 0: 'success', 1: 'warning', 2: 'info', 3: 'danger' }
  return map[status] || 'info'
}
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  margin-right: 16px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  color: #999;
  margin-top: 4px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
