<template>
  <div class="banner-admin-page">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入标题"
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
            <el-option label="启用" :value="1" />
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

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增轮播图
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
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="linkUrl" label="跳转链接" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.linkUrl" style="color: #409eff">{{ row.linkUrl }}</span>
            <span v-else style="color: #c0c4cc">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" align="center" />
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
      :title="isEdit ? '编辑轮播图' : '新增轮播图'"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="banner-form"
      >
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="50" show-word-limit />
        </el-form-item>
        <el-form-item label="封面图" prop="coverImage">
          <UploadImage v-model="form.coverImage" placeholder="上传封面图，建议尺寸 1920x600" />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.linkUrl" placeholder="如 /news 或 https://..." />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
          <span style="margin-left: 8px; color: #909399; font-size: 12px">数字越小越靠前</span>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">禁用</el-radio>
            <el-radio :value="1">启用</el-radio>
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
  title: '',
  coverImage: '',
  linkUrl: '',
  sort: 0,
  status: 1
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  coverImage: [{ required: true, message: '请上传封面图', trigger: 'change' }]
}

// ========== 加载列表 ==========
const loadList = async () => {
  loading.value = true
  try {
    const res = await get('/admin/banner/list', {
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
    title: '',
    coverImage: '',
    linkUrl: '',
    sort: 0,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  isEdit.value = true
  try {
    const res = await get(`/admin/banner/${row.id}`)
    const data = res.data || row
    Object.assign(form, {
      id: data.id,
      title: data.title || '',
      coverImage: data.coverImage || '',
      linkUrl: data.linkUrl || '',
      sort: data.sort ?? 0,
      status: data.status ?? 1
    })
    dialogVisible.value = true
  } catch (e) {
    console.error('获取详情失败:', e)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    submitting.value = true
    if (isEdit.value) {
      await put('/admin/banner', form)
      ElMessage.success('修改成功')
    } else {
      await post('/admin/banner', form)
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
  ElMessageBox.confirm(`确认删除该轮播图吗？删除后不可恢复。`, '确认删除', {
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await del(`/admin/banner/${row.id}`)
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
    await put('/admin/banner', { id: row.id, status: newStatus })
    row.status = newStatus
    ElMessage.success(val ? '已启用' : '已禁用')
  } catch (e) {
    console.error('状态修改失败:', e)
    // 回滚
    row.status = val ? 0 : 1
  }
}

// ========== 工具方法 ==========
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
.banner-admin-page {
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
.banner-form {
  max-height: 60vh;
  overflow-y: auto;
  padding-right: 8px;
}
</style>
