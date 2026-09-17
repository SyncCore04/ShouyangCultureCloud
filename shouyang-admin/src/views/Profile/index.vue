<template>
  <div class="profile-page">
    <el-row :gutter="16">
      <!-- 管理员信息 -->
      <el-col :span="10">
        <el-card class="info-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">管理员信息</span>
            </div>
          </template>
          <div class="admin-info" v-loading="loading">
            <div class="avatar-section">
              <el-avatar :size="80" :src="adminInfo.avatar">
                {{ adminInfo.nickname ? adminInfo.nickname.charAt(0) : 'A' }}
              </el-avatar>
              <div class="admin-name">{{ adminInfo.nickname || adminInfo.username }}</div>
              <el-tag v-if="adminInfo.role === 'super'" type="danger" effect="plain" size="small">超级管理员</el-tag>
              <el-tag v-else type="primary" effect="plain" size="small">管理员</el-tag>
            </div>
            <el-descriptions :column="1" border class="info-descriptions">
              <el-descriptions-item label="用户名">{{ adminInfo.username || '-' }}</el-descriptions-item>
              <el-descriptions-item label="昵称">{{ adminInfo.nickname || '-' }}</el-descriptions-item>
              <el-descriptions-item label="角色">{{ roleText(adminInfo.role) }}</el-descriptions-item>
              <el-descriptions-item label="账号ID">{{ adminInfo.id || '-' }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>

      <!-- 修改密码 -->
      <el-col :span="14">
        <el-card class="password-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span class="card-title">修改密码</span>
            </div>
          </template>
          <el-form
            ref="passwordFormRef"
            :model="passwordForm"
            :rules="passwordRules"
            label-width="100px"
            class="password-form"
          >
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input
                v-model="passwordForm.oldPassword"
                type="password"
                placeholder="请输入旧密码"
                show-password
              />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input
                v-model="passwordForm.newPassword"
                type="password"
                placeholder="请输入新密码（6-20位）"
                show-password
              />
            </el-form-item>
            <el-form-item label="确认新密码" prop="confirmPassword">
              <el-input
                v-model="passwordForm.confirmPassword"
                type="password"
                placeholder="请再次输入新密码"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="submitting" @click="handleSubmit">
                确认修改
              </el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
          <el-alert
            title="密码修改成功后需要重新登录"
            type="info"
            :closable="false"
            show-icon
            class="tip-alert"
          />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAdminInfo, updateAdminPassword } from '@/api/adminAuth'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const passwordFormRef = ref(null)

const adminInfo = reactive({
  id: null,
  username: '',
  nickname: '',
  role: '',
  avatar: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 校验确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 加载管理员信息
const loadAdminInfo = async () => {
  loading.value = true
  try {
    const res = await getAdminInfo()
    const data = res.data
    adminInfo.id = data.id
    adminInfo.username = data.username
    adminInfo.nickname = data.nickname
    adminInfo.role = data.role
    adminInfo.avatar = data.avatar || ''
  } catch (e) {
    console.error('加载管理员信息失败:', e)
  } finally {
    loading.value = false
  }
}

// 提交修改密码
const handleSubmit = async () => {
  if (!passwordFormRef.value) return
  try {
    await passwordFormRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    await updateAdminPassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功，请重新登录')
    // 退出登录并跳转登录页
    userStore.logout()
    router.push('/login')
  } catch (e) {
    console.error('修改密码失败:', e)
  } finally {
    submitting.value = false
  }
}

// 重置表单
const handleReset = () => {
  if (passwordFormRef.value) {
    passwordFormRef.value.resetFields()
  }
}

// 角色文本
const roleText = (role) => {
  if (role === 'super') return '超级管理员'
  if (role === 'admin') return '管理员'
  return '-'
}

onMounted(() => {
  loadAdminInfo()
})
</script>

<style lang="scss" scoped>
.profile-page {
  padding: 16px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.info-card,
.password-card {
  height: 100%;
}
.admin-info {
  min-height: 300px;
}
.avatar-section {
  text-align: center;
  margin-bottom: 24px;
  .admin-name {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 12px 0 8px;
  }
}
.info-descriptions {
  margin-top: 16px;
  :deep(.el-descriptions__label) {
    width: 100px;
    background: #fafafa;
  }
}
.password-form {
  max-width: 500px;
  margin: 0 auto;
}
.tip-alert {
  margin-top: 16px;
}
</style>
