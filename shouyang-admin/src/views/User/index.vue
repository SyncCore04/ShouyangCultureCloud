<template>
  <div class="user-admin-page">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="用户名/昵称"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="全部状态"
            clearable
            style="width: 120px"
          >
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        <el-table-column label="头像" width="80" align="center">
          <template #default="{ row }">
            <el-avatar :size="36" :src="row.avatar">
              {{ row.nickname ? row.nickname.charAt(0) : row.username.charAt(0) }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" width="120" align="center" />
        <el-table-column prop="nickname" label="昵称" width="120" align="center" />
        <el-table-column prop="phone" label="手机号" width="130" align="center">
          <template #default="{ row }">
            {{ row.phone || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="性别" width="70" align="center">
          <template #default="{ row }">
            {{ genderText(row.gender) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              active-color="#67c23a"
              inactive-color="#dcdfe6"
              @change="(val) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="170" align="center">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="warning" size="small" text @click="handleResetPassword(row)">
              <el-icon><Key /></el-icon>
              重置密码
            </el-button>
            <el-button type="danger" size="small" text @click="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadList"
          @current-change="loadList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Key, Delete } from '@element-plus/icons-vue'
import { getUserList, updateUserStatus, deleteUser, resetUserPassword } from '@/api/adminUser'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: null
})

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 表格数据
const loading = ref(false)
const tableData = ref([])

// 加载列表
const loadList = async () => {
  loading.value = true
  try {
    const res = await getUserList({
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      status: searchForm.status
    })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e) {
    console.error('加载用户列表失败:', e)
  } finally {
    loading.value = false
  }
}

// 搜索/重置
const handleSearch = () => {
  pagination.page = 1
  loadList()
}

const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = null
  pagination.page = 1
  loadList()
}

// 状态切换
const handleStatusChange = async (row, val) => {
  const newStatus = val ? 1 : 0
  try {
    await updateUserStatus(row.id, newStatus)
    row.status = newStatus
    ElMessage.success(val ? '已启用' : '已禁用')
  } catch (e) {
    console.error('状态修改失败:', e)
    // 回滚开关状态
    row.status = val ? 0 : 1
  }
}

// 重置密码
const handleResetPassword = (row) => {
  ElMessageBox.confirm(
    `确定要重置用户「${row.nickname || row.username}」的密码为 123456 吗？`,
    '重置密码确认',
    {
      confirmButtonText: '确定重置',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await resetUserPassword(row.id)
      ElMessage.success('密码已重置为 123456')
    } catch (e) {
      console.error('重置密码失败:', e)
    }
  }).catch(() => {})
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除用户「${row.nickname || row.username}」吗？删除后不可恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      loadList()
    } catch (e) {
      console.error('删除失败:', e)
    }
  }).catch(() => {})
}

// 工具方法
const genderText = (gender) => {
  if (gender === 1) return '男'
  if (gender === 2) return '女'
  return '-'
}

const formatTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 16)
}

// 初始化
onMounted(() => {
  loadList()
})
</script>

<style lang="scss" scoped>
.user-admin-page {
  padding: 16px;
}
.search-card {
  margin-bottom: 16px;
  :deep(.el-card__body) {
    padding-bottom: 0;
  }
}
.search-form {
  display: flex;
  flex-wrap: wrap;
}
.table-card {
  :deep(.el-card__body) {
    padding: 0;
  }
}
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  padding: 16px;
}
</style>
