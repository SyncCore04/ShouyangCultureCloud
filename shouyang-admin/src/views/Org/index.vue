<template>
  <div class="org-admin-page">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入单位名称"
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
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
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
        新增文旅单位
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
        <el-table-column prop="name" label="单位名称" min-width="160" show-overflow-tooltip />
        <el-table-column label="单位类型" width="110" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.type" :type="getTypeTagType(row.type)" size="small">
              {{ row.type }}
            </el-tag>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="130" align="center">
          <template #default="{ row }">
            {{ row.phone || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch
              :model-value="row.status === 1"
              active-color="#67c23a"
              inactive-color="#dcdfe6"
              @change="(val) => handleStatusChange(row, val)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="handleEdit(row)">
              <el-icon><Edit /></el-icon>
              编辑
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
      :title="isEdit ? '编辑文旅单位' : '新增文旅单位'"
      width="700px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="90px"
        class="org-form"
      >
        <el-form-item label="单位名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入单位名称" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <UploadImage v-model="form.coverImage" placeholder="上传封面图" />
        </el-form-item>
        <el-form-item label="单位类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择单位类型" style="width: 100%">
            <el-option label="政府部门" value="政府部门" />
            <el-option label="事业单位" value="事业单位" />
            <el-option label="文化企业" value="文化企业" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" placeholder="请输入单位地址" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="单位描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入简短描述"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">下架</el-radio>
            <el-radio :value="1">上架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import UploadImage from '@/components/UploadImage.vue'
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

// ========== 弹窗 ==========
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  coverImage: '',
  type: '',
  address: '',
  phone: '',
  description: '',
  sort: 0,
  status: 1
})

const rules = {
  name: [{ required: true, message: '请输入单位名称', trigger: 'blur' }],
  coverImage: [{ required: true, message: '请上传封面图', trigger: 'change' }],
  type: [{ required: true, message: '请选择单位类型', trigger: 'change' }]
}

// ========== 加载列表 ==========
const loadList = async () => {
  loading.value = true
  try {
    const res = await get('/admin/org/list', {
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
    name: '',
    coverImage: '',
    type: '',
    address: '',
    phone: '',
    description: '',
    sort: 0,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    name: row.name || '',
    coverImage: row.coverImage || '',
    type: row.type || '',
    address: row.address || '',
    phone: row.phone || '',
    description: row.description || '',
    sort: row.sort ?? 0,
    status: row.status ?? 1
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitting.value = true
    if (isEdit.value) {
      await put('/admin/org', form)
      ElMessage.success('修改成功')
    } else {
      await post('/admin/org', form)
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
  ElMessageBox.confirm(`确认删除该单位吗？删除后不可恢复。`, '确认删除', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await del(`/admin/org/${row.id}`)
      ElMessage.success('删除成功')
      loadList()
    } catch (e) {
      console.error('删除失败:', e)
    }
  }).catch(() => {})
}

// ========== 状态切换 ==========
const handleStatusChange = async (row, val) => {
  const newStatus = val ? 1 : 0
  try {
    await put('/admin/org', { id: row.id, status: newStatus })
    row.status = newStatus
    ElMessage.success(val ? '已上架' : '已下架')
  } catch (e) {
    console.error('状态修改失败:', e)
    // 回滚
    row.status = val ? 0 : 1
  }
}

// ========== 工具方法 ==========
const getTypeTagType = (type) => {
  const map = {
    '政府部门': 'danger',
    '事业单位': 'primary',
    '文化企业': 'success',
    '其他': 'info'
  }
  return map[type] || 'info'
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
.org-admin-page {
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
.org-form {
  max-height: 65vh;
  overflow-y: auto;
  padding-right: 8px;
}
</style>
