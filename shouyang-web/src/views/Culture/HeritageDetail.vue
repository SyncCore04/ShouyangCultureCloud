<template>
  <div class="heritage-detail-page">
    <div class="page-container">
      <div class="breadcrumb-bar">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item :to="{ path: '/culture/heritage' }">非遗文化</el-breadcrumb-item>
          <el-breadcrumb-item>详情</el-breadcrumb-item>
        </el-breadcrumb>
      </div>

      <div v-if="loading" class="detail-skeleton"><el-skeleton :rows="15" animated /></div>

      <template v-else-if="heritage">
        <div class="detail-layout">
          <!-- 左侧主内容 -->
          <div class="main-content">
            <div class="detail-cover">
              <el-image :src="heritage.coverImage" fit="cover" class="cover-img" />
            </div>

            <div class="detail-header">
              <span class="level-tag" :style="getLevelStyle(heritage.level)">{{ heritage.level }}</span>
              <h1 class="detail-title">{{ heritage.name }}</h1>
            </div>

            <div class="content-card">
              <h3 class="content-title"><el-icon color="#2c3e6b"><Document /></el-icon>详细介绍</h3>
              <div class="article-content" v-html="heritage.content || heritage.description || '<p>暂无详细介绍</p>'"></div>
            </div>
          </div>

          <!-- 右侧信息栏 -->
          <div class="detail-sidebar">
            <div class="sidebar-card">
              <h3 class="sidebar-title"><el-icon color="#2c3e6b"><InfoFilled /></el-icon>基本信息</h3>
              <div class="info-list">
                <div class="info-item">
                  <span class="info-label">项目名称</span>
                  <span class="info-value">{{ heritage.name }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">非遗级别</span>
                  <span class="info-value" :style="{ color: getLevelStyle(heritage.level).color }">
                    {{ heritage.level }}
                  </span>
                </div>
                <div class="info-item">
                  <span class="info-label">所属类别</span>
                  <span class="info-value">{{ heritage.category }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">代表性传承人</span>
                  <span class="info-value">{{ heritage.inheritor || '暂无' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">浏览量</span>
                  <span class="info-value">{{ heritage.viewCount || 0 }} 次</span>
                </div>
              </div>
            </div>

            <div class="sidebar-card">
              <el-button type="primary" plain @click="$router.back()" class="back-btn">
                <el-icon><ArrowLeft /></el-icon>返回非遗列表
              </el-button>
            </div>
          </div>
        </div>
      </template>

      <el-empty v-else description="非遗项目不存在" class="empty-state" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { InfoFilled, Document, ArrowLeft } from '@element-plus/icons-vue'
import { getHeritageDetail, getLevelStyle } from '@/api/culture'

const route = useRoute()
const loading = ref(true)
const heritage = ref(null)

const loadDetail = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getHeritageDetail(id)
    heritage.value = res.data
  } catch (e) {
    console.error('加载非遗详情失败:', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => { loadDetail() })
</script>

<style lang="scss" scoped>
.heritage-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
  padding-bottom: 40px;
}
.page-container { max-width: 1200px; margin: 0 auto; padding: 24px 20px; }
.breadcrumb-bar { margin-bottom: 20px;
  :deep(.el-breadcrumb__inner) { color: #78909c; font-size: 13px; }
  :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) { color: #2c3e6b; font-weight: 500; }
}

.detail-layout { display: flex; gap: 24px; align-items: flex-start; }
.main-content { flex: 1; min-width: 0; }

.detail-cover {
  width: 100%;
  height: 360px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
}
.cover-img { width: 100%; height: 100%; }

.detail-header {
  background: #fff;
  border-radius: 12px;
  padding: 24px 28px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 14px;
}
.level-tag {
  padding: 6px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  flex-shrink: 0;
}
.detail-title { font-size: 26px; font-weight: 700; color: #1a2a4a; margin: 0; }

.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 28px 30px;
  margin-bottom: 24px;
}
.content-title { font-size: 18px; font-weight: 600; color: #1a2a4a; margin: 0 0 20px; display: flex; align-items: center; gap: 8px; }

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

.detail-sidebar { width: 300px; flex-shrink: 0; display: flex; flex-direction: column; gap: 20px; position: sticky; top: 20px; }
.sidebar-card { background: #fff; border-radius: 12px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
.sidebar-title { font-size: 16px; font-weight: 600; color: #1a2a4a; margin: 0 0 18px; display: flex; align-items: center; gap: 8px; }

.info-list { display: flex; flex-direction: column; }
.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f2f5;
  &:last-child { border-bottom: none; }
}
.info-label { font-size: 13px; color: #78909c; }
.info-value { font-size: 14px; color: #1a2a4a; font-weight: 500; text-align: right; max-width: 180px; }

.back-btn { width: 100%; }
.detail-skeleton { background: #fff; border-radius: 12px; padding: 30px; }
.empty-state { background: #fff; border-radius: 12px; padding: 80px 0; }

@media (max-width: 992px) {
  .detail-layout { flex-direction: column; }
  .detail-sidebar { width: 100%; position: static; }
  .detail-cover { height: 240px; }
  .detail-title { font-size: 22px; }
}
</style>
