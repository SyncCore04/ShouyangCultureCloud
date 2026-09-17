<template>
  <div class="activity-page">
    <div class="content-card">
      <h3 class="card-title">我的报名</h3>

      <div v-if="loading" class="activity-list">
        <el-skeleton v-for="i in 3" :key="i" :rows="3" animated class="skeleton-item" />
      </div>

      <div v-else-if="list.length > 0" class="activity-list">
        <div v-for="item in list" :key="item.id" class="activity-item">
          <el-image :src="item.coverImage || defaultCover" fit="cover" class="item-cover" :lazy="true" @click="goDetail(item)" />
          <div class="item-info" @click="goDetail(item)">
            <h4 class="item-title">{{ item.activityName || '活动名称' }}</h4>
            <div class="item-meta">
              <span class="meta-item"><el-icon><Clock /></el-icon>{{ formatTime(item.startTime) }} ~ {{ formatTime(item.endTime) }}</span>
              <span class="meta-item"><el-icon><Location /></el-icon>{{ item.location || '待定' }}</span>
            </div>
            <div class="item-footer">
              <el-tag size="small" :type="getStatusType(item.activityStatus)">{{ getStatusLabel(item.activityStatus) }}</el-tag>
              <span class="register-time">报名时间：{{ formatTime(item.createTime) }}</span>
            </div>
          </div>
          <div class="item-actions">
            <el-button
              v-if="item.activityStatus === 0"
              type="danger"
              size="small"
              plain
              @click="handleCancel(item)"
            >
              取消报名
            </el-button>
            <el-button v-else type="info" size="small" disabled>
              {{ item.activityStatus === 1 ? '进行中' : '已结束' }}
            </el-button>
          </div>
        </div>
      </div>

      <el-empty v-else description="暂无报名记录" class="empty-state">
        <el-button type="primary" @click="$router.push('/service/activity')">去报名</el-button>
      </el-empty>

      <Pagination
        v-if="total > 0"
        v-model:page="currentPage"
        :total="total"
        :page-size="pageSize"
        @change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Clock, Location } from '@element-plus/icons-vue'
import Pagination from '@/components/Pagination.vue'
import { getMyActivityList, cancelActivity } from '@/api/user'

const router = useRouter()
const loading = ref(true)
const list = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(8)
const defaultCover = 'https://picsum.photos/seed/activity-cover/300/200'

const loadList = async () => {
  loading.value = true
  try {
    const res = await getMyActivityList({ page: currentPage.value, size: pageSize.value })
    list.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('加载报名列表失败:', e)
  } finally {
    loading.value = false
  }
}

const handlePageChange = ({ page }) => {
  currentPage.value = page
  loadList()
}

const handleCancel = async (item) => {
  try {
    await ElMessageBox.confirm('确定要取消报名吗？取消后名额将释放给其他用户。', '确认取消', {
      confirmButtonText: '确定取消',
      cancelButtonText: '再想想',
      type: 'warning'
    })
    await cancelActivity(item.id)
    ElMessage.success('已取消报名')
    loadList()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('取消报名失败:', e)
    }
  }
}

const goDetail = (item) => {
  router.push(`/service/activity/${item.activityId}`)
}

const getStatusLabel = (status) => {
  const map = { 0: '未开始', 1: '进行中', 2: '已结束' }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = { 0: 'primary', 1: 'success', 2: 'info' }
  return map[status] || 'info'
}

const formatTime = (time) => {
  if (!time) return '待定'
  return time.replace('T', ' ').substring(0, 16)
}

onMounted(() => { loadList() })
</script>

<style lang="scss" scoped>
.activity-page { min-height: 100%; }
.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 28px 32px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.card-title { font-size: 18px; font-weight: 600; color: #1a2a4a; margin: 0 0 20px; padding-bottom: 16px; border-bottom: 1px solid #f0f2f5; }

.activity-list { display: flex; flex-direction: column; gap: 16px; margin-bottom: 24px; }
.skeleton-item { padding: 16px; border: 1px solid #f0f2f5; border-radius: 10px; }

.activity-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border: 1px solid #f0f2f5;
  border-radius: 10px;
  transition: all 0.2s;
  &:hover {
    border-color: #c5d3e8;
    box-shadow: 0 4px 12px rgba(0,0,0,0.06);
  }
}
.item-cover { width: 160px; height: 110px; border-radius: 8px; flex-shrink: 0; background: #f0f2f5; cursor: pointer; }
.item-info { flex: 1; min-width: 0; cursor: pointer; }
.item-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a2a4a;
  margin: 0 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.item-meta { display: flex; flex-direction: column; gap: 6px; margin-bottom: 10px; }
.meta-item { display: flex; align-items: center; gap: 6px; font-size: 13px; color: #78909c; }
.item-footer { display: flex; align-items: center; gap: 12px; }
.register-time { font-size: 12px; color: #b0bec5; }
.item-actions { flex-shrink: 0; }
.empty-state { padding: 60px 0; }
</style>
