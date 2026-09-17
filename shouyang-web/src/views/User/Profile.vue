<template>
  <div class="profile-page">
    <div class="content-card">
      <h3 class="card-title">个人信息</h3>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="profile-form"
      >
        <!-- 头像 -->
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :on-success="handleAvatarSuccess"
            accept="image/*"
          >
            <el-avatar :size="100" :src="form.avatar || defaultAvatar" class="avatar-preview" />
            <div class="upload-tip">点击更换头像</div>
          </el-upload>
        </el-form-item>

        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="form.nickname" placeholder="请输入昵称" maxlength="20" show-word-limit />
        </el-form-item>

        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio value="男">男</el-radio>
            <el-radio value="女">女</el-radio>
            <el-radio value="保密">保密</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getUserInfo, updateUserInfo, uploadAvatar } from '@/api/user'

const userStore = useUserStore()
const formRef = ref(null)
const saving = ref(false)
const defaultAvatar = 'https://picsum.photos/seed/default-avatar/200/200'
const uploadUrl = '/api/admin/upload/image'
const uploadHeaders = {
  Authorization: `Bearer ${userStore.token || ''}`
}

const form = reactive({
  nickname: '',
  avatar: '',
  phone: '',
  email: '',
  gender: '保密'
})

const rules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const loadUserInfo = async () => {
  try {
    const res = await getUserInfo()
    const user = res.data
    if (user) {
      form.nickname = user.nickname || ''
      form.avatar = user.avatar || ''
      form.phone = user.phone || ''
      form.email = user.email || ''
      form.gender = user.gender || '保密'
    }
  } catch (e) {
    console.error('加载用户信息失败:', e)
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt5M = file.size / 1024 / 1024 < 5
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (!isLt5M) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

const handleAvatarSuccess = (response) => {
  if (response.code === 200 && response.data) {
    form.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const handleSave = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    saving.value = true
    await updateUserInfo({
      nickname: form.nickname,
      avatar: form.avatar,
      phone: form.phone,
      email: form.email,
      gender: form.gender
    })
    ElMessage.success('保存成功')
    // 更新 store 中的用户信息
    userStore.fetchUserInfo()
  } catch (e) {
    if (e !== false) {
      console.error('保存失败:', e)
    }
  } finally {
    saving.value = false
  }
}

const resetForm = () => {
  loadUserInfo()
}

onMounted(() => { loadUserInfo() })
</script>

<style lang="scss" scoped>
.profile-page { min-height: 100%; }
.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 28px 32px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.card-title { font-size: 18px; font-weight: 600; color: #1a2a4a; margin: 0 0 24px; padding-bottom: 16px; border-bottom: 1px solid #f0f2f5; }
.profile-form { max-width: 500px; }

.avatar-uploader {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}
.avatar-preview { border: 2px solid #e8eef7; }
.upload-tip { font-size: 12px; color: #90a4ae; margin-top: 8px; }
</style>
