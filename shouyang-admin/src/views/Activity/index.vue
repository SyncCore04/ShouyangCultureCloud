<template>
  <div class="activity-admin-page">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入活动标题"
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
            <el-option label="未开始" :value="0" />
            <el-option label="进行中" :value="1" />
            <el-option label="已结束" :value="2" />
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

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增活动
      </el-button>
    </div>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column label="封面图" width="80" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.coverImage"
              :src="row.coverImage"
              fit="cover"
              style="width: 60px; height: 40px; border-radius: 4px"
              :preview-src-list="[row.coverImage]"
            />
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="活动标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="venue" label="活动地点" min-width="120" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.venue || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="报名人数" width="100" align="center">
          <template #default="{ row }">
            <span :style="{ color: row.maxPeople > 0 && row.signupCount >= row.maxPeople ? '#f56c6c' : '' }">
              {{ row.signupCount }}/{{ row.maxPeople || '不限' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="info" size="small" text @click="handleRegisterList(row)">
              报名记录
            </el-button>
            <el-button type="primary" size="small" text @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" size="small" text @click="handleDelete(row)">
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
          :page-sizes="[10, 20, 50]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadList"
          @current-change="loadList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑活动' : '新增活动'"
      width="800px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        class="activity-form"
      >
        <el-form-item label="活动标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入活动标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <UploadImage v-model="form.coverImage" placeholder="上传封面图" />
        </el-form-item>
        <el-form-item label="活动地点" prop="venue">
          <el-input v-model="form.venue" placeholder="请输入活动地点" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="报名截止" prop="signupDeadline">
          <el-date-picker
            v-model="form.signupDeadline"
            type="datetime"
            placeholder="选择报名截止时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="人数上限">
          <el-input-number v-model="form.maxPeople" :min="0" :max="9999" />
          <span style="margin-left: 8px; color: #909399; font-size: 12px">0 表示不限人数</span>
        </el-form-item>
        <el-form-item label="活动详情" prop="description">
          <RichEditor v-model="form.description" :height="300" placeholder="请输入活动详情..." />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">未开始</el-radio>
            <el-radio :value="1">进行中</el-radio>
            <el-radio :value="2">已结束</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 报名记录弹窗 -->
    <el-dialog
      v-model="registerDialogVisible"
      :title="`报名记录 - ${currentActivityTitle}`"
      width="700px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      destroy-on-close
    >
      <el-table :data="registerList" v-loading="registerLoading" stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="name" label="姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.remark || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报名时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
      </el-table>
      <div class="register-pagination">
        <el-pagination
          small
          v-model:current-page="registerPage.page"
          v-model:page-size="registerPage.size"
          :page-sizes="[10, 20]"
          :total="registerPage.total"
          layout="total, prev, pager, next"
          @size-change="loadRegisterList"
          @current-change="loadRegisterList"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import UploadImage from '@/components/UploadImage.vue'
import RichEditor from '@/components/RichEditor.vue'
import { get, post, put, del } from '@/utils/request'

// ========== 搜索表单 ==========
const searchForm = reactive({
  keyword: '',
  status: null
})

// ========== 分页 ==========
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// ========== 表格数据 ==========
const loading = ref(false)
const tableData = ref([])

// ========== 新增/编辑弹窗 ==========
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  title: '',
  coverImage: '',
  venue: '',
  startTime: '',
  endTime: '',
  signupDeadline: '',
  maxPeople: 0,
  description: '',
  status: 0
})

const validateEndTime = (rule, value, callback) => {
  if (!value || !form.startTime) {
    callback()
    return
  }
  if (new Date(value) <= new Date(form.startTime)) {
    callback(new Error('结束时间必须晚于开始时间'))
  } else {
    callback()
  }
}

const validateSignupDeadline = (rule, value, callback) => {
  if (!value || !form.endTime) {
    callback()
    return
  }
  if (new Date(value) > new Date(form.endTime)) {
    callback(new Error('报名截止时间不能晚于结束时间'))
  } else {
    callback()
  }
}

const rules = {
  title: [{ required: true, message: '请输入活动标题', trigger: 'blur' }],
  coverImage: [{ required: true, message: '请上传封面图', trigger: 'change' }],
  venue: [{ required: true, message: '请输入活动地点', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, validator: validateEndTime, trigger: 'change' }],
  signupDeadline: [{ required: true, validator: validateSignupDeadline, trigger: 'change' }]
}

// ========== 报名记录弹窗 ==========
const registerDialogVisible = ref(false)
const registerLoading = ref(false)
const registerList = ref([])
const currentActivityId = ref(null)
const currentActivityTitle = ref('')
const registerPage = reactive({
  page: 1,
  size: 10,
  total: 0
})

// ========== 加载列表 ==========
const loadList = async () => {
  loading.value = true
  try {
    const res = await get('/admin/activity/list', {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      status: searchForm.status
    })
    tableData.value = res.data?.records || []
    pagination.total = res.data?.total || 0
  } catch (e) {
    console.error('加载列表失败:', e)
  } finally {
    loading.value = false
  }
}

// ========== 加载报名记录 ==========
const loadRegisterList = async () => {
  if (!currentActivityId.value) return
  registerLoading.value = true
  try {
    const res = await get('/admin/activity/register/list', {
      activityId: currentActivityId.value,
      page: registerPage.page,
      size: registerPage.size
    })
    registerList.value = res.data?.records || []
    registerPage.total = res.data?.total || 0
  } catch (e) {
    console.error('加载报名记录失败:', e)
  } finally {
    registerLoading.value = false
  }
}

// ========== 搜索/重置 ==========
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

// ========== 新增/编辑 ==========
const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    title: '',
    coverImage: '',
    venue: '',
    startTime: '',
    endTime: '',
    signupDeadline: '',
    maxPeople: 0,
    description: '',
    status: 0
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    title: row.title || '',
    coverImage: row.coverImage || '',
    venue: row.venue || '',
    startTime: row.startTime || '',
    endTime: row.endTime || '',
    signupDeadline: row.signupDeadline || '',
    maxPeople: row.maxPeople ?? 0,
    description: row.description || '',
    status: row.status ?? 0
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitting.value = true
    if (isEdit.value) {
      await put('/admin/activity', form)
      ElMessage.success('修改成功')
    } else {
      await post('/admin/activity', form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadList()
  } catch (e) {
    if (e !== false) {
      console.error('提交失败:', e)
    }
  } finally {
    submitting.value = false
  }
}

// ========== 删除 ==========
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除该活动吗？删除后不可恢复。`, '确认删除', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await del(`/admin/activity/${row.id}`)
      ElMessage.success('删除成功')
      loadList()
    } catch (e) {
      console.error('删除失败:', e)
    }
  }).catch(() => {})
}

// ========== 报名记录 ==========
const handleRegisterList = (row) => {
  currentActivityId.value = row.id
  currentActivityTitle.value = row.title
  registerPage.page = 1
  registerPage.total = 0
  registerDialogVisible.value = true
  loadRegisterList()
}

// ========== 工具方法 ==========
const getStatusText = (status) => {
  const map = { 0: '未开始', 1: '进行中', 2: '已结束' }
  return map[status] || '未知'
}

const getStatusTagType = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'warning' }
  return map[status] || 'info'
}

const formatTime = (time) => {
  if (!time) return '-'
  return time.replace('T', ' ').substring(0, 16)
}

// ========== 初始化 ==========
onMounted(() => {
  loadList()
})
</script>

<style lang="scss" scoped>
.activity-admin-page {
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
.action-bar {
  margin-bottom: 16px;
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
.activity-form {
  max-height: 65vh;
  overflow-y: auto;
  padding-right: 8px;
}
.register-pagination {
  display: flex;
  justify-content: flex-end;
  padding-top: 16px;
}
</style>
