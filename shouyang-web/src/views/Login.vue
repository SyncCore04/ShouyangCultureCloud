<template>
  <div class="login-page">
    <div class="login-container">
      <!-- 左侧品牌区 -->
      <div class="login-brand">
        <div class="brand-content">
          <el-icon :size="48" color="#fff"><Picture /></el-icon>
          <h1 class="brand-title">寿阳文旅云</h1>
          <p class="brand-subtitle">公共文旅服务平台</p>
          <div class="brand-features">
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>最新文旅资讯</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>在线活动报名</span>
            </div>
            <div class="feature-item">
              <el-icon><Check /></el-icon>
              <span>数字文化资源</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧登录表单 -->
      <div class="login-form-wrap">
        <h2 class="form-title">用户登录</h2>
        <p class="form-subtitle">欢迎回来，请登录您的账号</p>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="loginForm.remember">记住我</el-checkbox>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              class="login-btn"
              :loading="loading"
              @click="handleLogin"
            >
              登 录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="form-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>

        <div class="back-home">
          <router-link to="/">
            <el-icon><ArrowLeft /></el-icon>
            返回首页
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

async function handleLogin() {
  if (!loginFormRef.value) return

  try {
    await loginFormRef.value.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    await userStore.login({
      username: loginForm.username,
      password: loginForm.password
    })

    ElMessage.success('登录成功')

    // 跳转到回跳地址或首页
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (error) {
    // 错误已在 request 拦截器中提示
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, $primary-dark 0%, $primary-color 50%, $primary-light 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  width: 900px;
  max-width: 100%;
  background: #fff;
  border-radius: $border-radius-lg;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  display: flex;
  min-height: 560px;
}

// 左侧品牌区
.login-brand {
  flex: 1;
  background: linear-gradient(135deg, $primary-dark 0%, $primary-color 100%);
  padding: 50px 40px;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    width: 300px;
    height: 300px;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 50%;
    top: -100px;
    right: -100px;
  }

  &::after {
    content: '';
    position: absolute;
    width: 200px;
    height: 200px;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 50%;
    bottom: -50px;
    left: -50px;
  }
}

.brand-content {
  position: relative;
  z-index: 1;
  color: #fff;

  .brand-title {
    font-size: 32px;
    font-weight: bold;
    margin: 16px 0 8px;
    letter-spacing: 2px;
  }

  .brand-subtitle {
    font-size: 16px;
    opacity: 0.8;
    margin-bottom: 40px;
  }
}

.brand-features {
  .feature-item {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 16px;
    font-size: 14px;
    opacity: 0.9;

    .el-icon {
      background: rgba(255, 255, 255, 0.2);
      padding: 4px;
      border-radius: 50%;
    }
  }
}

// 右侧表单区
.login-form-wrap {
  flex: 1;
  padding: 50px 45px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-title {
  font-size: 26px;
  font-weight: bold;
  color: $text-primary;
  margin-bottom: 8px;
}

.form-subtitle {
  font-size: 14px;
  color: $text-secondary;
  margin-bottom: 30px;
}

.login-form {
  .form-options {
    display: flex;
    justify-content: flex-end;
    width: 100%;
  }
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  letter-spacing: 4px;
}

.form-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: $text-secondary;

  .register-link {
    color: $primary-color;
    font-weight: 500;
    margin-left: 4px;

    &:hover {
      text-decoration: underline;
    }
  }
}

.back-home {
  text-align: center;
  margin-top: 16px;

  a {
    font-size: 13px;
    color: $text-placeholder;
    display: inline-flex;
    align-items: center;
    gap: 4px;

    &:hover {
      color: $primary-color;
    }
  }
}
</style>
