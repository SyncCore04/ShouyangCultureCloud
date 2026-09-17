<template>
  <div class="pavilion-detail-page">
    <div class="page-container">
      <div class="breadcrumb-bar">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/culture/pavilion' }">数字展馆</el-breadcrumb-item>
          <el-breadcrumb-item>详情</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div v-if="loading" class="detail-skeleton"><el-skeleton :rows="15" animated /></div>

      <template v-else-if="pavilion">
        <!-- 顶部大图 -->
        <div class="detail-hero">
          <el-image :src="pavilion.coverImage" fit="cover" class="hero-img" />
          <div class="hero-overlay">
            <h1 class="hero-title">{{ pavilion.name }}</h1>
            <p class="hero-views"><el-icon><View /></el-icon>{{ pavilion.viewCount || 0 }} 次浏览</p>
          </div>
        </div>

        <!-- 内容区 -->
        <div class="detail-content">
          <div class="content-card">
            <h3 class="content-title"><el-icon color="#2c3e6b"><InfoFilled /></el-icon>展馆简介</h3>
            <p class="intro-text">{{ pavilion.description }}</p>
          </div>

          <div class="content-card">
            <h3 class="content-title"><el-icon color="#2c3e6b"><Document /></el-icon>详细介绍</h3>
            <div class="article-content" v-html="pavilion.content || '<p>暂无详细介绍</p>'"></div>
          </div>

          <el-button type="primary" plain @click="$router.back()">
            <el-icon><ArrowLeft /></el-icon>返回展馆列表
          </el-button>
        </div>
      </template>

      <el-empty v-else description="展馆不存在" class="empty-state" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { View, InfoFilled, Document, ArrowLeft } from '@element-plus/icons-vue'
import { getPavilionDetail } from '@/api/culture'

const route = useRoute()
const loading = ref(true)
const pavilion = ref(null)

const loadDetail = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getPavilionDetail(id)
    pavilion.value = res.data
  } catch (e) {
    console.error('加载展馆详情失败:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => { loadDetail() })
</script>

<style lang="scss" scoped>
.pavilion-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
}
.page-container { max-width: 1200px; margin: 0 auto; padding: 24px 20px; }
.breadcrumb-bar { margin-bottom: 20px;
  :deep(.el-breadcrumb__inner) { color: #78909c; font-size: 13px; }
  :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) { color: #2c3e6b; font-weight: 500; }
}
.detail-hero {
  position: relative;
  width: 100%;
  height: 380px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
}
.hero-img { width: 100%; height: 100%; }
.hero-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.7), transparent 60%);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 36px;
}
.hero-title { font-size: 32px; font-weight: 700; color: #fff; margin: 0 0 10px; text-shadow: 0 2px 8px rgba(0,0,0,0.3); }
.hero-views { color: rgba(255,255,255,0.85); font-size: 14px; display: flex; align-items: center; gap: 6px; }

.detail-content { background: #fff; border-radius: 12px; padding: 30px; }
.content-card { margin-bottom: 32px; }
.content-title { font-size: 18px; font-weight: 600; color: #1a2a4a; margin: 0 0 16px; display: flex; align-items: center; gap: 8px; }
.intro-text { font-size: 15px; line-height: 1.9; color: #455a64; margin: 0; text-indent: 2em; }

.article-content {
  font-size: 15px;
  line-height: 1.9;
  color: #37474f;
  word-break: break-word;
  :deep(h1), :deep(h2), :deep(h3) { color: #1a2a4a; margin: 24px 0 12px; font-weight: 600; }
  :deep(p) { margin: 0 0 16px; text-indent: 2em; }
  :deep(img) { max-width: 100%; height: auto; border-radius: 8px; margin: 16px auto; display: block; }
  :deep(ul), :deep(ol) { margin: 0 0 16px; padding-left: 2em; }
}
.detail-skeleton { background: #fff; border-radius: 12px; padding: 30px; }
.empty-state { background: #fff; border-radius: 12px; padding: 80px 0; }

@media (max-width: 768px) {
  .detail-hero { height: 240px; }
  .hero-title { font-size: 22px; }
  .hero-overlay { padding: 20px; }
}
</style>
